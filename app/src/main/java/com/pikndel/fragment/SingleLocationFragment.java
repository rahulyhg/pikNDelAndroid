package com.pikndel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pikndel.R;
import com.pikndel.activity.intracity.DeliveryLocationActivity;
import com.pikndel.fragment.intracityfragment.FavoriteLocationFragment;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.services.GooglePlace;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.services.WebServiceAsync;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujeet on 30-04-2016.
 */
public class SingleLocationFragment extends Fragment {

    private TextView tvGoogle, tvFavourites;
    private FrameLayout flContainer;
    private EditText etLocation;
    private RecyclerView rvGoogle;
    private LinearLayout llGoogle;
    private Context context;
    public List<GooglePlace> predictions = new ArrayList<>();
    private SearchAdapter searchListAdapter;
    private View view;
    private String cityName = "";
    private PrefsManager prefsManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.single_location_fragment_layout, container, false);
        CommonUtils.hideSoftKeyboard(context);
        prefsManager = new PrefsManager(context);
        if (getArguments() != null){
            cityName = getArguments().getString("CITY_INFO");
        }

        findIds();
        setListeners();
        setTextAttributes();
        settingFragment(0);

        searchListAdapter = new SearchAdapter(context, predictions, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                ((DeliveryLocationActivity)context).onGetFragmentData(predictions.get(position).description, predictions.get(position).reference, false);
            }
        });

        rvGoogle.setAdapter(searchListAdapter);
        return view;
    }

    private void settingFragment(int position) {
        llGoogle.setVisibility(View.GONE);
        flContainer.setVisibility(View.GONE);
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                llGoogle.setVisibility(View.VISIBLE);
                tvGoogle.setTextColor(getResources().getColor(R.color.white));
                tvFavourites.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvGoogle.setBackgroundResource(R.drawable.rectangle_left_rounded);
                tvFavourites.setBackgroundResource(R.drawable.white_rectangle_right_rounded);
                break;
            case 1:
                flContainer.setVisibility(View.VISIBLE);
                tvGoogle.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvFavourites.setTextColor(getResources().getColor(R.color.white));
                tvGoogle.setBackgroundResource(R.drawable.white_rectangle_left_rounded);
                tvFavourites.setBackgroundResource(R.drawable.rectangle_right_rounded);
                fragment = new FavoriteLocationFragment();
                bundle.putString("CITY_INFO", cityName);
                bundle.putString("SECTION", "DeliveryLocationActivity");
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, true, getActivity(), flContainer);
                break;
        }
    }

    private void setListeners() {

        tvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(0);
            }
        });

        tvFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")) {
                    CommonUtils.showToast(context, "Register or login to for favourite.");
                }else {
                    settingFragment(1);
                }
            }
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()>0) {
                    callGoogleAutoCompleteAPI(s.toString());
                }else {
                    predictions.clear();
                    searchListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setTextAttributes() {
        tvGoogle.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvFavourites.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void findIds() {
        flContainer=(FrameLayout) view.findViewById(R.id.flChildContainer);
        tvGoogle=(TextView) view.findViewById(R.id.tvGoogle);
        etLocation=(EditText) view.findViewById(R.id.etLocation);
        tvFavourites=(TextView) view.findViewById(R.id.tvFavourites);
        llGoogle=(LinearLayout) view.findViewById(R.id.llGoogle);
        rvGoogle=(RecyclerView) view.findViewById(R.id.rvGoogle);
        rvGoogle.setLayoutManager(new LinearLayoutManager(context));
    }

    private void callGoogleAutoCompleteAPI(String place){
        String newPlace = place.replace(" ", "%20");
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+newPlace+"&types=geocode&key="+ AppConstant.GOOGLE_KEY +"&components=country:in";

        WebServiceAsync serviceAsync = new WebServiceAsync(context, false, "", url, "GET", new ServiceStatus() {
            @Override
            public void onSuccess(Object o) {

                try {
                    ServiceResponse response = new Gson().fromJson(o.toString(),ServiceResponse.class);
                    if (response != null){
                        if (!TextUtils.isEmpty(response.status)) {
                            if (response.status.equalsIgnoreCase("OK")) {
                                if (response.predictions != null && response.predictions.size() > 0) {
                                    predictions.clear();
                                    for (int i = 0; i < response.predictions.size(); i++) {
                                        String predictionCity = TextUtils.isEmpty(response.predictions.get(i).description)?"":response.predictions.get(i).description.toLowerCase();
                                        //for Delhi/NCR case
                                        if (cityName.toLowerCase().contains("delhi")){
                                            if (predictionCity.contains("delhi") || predictionCity.contains("faridabad") ||
                                                    predictionCity.contains("ghaziabad") || predictionCity.contains("ballabgarh")||
                                                    predictionCity.contains("gurugram") || predictionCity.contains("loni")||
                                                    predictionCity.contains("greater noida") || predictionCity.contains("noida")||
                                                    predictionCity.contains("sonipat") || predictionCity.contains("bahadurgarh")||
                                                    predictionCity.contains("kundli") || predictionCity.contains("anand vihar")){
                                                predictions.add(response.predictions.get(i));
                                            }
                                        }else {
                                            if (predictionCity.contains(cityName.toLowerCase())){
                                                predictions.add(response.predictions.get(i));
                                            }
                                        }
                                    }
                                    searchListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                CommonUtils.showToast(context, "Invalid location search.");
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Object o) {

            }
        });

        serviceAsync.execute("");
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
        private Context context;
        private List<GooglePlace> predictions;
        private RecyclerItemClickListener listener;

        public SearchAdapter(Context context, List<GooglePlace> predictions, RecyclerItemClickListener listener) {
            this.context = context;
            this.predictions = predictions;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_search, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            GooglePlace googlePlace = predictions.get(position);
            holder.tv_search_result.setText(TextUtils.isEmpty(googlePlace.description)?"":googlePlace.description);
            holder.tv_search_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return predictions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_search_result;
            public ViewHolder(View itemView) {
                super(itemView);
                tv_search_result = (TextView)itemView.findViewById(R.id.tv_search_result);
                tv_search_result.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            }
        }
    }
}
