package com.pikndel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pikndel.R;
import com.pikndel.activity.intracity.DeliveryLocationActivity;
import com.pikndel.activity.intracity.PickUpLocationActivity;
import com.pikndel.model.CityInfo;
import com.pikndel.model.DeliveryInfo;
import com.pikndel.model.DistanceGoogleModel;
import com.pikndel.model.IntraCityModel;
import com.pikndel.model.ProductInfo;
import com.pikndel.model.Terms;
import com.pikndel.model.WeightInfo;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.services.WebServiceAsync;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.HintSpinner;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IntraCityDeliveryActivity extends AppCompatActivity{

    private TextView tvOneWay, tvRoundTrip, tvHeader;
    private Context context = IntraCityDeliveryActivity.this;
    private ImageView ivLeft, ivRight;
    private static String s_Mobile;

    //*******//
    private LinearLayout llSelectCity, llNonFragile, llEnterWght;
    private TextView tvSelectCityError, tvPickUpError, tvDeliveryError, tvPackgeType, tvNonFragileError,
            tvPkgWeight, tvDPackagwgtError, tvEntWghtError, tvCOD, tvEntAmontError, tvNext, tvPickUpLocation, tvDeliveryLocation;
    private EditText etEnterWeight,etEnterAmount;
    private HintSpinner spNonFragileGoods, spWeight, spSelectCity;
    private RadioButton btYes, btNo;

    private String cityId = "", cityName = "";

    private List<WeightInfo> weightInfo = new ArrayList<>();
    private List<CityInfo> cityInfo = new ArrayList<>();
    private List<ProductInfo> productInfo = new ArrayList<>();

    private CitySpinnerAdapter citySpinnerAdapter;
    private ProductSpinnerAdapter productSpinnerAdapter;
    private WeightSpinnerAdapter weightSpinnerAdapter;
    private boolean isWeightEnabled = false;
    private Intent intent = null;
    public static final int RC_PICK_UP = 100;
    public static final int RC_DELIVERY = 101;
    private String pickUpReference = "";
    private String deliveryReference = "";

    public String productType = "", packageWeight = "", packageWeightId = "",
            planId = "", city = "", distanceKm = "0", productTypeId = "";
    public boolean roundTrip = false;
    private PrefsManager prefsManager;
    public String origin, destination;
    private String weight;
    TextView   tvErrorMobile;

    public EditText etMobile;
    private String stPikupPartialAddress="";

    private IntraCityModel intraCityModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_city_delivery);

        prefsManager = new PrefsManager(context);
        findIds();
        setTextAttributes();
        setListeners();

        settingSpinner();
        intraCityDeliveryService();
        settingFragment(0);
    }




    private void setTextAttributes() {

        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvOneWay.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvRoundTrip.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvHeader.setText("Intra-city Delivery");
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);

        //***//
        tvPickUpError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliveryError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNonFragileError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPkgWeight.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDPackagwgtError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEnterWeight.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPickUpLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliveryLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEntWghtError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        btYes.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        btNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEnterAmount.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEntAmontError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNext.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }



    private void setListeners() {
        tvOneWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(0);
            }
        });

        tvRoundTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(1);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

