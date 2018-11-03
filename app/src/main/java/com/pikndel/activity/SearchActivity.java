package com.pikndel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pikndel.R;
import com.pikndel.fragment.DeliveryDetailsAddressFragment;
import com.pikndel.fragment.PickUpDetailsAddressFragment;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.Terms;
import com.pikndel.services.GooglePlace;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.services.WebServiceAsync;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView ivLeft, ivRight;
    private TextView tvHeader;
    private EditText etLocation;
    private RecyclerView rvGoogle;
    private Context context = SearchActivity.this;
    public List<GooglePlace> predictions = new ArrayList<>();
    private SearchAdapter searchListAdapter;
    private String cityName = "";
    private List<Terms> terms = new ArrayList<>();
    private String section = "";
    private String addFavourite = "";
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getIntent() != null){

            cityName = getIntent().getStringExtra("CITY_INFO");
            section = getIntent().getStringExtra("SECTION");
        }

        latLng = Utils.getLocationFromAddress(this, cityName);

        findIds();
        setListeners();
        setTextAttributes();

        searchListAdapter = new SearchAdapter(context, predictions, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                if (section.equalsIgnoreCase("PickUpDetailsAddressFragment")) {
                    Intent intent = new Intent();
                    intent.putExtra("PICK_UP_CITY", predictions.get(position).description);
                    intent.putExtra("PICK_UP_DETAILS", predictions.get(position).reference);
                    setResult(PickUpDetailsAddressFragment.RC_PICK_UP, intent);
                    finish();
                }else if (section.equalsIgnoreCase("DeliveryDetailsAddressFragment")){
                    Intent intent = new Intent();
                    intent.putExtra("PICK_UP_CITY", predictions.get(position).description);
                    intent.putExtra("PICK_UP_DETAILS", predictions.get(position).reference);
                    setResult(DeliveryDetailsAddressFragment.RC_DELIVERY, intent);
                    finish();

                }

            }
        });
        rvGoogle.setAdapter(searchListAdapter);
    }

    private void findIds() {
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        etLocation=(EditText) findViewById(R.id.etLocation);
        rvGoogle=(RecyclerView) findViewById(R.id.rvGoogle);
        rvGoogle.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        etLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
        tvHeader.setText("Google Search");
    }


    private void setListeners() {

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void callGoogleAutoCompleteAPI(String place){
        String newPlace = place.replace(" ", "%20");
        try{


            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+newPlace+"&location="+latLng.latitude+","+latLng.longitude+"&radius=500&types=geocode&key="+ AppConstant.GOOGLE_KEY + "&components=country:in";

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
//                                        if (!TextUtils.isEmpty(response.predictions.get(i).description) &&
//                                                response.predictions.get(i).description.toLowerCase().contains(cityName.toLowerCase())
//                                                || cityName.toLowerCase().contains(response.predictions.get(i).description.toLowerCase())){
//                                            predictions.add(response.predictions.get(i));
//                                            terms = response.predictions.get(i).terms;
//                                        }
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
                                            if (predictionCity.contains(cityName.toLowerCase()) || cityName.toLowerCase().contains(predictionCity)){
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
    }   catch (Exception e1){

        e1.printStackTrace();
    }}

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
