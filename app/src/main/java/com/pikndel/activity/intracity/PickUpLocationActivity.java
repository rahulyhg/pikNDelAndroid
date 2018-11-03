package com.pikndel.activity.intracity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pikndel.R;
import com.pikndel.activity.IntraCityDeliveryActivity;
import com.pikndel.fragment.intracityfragment.FavoriteLocationFragment;
import com.pikndel.listeners.GetFragmentData;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.Terms;
import com.pikndel.services.GooglePlace;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.services.WebServiceAsync;
import com.pikndel.services.ZipCodeGooglePlace;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PickUpLocationActivity extends AppCompatActivity implements GetFragmentData{

    private ImageView ivLeft, ivRight;
    private TextView tvGoogle,tvFavourites, tvHeader;
    private FrameLayout flContainer;
    private EditText etLocation;
    private RecyclerView rvGoogle;
    private LinearLayout llGoogle;
    private Context context = PickUpLocationActivity.this;
    public List<GooglePlace> predictions = new ArrayList<>();
    public List<ZipCodeGooglePlace> address_components = new ArrayList<>();
    private SearchAdapter searchListAdapter;
    private String cityName = "";
    private List<Terms> terms = new ArrayList<>();
    private PrefsManager prefsManager;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_location);
        CommonUtils.hideSoftKeyboard(context);
        prefsManager = new PrefsManager(context);
        if (getIntent() != null){

            cityName = getIntent().getStringExtra("CITY_INFO");
        }


//        callGoogleAutoCompleteAPIZipCode("city");
        findIds();
        setListeners();
        setTextAttributes();
        settingFragment(0);

        searchListAdapter = new SearchAdapter(context, predictions, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Intent intent = new Intent();
                intent.putExtra("PICK_UP_CITY", predictions.get(position).description);
                intent.putExtra("PICK_UP_REF", predictions.get(position).reference);
                intent.putExtra("SEARCH_DELIVERY", (Serializable) predictions.get(position).terms);
                intent.putExtra("IS_TRIM", false);
                setResult(IntraCityDeliveryActivity.RC_PICK_UP, intent);
                finish();
            }
        });

        rvGoogle.setAdapter(searchListAdapter);

        latLng = Utils.getLocationFromAddress(this, cityName);
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
                bundle.putString("SECTION", "PickUpLocationActivity");
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, true, PickUpLocationActivity.this, flContainer);
                break;
        }
    }



    private void setListeners() {
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                    CommonUtils.showToast(context, "Register or login for favourite.");
                } else {
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
                if (s.toString().length() > 0) {
                    callGoogleAutoCompleteAPI(s.toString());
                } else {
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
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvGoogle.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvFavourites.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
        tvHeader.setText("Pick-up Location");
    }

    private void findIds() {
        flContainer=(FrameLayout) findViewById(R.id.flContainer);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        tvGoogle=(TextView) findViewById(R.id.tvGoogle);
        etLocation=(EditText) findViewById(R.id.etLocation);
        tvFavourites=(TextView) findViewById(R.id.tvFavourites);
        llGoogle=(LinearLayout) findViewById(R.id.llGoogle);
        rvGoogle=(RecyclerView) findViewById(R.id.rvGoogle);
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
                                    terms.clear();
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
                                                terms = response.predictions.get(i).terms;
                                            }
                                        }else {
                                            if (predictionCity.contains(cityName.toLowerCase())){
                                                predictions.add(response.predictions.get(i));
                                                terms = response.predictions.get(i).terms;
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

    @Override
    public void onGetFragmentData(String data, boolean value) {
        Intent intent = new Intent();
        intent.putExtra("PICK_UP_CITY", data);
        intent.putExtra("IS_TRIM", value);
        setResult(IntraCityDeliveryActivity.RC_PICK_UP, intent);
        finish();
    }

    @Override
    public void onGetFragmentData(String data, String refData, boolean value) {

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