//****************************//
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidation()){
                    callGoogleAutoCompleteAPI(origin, destination);

                }
            }
        });

        btYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (btYes.isChecked()) {
                    etEnterAmount.setVisibility(View.VISIBLE);
                } else {
                    etEnterAmount.setVisibility(View.GONE);
                    tvEntAmontError.setVisibility(View.GONE);
                    etEnterAmount.setBackgroundResource(R.drawable.rectangle_rounded_corner_edittext);
                }
            }
        });

        tvPickUpLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cityName.equalsIgnoreCase("")) {
                    intent = new Intent(context, PickUpLocationActivity.class);
                    intent.putExtra("CITY_INFO", cityName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, RC_PICK_UP);
                }else {
                    CommonUtils.showToast(context, "Please select city first.");
                }
            }
        });



        tvDeliveryLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvPickUpLocation.getText().toString().trim().equalsIgnoreCase("Pick-up Location")) {
                    IntraCityModel intraCityModel = new IntraCityModel();
                    intraCityModel.deliveryInfo = new DeliveryInfo();

                    intraCityModel.deliveryType = "intracity";
                    intraCityModel.deliveryInfo.startCityId = cityId;
                    intraCityModel.deliveryInfo.startCityName = cityName;
                    intraCityModel.deliveryInfo.endCityId = cityId;
                    intraCityModel.deliveryInfo.endCityName = cityName;
                    intraCityModel.deliveryInfo.productTypeId = productTypeId;
                    intraCityModel.deliveryInfo.packageWeightId = packageWeightId;
                    intraCityModel.deliveryInfo.packageWeight = packageWeight;
                    intraCityModel.deliveryInfo.pickupLocation = tvPickUpLocation.getText().toString().trim();
                    intraCityModel.deliveryInfo.deliverylocation = "";
                    intraCityModel.weight = weight;
                    if (roundTrip){
                        intraCityModel.deliveryInfo.isRoundTrip = "1";
                    }else {
                        intraCityModel.deliveryInfo.isRoundTrip = "0";
                    }
                    intraCityModel.packageWeightAbove = packageWeight;
                    intraCityModel.packageType = productType;
                    intraCityModel.roundTrip = roundTrip;
                    intraCityModel.distanceKm = distanceKm;

                    if (TextUtils.isEmpty(etEnterAmount.getText().toString().trim())){
                        intraCityModel.deliveryInfo.codAmount = "0";
                    }else {
                        intraCityModel.deliveryInfo.codAmount = etEnterAmount.getText().toString().trim();
                    }

                    intraCityModel.planId = prefsManager.getKeyPlanId();

                    intent = new Intent(context, DeliveryLocationActivity.class);
                    intent.putExtra("CITY_INFO", cityName);
                    intent.putExtra("INTER_CITY_DELIVERY", intraCityModel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, RC_DELIVERY);



                }else {
                    CommonUtils.showToast(context, "Please select pick up location first.");
                }
            }
        });

    }

    private void findIds() {
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvOneWay = (TextView) findViewById(R.id.tvOneWay);
        tvRoundTrip = (TextView) findViewById(R.id.tvRoundTrip);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);

        //*****//
        spSelectCity = (HintSpinner)findViewById(R.id.spSelectCity);
        spNonFragileGoods = (HintSpinner)findViewById(R.id.spNonFragileGoods);
        spWeight = (HintSpinner)findViewById(R.id.spWeight);

        etEnterWeight = (EditText)findViewById(R.id.etEnterWeight);
        tvPickUpLocation = (TextView)findViewById(R.id.tvPickUpLocation);
        tvDeliveryLocation = (TextView)findViewById(R.id.tvDeliveryLocation);
        etEnterAmount = (EditText)findViewById(R.id.etEnterAmount);

        tvPickUpError = (TextView)findViewById(R.id.tvPickUpError);
        tvSelectCityError = (TextView)findViewById(R.id.tvSelectCityError);
        tvDeliveryError = (TextView)findViewById(R.id.tvDeliveryError);
        tvPackgeType = (TextView)findViewById(R.id.tvPackgeType);
        tvDPackagwgtError = (TextView)findViewById(R.id.tvDPackagwgtError);
        tvNonFragileError = (TextView)findViewById(R.id.tvNonFragileError);
        tvPkgWeight = (TextView)findViewById(R.id.tvPkgWeight);
        tvEntAmontError = (TextView)findViewById(R.id.tvEntAmontError);
        tvNext = (TextView)findViewById(R.id.tvNext);
        tvEntWghtError = (TextView)findViewById(R.id.tvEntWghtError);
        tvCOD = (TextView)findViewById(R.id.tvCOD);

        btYes = (RadioButton)findViewById(R.id.btYes);
        btNo = (RadioButton)findViewById(R.id.btNo);

        llSelectCity = (LinearLayout)findViewById(R.id.llSelectCity);
        llNonFragile = (LinearLayout)findViewById(R.id.llNonFragile);
        llEnterWght = (LinearLayout)findViewById(R.id.llEnterWght);
    }

    private void settingFragment(int position){
        switch (position){
            case 0:
                tvOneWay.setBackgroundResource(R.drawable.rectangle_left_rounded);
                tvRoundTrip.setBackgroundResource(R.drawable.white_rectangle_right_rounded);
                tvOneWay.setTextColor(getResources().getColor(R.color.white));
                tvRoundTrip.setTextColor(getResources().getColor(R.color.colorPrimary));
                roundTrip = false;
                break;
            case 1:
                tvOneWay.setBackgroundResource(R.drawable.white_rectangle_left_rounded);
                tvRoundTrip.setBackgroundResource(R.drawable.rectangle_right_rounded);
                tvOneWay.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvRoundTrip.setTextColor(getResources().getColor(R.color.white));
                roundTrip = true;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            boolean isFilter = false;
            switch (requestCode) {
                case RC_PICK_UP:
                    if (data != null && !TextUtils.isEmpty(data.getStringExtra("PICK_UP_CITY"))) {
                        origin = data.getStringExtra("PICK_UP_CITY");
                        pickUpReference = data.getStringExtra("PICK_UP_REF");
                        isFilter = data.getBooleanExtra("IS_TRIM", false);
                        if (isFilter) {
                            String[] areaArray = origin.split(",");


                            tvPickUpLocation.setText(areaArray[0].trim());
                            origin = origin.replace(",", " ");


                        } else {

                            List<Terms> terms = new ArrayList<>();
                            terms = (List<Terms>) data.getSerializableExtra("SEARCH_DELIVERY");
                            Log.e("Terms_size", "" + terms.size());

                            if (terms.size() > 1) {

                                stPikupPartialAddress= terms.get(0).value;

                            }
                            tvPickUpLocation.setText(origin);
                        }

                    }
                    break;
                case RC_DELIVERY:
                    if (data != null && !TextUtils.isEmpty(data.getStringExtra("DELIVERY_CITY"))) {
                        destination = data.getStringExtra("DELIVERY_CITY");
                        deliveryReference= data.getStringExtra("DELIVERY_REF");
                        isFilter = data.getBooleanExtra("IS_TRIM", false);
                        if (isFilter) {
                            String[] areaArray = destination.split(",");
                            tvDeliveryLocation.setText(areaArray[0].trim());
                            destination = destination.replace(",", " ");
                        } else {
                            tvDeliveryLocation.setText(destination);
                        }
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void settingSpinner() {
        CityInfo cityInfoModel = new CityInfo();
        cityInfoModel.cityName = "Select City";
        cityInfoModel.cityId = "";
        cityInfo.add(0, cityInfoModel);
        citySpinnerAdapter = new CitySpinnerAdapter(context, R.layout.row_custom_spinner, cityInfo);
        spSelectCity.setAdapter(citySpinnerAdapter);
        spSelectCity.setSelection(0);
        spSelectCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    llSelectCity.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                    tvSelectCityError.setVisibility(View.GONE);
                    cityId = cityInfo.get(position).cityId;
                    cityName = cityInfo.get(position).cityName;
                    tvPickUpLocation.setText("Pick-up Location");
                    tvDeliveryLocation.setText("Delivery Location");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ProductInfo productInfoModel = new ProductInfo();
        productInfoModel.productType = "Select Package Type";
        productInfoModel.productTypeId = "";
        productInfo.add(0, productInfoModel);
        productSpinnerAdapter = new ProductSpinnerAdapter(context,R.layout.row_custom_spinner,productInfo);
        spNonFragileGoods.setAdapter(productSpinnerAdapter);
        spNonFragileGoods.setSelection(0);
        spNonFragileGoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    llNonFragile.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                    tvNonFragileError.setVisibility(View.GONE);
                    productType = productInfo.get(position).productType;
                    productTypeId = productInfo.get(position).productTypeId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        WeightInfo weightInfoModel = new WeightInfo();
        weightInfoModel.weightDescription = "Select Weight";
        weightInfoModel.packageWeightId = "";
        weightInfo.add(0, weightInfoModel);
        weightSpinnerAdapter = new WeightSpinnerAdapter(context,R.layout.row_custom_spinner,weightInfo);
        spWeight.setAdapter(weightSpinnerAdapter);
        spWeight.setSelection(0);
        spWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    llEnterWght.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                    tvDPackagwgtError.setVisibility(View.GONE);
                    packageWeightId = weightInfo.get(position).packageWeightId;
                    packageWeight = String.valueOf(weightInfo.get(position).packageWeight);
                    weight = (weightInfo.get(position).weightDescription);
                    if (weightInfo.get(position).packageWeight <= 0) {
                        etEnterWeight.setVisibility(View.VISIBLE);
                        etEnterWeight.setText("");
                        isWeightEnabled = true;
                        minWeight = weightInfo.get(position-1).packageWeight;

                    } else {
                        tvNext.setClickable(true);
                        etEnterWeight.setVisibility(View.GONE);
                        tvEntWghtError.setVisibility(View.GONE);
                        isWeightEnabled = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private float minWeight = 0.0f;

    private boolean checkValidation() {
        if (spSelectCity.getSelectedItemPosition() == 0) {
            llSelectCity.setBackgroundResource(R.drawable.rederror_shape);
            tvSelectCityError.setVisibility(View.VISIBLE);
            return false;
        } else if (tvPickUpLocation.getText().toString().trim().equalsIgnoreCase("Pick-up Location")) {
            tvPickUpError.setText("*Please select pick-up location.");
            tvPickUpLocation.setBackgroundResource(R.drawable.rederror_shape);
            tvPickUpError.setVisibility(View.VISIBLE);
            return false;
        } else if (tvDeliveryLocation.getText().toString().trim().equalsIgnoreCase("Delivery Location")) {
            tvDeliveryError.setText("*Please select delivery location.");
            tvDeliveryLocation.setBackgroundResource(R.drawable.rederror_shape);
            tvDeliveryError.setVisibility(View.VISIBLE);
            return false;
        } else if (spNonFragileGoods.getSelectedItemPosition() == 0) {
            llNonFragile.setBackgroundResource(R.drawable.rederror_shape);
            tvNonFragileError.setVisibility(View.VISIBLE);
            return false;
        } else if (spWeight.getSelectedItemPosition() == 0) {
            llEnterWght.setBackgroundResource(R.drawable.rederror_shape);
            tvDPackagwgtError.setVisibility(View.VISIBLE);
            return false;
        } else if ((etEnterWeight.getVisibility() == View.VISIBLE) && etEnterWeight.getText().toString().trim().isEmpty()) {
            etEnterWeight.setBackgroundResource(R.drawable.rederror_shape);
            tvEntWghtError.setVisibility(View.VISIBLE);
            tvEntWghtError.setText("*Please enter the weight. ");
            etEnterWeight.setVisibility(View.VISIBLE);
            etEnterAmount.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvEntAmontError.setVisibility(View.INVISIBLE);
            etEnterWeight.requestFocus();
            return false;
        } else if (etEnterWeight.getVisibility() == View.VISIBLE && Float.parseFloat(etEnterWeight.getText().toString()) <= 6) {
            etEnterWeight.setBackgroundResource(R.drawable.rederror_shape);
            tvEntWghtError.setText("*Please enter the valid weight. ");
            tvEntWghtError.setVisibility(View.VISIBLE);
            etEnterAmount.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvEntAmontError.setVisibility(View.INVISIBLE);
            etEnterWeight.requestFocus();
            return false;
        } else if ((etEnterAmount.getVisibility() == View.VISIBLE) && etEnterAmount.getText().toString().trim().isEmpty()) {

            etEnterAmount.setBackgroundResource(R.drawable.rederror_shape);
            tvEntAmontError.setVisibility(View.VISIBLE);
            etEnterAmount.requestFocus();
            return false;

        }
        /*if ((etEnterWeight.getVisibility() == View.VISIBLE && !etEnterWeight.getText().toString().trim().isEmpty()) && (!blPhoneNumber)) {
            dialogWeight();
            return false;
        }
        if (isWeightEnabled && etEnterWeight.getVisibility() == View.VISIBLE && !dataImp.isEmpty()) {
            tvNext.setClickable(false);
            return false;
        }*/else {
            return true;
        }
    }

    private void intraCityDeliveryService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("OneWayFragment", "_____Request_____" + String.format(RequestURL.URL_INTRA_CITY_DELIVERY, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_INTRA_CITY_DELIVERY, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    weightInfo.clear();
                                    cityInfo.clear();
                                    productInfo.clear();
                                    prefsManager.setKeyIsMultiLocation(TextUtils.isEmpty(serviceResponse.isMultiLocation)?"0":serviceResponse.isMultiLocation);
                                    //prefsManager.setKeyIsMultiLocation("1");

                                    if (serviceResponse.weightInfo != null && serviceResponse.weightInfo.size()>0){
                                        weightInfo.addAll(serviceResponse.weightInfo);
                                        WeightInfo weightInfoModel = new WeightInfo();
                                        weightInfoModel.weightDescription = "Select Weight";
                                        weightInfoModel.packageWeightId = "";
                                        weightInfo.add(0, weightInfoModel);
                                        weightSpinnerAdapter.notifyDataSetChanged();
                                    }

                                    if(serviceResponse.maxWeight !=null){

                                        CommonUtils.savePreferencesString(context, AppConstant.Max_WEIGHT, serviceResponse.maxWeight);
                                    }
                                    if (serviceResponse.cityInfo != null && serviceResponse.cityInfo.size()>0){
                                        cityInfo.addAll(serviceResponse.cityInfo);
                                        CityInfo startCityInfoModel = new CityInfo();
                                        startCityInfoModel.cityName = "Select City";
                                        startCityInfoModel.cityId = "";
                                        cityInfo.add(0, startCityInfoModel);
                                        citySpinnerAdapter.notifyDataSetChanged();
                                    }

                                    if (serviceResponse.productInfo != null && serviceResponse.productInfo.size()>0){
                                        productInfo.addAll(serviceResponse.productInfo);
                                        ProductInfo productInfoModel = new ProductInfo();
                                        productInfoModel.productType = "Package Type";
                                        productInfoModel.productTypeId = "";
                                        productInfo.add(0, productInfoModel);
                                        productSpinnerAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    CommonUtils.showToast(context, serviceResponse.message);
                                }
                            } else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Object o) {
                        ServiceResponse serviceResponse = (ServiceResponse) o;
                        if (serviceResponse != null) {
                            CommonUtils.showToast(context, serviceResponse.message);
                        }

                    }
                }).execute();
            } else {
                CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGoogleAutoCompleteAPI(final String origin, final String destination){
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&sensor=false&mode=DRIVING&key="+ AppConstant.GOOGLE_KEY_DISTANCE;

        WebServiceAsync serviceAsync = new WebServiceAsync(context, true, "", url.replace(" ", "%20"), "GET", new ServiceStatus() {

            @Override
            public void onSuccess(Object o) {

                try {
                    ServiceResponse response = new Gson().fromJson(o.toString(),ServiceResponse.class);
                    if (response != null){
                        if (!TextUtils.isEmpty(response.status)) {
                            if (response.status.equalsIgnoreCase("OK")) {
                                if (response.routes != null && response.routes.size() > 0 &&
                                        response.routes.get(0).legs != null && response.routes.get(0).legs.size()>0 &&
                                        response.routes.get(0).legs.get(0).distance != null) {
                                    DistanceGoogleModel distance = response.routes.get(0).legs.get(0).distance;
                                    float dist = (float) distance.value/1000;
                                    distanceKm = String.format(Locale.US, "%.3f", dist);
                                    if (isWeightEnabled){
                                        packageWeight = etEnterWeight.getText().toString().trim();
                                    }

                                    intraCityModel = new IntraCityModel();
                                    intraCityModel.deliveryInfo = new DeliveryInfo();

                                    intraCityModel.pickUpReference = pickUpReference;
                                    intraCityModel.deliveryReference = deliveryReference;
                                    intraCityModel.deliveryType = "intracity";
                                    intraCityModel.deliveryInfo.startCityId = cityId;
                                    intraCityModel.deliveryInfo.startCityName = cityName;
                                    intraCityModel.deliveryInfo.endCityId = cityId;
                                    intraCityModel.deliveryInfo.endCityName = cityName;
                                    intraCityModel.deliveryInfo.productTypeId = productTypeId;
                                    intraCityModel.deliveryInfo.packageWeightId = packageWeightId;
                                    intraCityModel.deliveryInfo.packageWeight = packageWeight;
                                    intraCityModel.deliveryInfo.pickupLocation = origin;
                                    intraCityModel.deliveryInfo.deliverylocation = destination;
                                    if(intraCityModel.deliveryInfo.pickupLocation.equals(intraCityModel.deliveryInfo.deliverylocation)){

                                        distanceKm =  "1";
                                    }
                                    intraCityModel.deliveryInfo.isMultiLocation = "0";
                                    intraCityModel.deliveryInfo.locationCount = "0";
                                    if (roundTrip){
                                        intraCityModel.deliveryInfo.isRoundTrip = "1";
                                    }else {
                                        intraCityModel.deliveryInfo.isRoundTrip = "0";
                                    }
                                    intraCityModel.packageWeightAbove = packageWeight;
                                    intraCityModel.packageType = productType;
                                    intraCityModel.roundTrip = roundTrip;
                                    intraCityModel.distanceKm = distanceKm;
                                    intraCityModel.weight = weight;
                                    intraCityModel.deliveryInfo.pickupPartialLocation=stPikupPartialAddress;



                                    if (etEnterWeight.getVisibility()==View.VISIBLE) {


                                        if (etEnterWeight.getText().toString().trim().length() > 0) {


                                            int i = Integer.valueOf(etEnterWeight.getText().toString());
                                            String st_max_weight;
                                            st_max_weight = CommonUtils.getPreferences(context, AppConstant.Max_WEIGHT);

                                            if (i >= Integer.parseInt(st_max_weight)) {

                                                if (prefsManager.getKeyUserId().equals("-1")) {

                                                    guestUserNumber((Activity) context);

                                                } else {

                                                    loginUser((Activity) context);
                                                }

                                            }

                                            else {


                                                if (TextUtils.isEmpty(etEnterAmount.getText().toString().trim())){
                                                    intraCityModel.deliveryInfo.codAmount = "0";
                                                }else {
                                                    intraCityModel.deliveryInfo.codAmount = etEnterAmount.getText().toString().trim();
                                                }
                                                intraCityModel.planId = prefsManager.getKeyPlanId();
                                                startActivity(new Intent(context, WaysToPikndelActivity.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        .putExtra("INTER_CITY_DELIVERY", intraCityModel));
                                            }

                                        }

                                    }


                                    else {

                                        if (TextUtils.isEmpty(etEnterAmount.getText().toString().trim())){
                                            intraCityModel.deliveryInfo.codAmount = "0";
                                        }else {
                                            intraCityModel.deliveryInfo.codAmount = etEnterAmount.getText().toString().trim();
                                        }
                                        intraCityModel.planId = prefsManager.getKeyPlanId();
                                        startActivity(new Intent(context, WaysToPikndelActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .putExtra("INTER_CITY_DELIVERY", intraCityModel));



                                    }



                                }
                            }else {
                                CommonUtils.showToast(context, "Something went wrong. Try again");
                            }
                        } else {
                            CommonUtils.showToast(context, "Something went wrong. Try again");
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

    /**
     * CitySpinnerAdapter
     */
    private class CitySpinnerAdapter extends BaseAdapter {
        private Context context;
        private List<CityInfo> startCityInfo;
        private int resource;

        public CitySpinnerAdapter(Context context, int resource, List<CityInfo> startCityInfo) {
            this.context = context;
            this.startCityInfo = startCityInfo;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return cityInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return cityInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder=new ViewHolder();
                convertView= LayoutInflater.from(context).inflate(resource,null);
                viewHolder.textSpinner=(TextView) convertView.findViewById(R.id.tvSpinnerItem);
                convertView.setTag(viewHolder);
            } else {
                viewHolder=(ViewHolder)convertView.getTag();
            }

            viewHolder.textSpinner.setText(cityInfo.get(position).cityName);
            viewHolder.textSpinner.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

            return convertView;
        }

        class ViewHolder {
            private TextView textSpinner;
        }
    }

    /**
     * ProductSpinnerAdapter
     */
    private class ProductSpinnerAdapter extends BaseAdapter{
        private Context context;
        private List<ProductInfo> productInfo;
        private int resource;

        public ProductSpinnerAdapter(Context context, int resource, List<ProductInfo> productInfo) {
            this.context = context;
            this.productInfo = productInfo;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return productInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return productInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder=new ViewHolder();
                convertView= LayoutInflater.from(context).inflate(resource,null);
                viewHolder.textSpinner=(TextView) convertView.findViewById(R.id.tvSpinnerItem);
                convertView.setTag(viewHolder);
            } else {
                viewHolder=(ViewHolder)convertView.getTag();
            }

            viewHolder.textSpinner.setText(productInfo.get(position).productType);
            viewHolder.textSpinner.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

            return convertView;
        }

        class ViewHolder {
            private TextView textSpinner;
        }
    }

    public  void guestUserNumber(final Activity context) {
        final TextView tvSubmit ;

        LayoutInflater inflater = LayoutInflater.from(context);
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.75f;
        mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow();

        View dialoglayout = inflater.inflate(R.layout.dialog_guest_number, null);
        mDialog.setContentView(dialoglayout);

        tvSubmit = (TextView) mDialog.findViewById(R.id.tv_submit);
        tvErrorMobile = (TextView) mDialog.findViewById(R.id.tv_error_mobile);
//        tvMsg.setTypeface(CommonUtils.setFontText(mContext));

        etMobile = (EditText)mDialog.findViewById(R.id.etMobile);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (checkValidationAmount()) {

                    s_Mobile = etMobile.getText().toString().trim();
                    getDialogService(s_Mobile);
                    mDialog.dismiss();




                }

            }
        });
        mDialog.show();



    }

    private boolean checkValidationAmount() {


        if (etMobile.getText().toString().trim().isEmpty()) {
            tvErrorMobile.setVisibility(View.VISIBLE);
            tvErrorMobile.setText("*Please Enter Mobile No.");
            tvErrorMobile.requestFocus();
            return false;

        } else if(etMobile.getText().toString().length()<10){
            tvErrorMobile.setVisibility(View.VISIBLE);
            tvErrorMobile.setText("*Mobile No. should contains 10 digits");
            tvErrorMobile.requestFocus();
            return false;
        }else {
            tvErrorMobile.setVisibility(View.GONE);
            return true;
        }
    }

    public void loginUser(final Activity context) {
        final TextView tvSubmit;
        LayoutInflater inflater = LayoutInflater.from(context);
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.75f;
        mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow();

        View dialoglayout = inflater.inflate(R.layout.dialog_login_user, null);
        mDialog.setContentView(dialoglayout);

        tvSubmit = (TextView) mDialog.findViewById(R.id.tv_submit);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mDialog.dismiss();
                getDialogService_registered_user();


            }
        });
        mDialog.show();
    }

    private void getDialogService(String sMobile) {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.phoneNumber,sMobile );
            jsonObject.put(serviceRequest.maxWeightValue, etEnterWeight.getText().toString());
            jsonObject.put(serviceRequest.pickUpCityName, "");
            jsonObject.put(serviceRequest.deliveryCityName,"" );
            jsonObject.put(serviceRequest.pickUpLocation, intraCityModel.deliveryInfo.pickupLocation);
            jsonObject.put(serviceRequest.deliveryLocation, intraCityModel.deliveryInfo.deliverylocation);
            jsonObject.put(serviceRequest.productType,intraCityModel.packageType);
            jsonObject.put(serviceRequest.deliveryType, intraCityModel.deliveryType);
            if (etEnterAmount.getVisibility() == View.VISIBLE){
                jsonObject.put(serviceRequest.codAmount, etEnterAmount.getText().toString());
            }else {
                jsonObject.put(serviceRequest.codAmount, "");
            }

            if (CommonUtils.isOnline(context)) {

                new ServiceAsync(context, false, jsonObject.toString(), RequestURL.URL_ENTER_MOBILE_NUMBER, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {


                                    startActivity(new Intent(context, HomeActivity.class)
                                            .putExtra(AppConstant.INTENT_USER_TYPE, "FREE_USER")
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                                } else {
                                    CommonUtils.showToast(context, serviceResponse.message);
                                }

                            }

                            else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Object o) {
                        ServiceResponse serviceResponse = (ServiceResponse) o;
                        if (serviceResponse != null) {
                            CommonUtils.showToast(context, serviceResponse.message);
                        }

                    }
                }).execute();
            } else {
                CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getDialogService_registered_user() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.phoneNumber, "" );
            jsonObject.put(serviceRequest.maxWeightValue, etEnterWeight.getText().toString());
            jsonObject.put(serviceRequest.pickUpCityName, "");
            jsonObject.put(serviceRequest.deliveryCityName,"" );
            jsonObject.put(serviceRequest.pickUpLocation, intraCityModel.deliveryInfo.pickupLocation);
            jsonObject.put(serviceRequest.deliveryLocation, intraCityModel.deliveryInfo.deliverylocation);
            jsonObject.put(serviceRequest.productType,intraCityModel.packageType);
            jsonObject.put(serviceRequest.deliveryType, intraCityModel.deliveryType);
            if (etEnterAmount.getVisibility() == View.VISIBLE){
                jsonObject.put(serviceRequest.codAmount, etEnterAmount.getText().toString());
            }else {
                jsonObject.put(serviceRequest.codAmount, "");
            }
            if (CommonUtils.isOnline(context)) {

                new ServiceAsync(context, false, jsonObject.toString(), RequestURL.URL_ENTER_MOBILE_NUMBER, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    //CommonUtils.showToast(context, serviceResponse.message);
                                    startActivity(new Intent(context, HomeActivity.class)
                                            .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                                } else {
                                    CommonUtils.showToast(context, serviceResponse.message);
                                }

                            }else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Object o) {
                        ServiceResponse serviceResponse = (ServiceResponse) o;
                        if (serviceResponse != null) {
                            CommonUtils.showToast(context, serviceResponse.message);
                        }

                    }
                }).execute();
            } else {
                CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * WeightSpinnerAdapter
     */
    private class WeightSpinnerAdapter extends BaseAdapter{
        private Context context;
        private List<WeightInfo> weightInfo;
        private int resource;

        public WeightSpinnerAdapter(Context context, int resource, List<WeightInfo> weightInfo) {
            this.context = context;
            this.weightInfo = weightInfo;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return weightInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return weightInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder=new ViewHolder();
                convertView= LayoutInflater.from(context).inflate(resource,null);
                viewHolder.textSpinner=(TextView) convertView.findViewById(R.id.tvSpinnerItem);
                convertView.setTag(viewHolder);
            } else {
                viewHolder=(ViewHolder)convertView.getTag();
            }

            viewHolder.textSpinner.setText(weightInfo.get(position).weightDescription);
            viewHolder.textSpinner.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

            return convertView;
        }

        class ViewHolder {
            private TextView textSpinner;
        }
    }

}



