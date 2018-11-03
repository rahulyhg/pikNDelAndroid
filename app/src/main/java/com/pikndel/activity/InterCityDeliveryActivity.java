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

import com.pikndel.R;
import com.pikndel.model.CityInfo;
import com.pikndel.model.DeliveryInfo;
import com.pikndel.model.EndCityInfo;
import com.pikndel.model.IntraCityModel;
import com.pikndel.model.ProductInfo;
import com.pikndel.model.StartCityInfo;
import com.pikndel.model.WeightInfo;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.HintSpinner;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterCityDeliveryActivity extends AppCompatActivity  implements TextWatcher {

    private static String s_Mobile;
    private HintSpinner spPickUp,spDelivery,spNonFragileGoods,spWeight;
    private EditText etEnterWeight,etEnterAmount;
    private TextView tvPickUpError,tvDeliveryError,tvNonFragileError,tvDPackagwgtError,tvEntWghtError,tvEntAmontError,tvCod,tvNext,tvHeader, tvPkgType, tvPkgWeight;
    private RadioButton btYes,btNo;
    private ImageView ivLeft,ivRight;
    private LinearLayout llPickup,lldeliveryCity,llNonFragile,llEnterWght;
    private Boolean blPhoneNumber=false;
    private String dataImp;
    private TextView tvPickUpCity, tvDeliveryCity, tvNonFragileGoods, tvWeight;

    private List<WeightInfo> weightInfo = new ArrayList<>();
    private List<StartCityInfo> startCityInfo = new ArrayList<>();
    private List<ProductInfo> productInfo = new ArrayList<>();
    private List<EndCityInfo> endCityInfo = new ArrayList<>();

    private Context context=InterCityDeliveryActivity.this;
    private StartCitySpinnerAdapter startCitySpinnerAdapter;
    private ProductSpinnerAdapter productSpinnerAdapter;
    private WeightSpinnerAdapter weightSpinnerAdapter;
    private EndCitySpinnerAdapter endCitySpinnerAdapter;
    private boolean isWeightEnabled = false;
    private String startCityId = "0", endCityId = "0", productTypeId = "0", packageWeightId = "0", startCityName = "", endCityName = "", productType = "";
    private String packageWeight = "0";
    private PrefsManager prefsManager;
    private float minWeight = 0.0f;
    private String weight = "";
    private IntraCityModel intraCityModel;

    TextView   tvErrorMobile;

    public EditText etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_city_delivery_two);

        prefsManager = new PrefsManager(context);
        findIds();
        setTextAttributes();
        setListeners();
        settingSpinner();
        intercityDeliveryService();
    }

    private void findIds() {
        spPickUp=(HintSpinner)findViewById(R.id.spPicUp);
        spDelivery=(HintSpinner)findViewById(R.id.spDelivery);
        spNonFragileGoods=(HintSpinner)findViewById(R.id.spNonFragileGoods);
        spWeight=(HintSpinner)findViewById(R.id.spWeight);
        etEnterWeight=(EditText)findViewById(R.id.etEnterWeight);
        etEnterAmount=(EditText)findViewById(R.id.etEnterAmount);
        tvPickUpError=(TextView) findViewById(R.id.tvPickUpError);
        tvDeliveryError=(TextView) findViewById(R.id.tvDeliveryError);

        tvPickUpCity=(TextView) findViewById(R.id.tvPickUpCity);
        tvDeliveryCity=(TextView) findViewById(R.id.tvDeliveryCity);
        tvNonFragileGoods=(TextView) findViewById(R.id.tvNonFragileGoods);
        tvWeight=(TextView) findViewById(R.id.tvWeight);

        tvHeader=(TextView) findViewById(R.id.tvHeader);
        tvPkgType=(TextView) findViewById(R.id.tvPkgType);
        tvDPackagwgtError=(TextView) findViewById(R.id.tvDPackagwgtError);
        tvNonFragileError=(TextView) findViewById(R.id.tvNonFragileError);
        tvEntAmontError=(TextView) findViewById(R.id.tvEntAmontError);
        tvNext=(TextView) findViewById(R.id.tvNext);
        tvPkgWeight=(TextView) findViewById(R.id.tvPkgWeight);
        tvCod=(TextView) findViewById(R.id.tvCod);
        tvEntWghtError=(TextView) findViewById(R.id.tvEntWghtError);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        btYes=(RadioButton)findViewById(R.id.btYes);
        btNo=(RadioButton)findViewById(R.id.btNo);
        llPickup=(LinearLayout) findViewById(R.id.llPickup);
        lldeliveryCity=(LinearLayout)findViewById(R.id.lldeliveryCity);
        llNonFragile=(LinearLayout)findViewById(R.id.llNonFragile);
        llEnterWght=(LinearLayout)findViewById(R.id.llEnterWght);

        btYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(btYes.isChecked()){
                    etEnterAmount.setVisibility(View.VISIBLE);
                }
                else {
                    etEnterAmount.setVisibility(View.GONE);
                    tvEntAmontError.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    if (isWeightEnabled){
                        packageWeight = etEnterWeight.getText().toString().trim();
                    }
                    intraCityModel = new IntraCityModel();
                    intraCityModel.deliveryInfo = new DeliveryInfo();

                    intraCityModel.deliveryType = "intercity";
                    intraCityModel.deliveryInfo.startCityId = startCityId;
                    intraCityModel.deliveryInfo.startCityName = startCityName;
                    intraCityModel.deliveryInfo.endCityId = endCityId;
                    intraCityModel.deliveryInfo.endCityName = endCityName;
                    intraCityModel.deliveryInfo.productTypeId = productTypeId;
                    intraCityModel.deliveryInfo.packageWeightId = packageWeightId;
                    intraCityModel.deliveryInfo.packageWeight = packageWeight;
                    intraCityModel.deliveryInfo.pickupLocation = "";
                    intraCityModel.deliveryInfo.deliverylocation = "";
                    intraCityModel.deliveryInfo.isMultiLocation = "0";
                    intraCityModel.deliveryInfo.locationCount = "0";
                    intraCityModel.deliveryInfo.isRoundTrip = "0";
                    intraCityModel.packageType = productType;
                    intraCityModel.weight = weight;





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
                        }
                        else {
                            intraCityModel.deliveryInfo.codAmount = etEnterAmount.getText().toString().trim();



                        }

                        intraCityModel.planId = prefsManager.getKeyPlanId();
                        startActivity(new Intent(context, WaysToPikndelActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .putExtra("INTER_CITY_DELIVERY", intraCityModel));


                    }


                    //  if (intraCityModel.)




                }
            }
        });


        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

        tvPickUpCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spPickUp.performClick();
            }
        });

        tvDeliveryCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spDelivery.performClick();
            }
        });

        tvNonFragileGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spNonFragileGoods.performClick();
            }
        });

        tvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spWeight.performClick();
            }
        });

        etEnterWeight.addTextChangedListener(this);
        etEnterAmount.addTextChangedListener(this);
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvPickUpError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliveryError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPkgType.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNonFragileError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPkgWeight.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDPackagwgtError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEnterWeight.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEntWghtError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCod.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        btYes.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        btNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEnterAmount.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEntAmontError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNext.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvPickUpCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliveryCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNonFragileGoods.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvWeight.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
        tvHeader.setText("Inter-city Delivery");
    }

    private void settingSpinner() {
        StartCityInfo startCityInfoModel = new StartCityInfo();
        startCityInfoModel.startCity = "Pick-up City";
        startCityInfoModel.startCityId = "0";
        startCityInfo.add(0, startCityInfoModel);
        startCitySpinnerAdapter = new StartCitySpinnerAdapter(context, R.layout.row_custom_spinner, startCityInfo);
        spPickUp.setAdapter(startCitySpinnerAdapter);
        spPickUp.setSelection(0);
        spPickUp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spDelivery.setSelection(0);
                    llPickup.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                    tvPickUpError.setVisibility(View.GONE);
                    startCityId = startCityInfo.get(position).startCityId;
                    startCityName = startCityInfo.get(position).startCity;
                    tvPickUpCity.setText(startCityInfo.get(position).startCity);
                    getEndCitiesService(startCityInfo.get(position).startCityId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EndCityInfo endCityInfoModel = new EndCityInfo();
        endCityInfoModel.endCity = "Delivery City";
        endCityInfoModel.cityId = "0";
        endCityInfo.add(0, endCityInfoModel);
        endCitySpinnerAdapter = new EndCitySpinnerAdapter(context,R.layout.row_custom_spinner,endCityInfo);
        spDelivery.setAdapter(endCitySpinnerAdapter);
        spDelivery.setSelection(0);
        spDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    lldeliveryCity.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                    tvDeliveryError.setVisibility(View.GONE);
                    endCityId = endCityInfo.get(position).cityId;
                    endCityName = endCityInfo.get(position).endCity;
                    tvDeliveryCity.setText(endCityInfo.get(position).endCity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ProductInfo productInfoModel = new ProductInfo();
        productInfoModel.productType = "Select Package Type";
        productInfoModel.productTypeId = "0";
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
                    tvNonFragileGoods.setText(productInfo.get(position).productType);
                    productTypeId = productInfo.get(position).productTypeId;
                    productType = productInfo.get(position).productType;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        WeightInfo weightInfoModel = new WeightInfo();
        weightInfoModel.weightDescription = "Select Weight";
        weightInfoModel.packageWeightId = "0";
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
                    packageWeight = String.valueOf(weightInfo.get(position).packageWeight);
                    packageWeightId = String.valueOf(weightInfo.get(position).packageWeightId);
                    weight = (weightInfo.get(position).weightDescription);
                    tvWeight.setText(weightInfo.get(position).weightDescription);
                    if (weightInfo.get(position).packageWeight <= 0.0f) {
                        etEnterWeight.setVisibility(View.VISIBLE);
                        etEnterWeight.setText("");
                        isWeightEnabled = true;
                        minWeight = weightInfo.get(position - 1).packageWeight;

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



    public void dialogWeight()  {
        final Dialog dialog = DialogUtils.createCustomDialog(context, R.layout.dialog_weight_three);

        TextView tvThankYou = (TextView) dialog.findViewById(R.id.tvThankYou);
        final EditText etMobileNo = (EditText) dialog.findViewById(R.id.etMobileNo);
        TextView tvEnter = (TextView) dialog.findViewById(R.id.tvEnter);
        final TextView tvErrorMobileNo = (TextView) dialog.findViewById(R.id.tvErrorMobileNo);
        TextView tvSubmit = (TextView) dialog.findViewById(R.id.tvSubmit);

        tvSubmit.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvThankYou.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEnter.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etMobileNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorMobileNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMobileNo.getText().toString().trim().length() > 0) {
                    tvErrorMobileNo.setVisibility(View.GONE);
                    etMobileNo.setBackgroundResource(R.drawable.rectangle_rounded_corner_edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etMobileNo.getText().toString().trim().isEmpty()) {
                    tvErrorMobileNo.setText("Please enter your mobile no.");
                    tvErrorMobileNo.setVisibility(View.VISIBLE);
                    etMobileNo.setBackgroundResource(R.drawable.rederror_shape);
                }else
                if (etMobileNo.getText().toString().trim().length()<10) {
                    tvErrorMobileNo.setText("Please enter valid mobile no.");
                    tvErrorMobileNo.setVisibility(View.VISIBLE);
                    etMobileNo.setBackgroundResource(R.drawable.rederror_shape);
                }else
                if(etMobileNo.getText().toString().trim().length()==10){
                    blPhoneNumber=true;
                    dialog.dismiss();
                    dataImp=etMobileNo.getText().toString().trim();
                }
                else {
                    dialog.dismiss();
                }

            }


        });

    }

    private boolean checkValidation() {
        if(spPickUp.getSelectedItemPosition() == 0){
            llPickup.setBackgroundResource(R.drawable.rederror_shape);
            tvPickUpError.setVisibility(View.VISIBLE);
            return false;
        } else if(spDelivery.getSelectedItemPosition() == 0) {
            lldeliveryCity.setBackgroundResource(R.drawable.rederror_shape);
            tvDeliveryError.setVisibility(View.VISIBLE);
            return false;
        } else if(spNonFragileGoods.getSelectedItemPosition() == 0) {
            llNonFragile.setBackgroundResource(R.drawable.rederror_shape);
            tvNonFragileError.setVisibility(View.VISIBLE);
            return false;
        } else if(spWeight.getSelectedItemPosition() == 0) {
            llEnterWght.setBackgroundResource(R.drawable.rederror_shape);
            tvDPackagwgtError.setVisibility(View.VISIBLE);
            return false;
        } else if((etEnterWeight.getVisibility() == View.VISIBLE) && etEnterWeight.getText().toString().trim().isEmpty()){
            etEnterWeight.setBackgroundResource(R.drawable.rederror_shape);
            tvEntWghtError.setVisibility(View.VISIBLE);
            tvEntWghtError.setText("*Please enter the weight.");
            etEnterWeight.setVisibility(View.VISIBLE);
            etEnterAmount.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvEntAmontError.setVisibility(View.INVISIBLE);
            etEnterWeight.requestFocus();
            return false;
        } else if(etEnterWeight.getVisibility() == View.VISIBLE && Float.parseFloat(etEnterWeight.getText().toString()) <= 6) {
            etEnterWeight.setBackgroundResource(R.drawable.rederror_shape);
            tvEntWghtError.setText("*Please enter the valid weight.");
            tvEntWghtError.setVisibility(View.VISIBLE);
            etEnterAmount.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvEntAmontError.setVisibility(View.INVISIBLE);
            etEnterWeight.requestFocus();
            return false;
        } else if((etEnterAmount.getVisibility() == View.VISIBLE) && etEnterAmount.getText().toString().trim().isEmpty()){
            etEnterAmount.setBackgroundResource(R.drawable.rederror_shape);
            tvEntAmontError.setVisibility(View.VISIBLE);
            etEnterAmount.requestFocus();
            return false;
        }
        /*if((etEnterWeight.getVisibility()==View.VISIBLE && !etEnterWeight.getText().toString().trim().isEmpty()) && (!blPhoneNumber)){
            dialogWeight();
            return false;
        }
        if(isWeightEnabled && etEnterWeight.getVisibility()==View.VISIBLE && !dataImp.isEmpty()){
            tvNext.setClickable(false);
            return false;
        } */else {
            return true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(spPickUp.getSelectedItemPosition()>=0) {
            llPickup.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvPickUpError.setVisibility(View.GONE);
        }

        if(spDelivery.getSelectedItemPosition()>=0) {
            lldeliveryCity.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvDeliveryError.setVisibility(View.GONE);
        }


        if(spNonFragileGoods.getSelectedItemPosition()>=0) {
            llNonFragile.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvNonFragileError.setVisibility(View.GONE);
        }
        if(spWeight.getSelectedItemPosition()>=0) {
            llEnterWght.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvDPackagwgtError.setVisibility(View.GONE);
        }
        if (etEnterWeight.getText().toString().trim().length() > 0) {
            etEnterWeight.setBackgroundResource(R.drawable.rectangle_rounded_corner_edittext);
            tvEntWghtError.setVisibility(View.GONE);
        }
        if (etEnterAmount.getText().toString().trim().length() > 0) {
            tvEntAmontError.setVisibility(View.GONE);
            etEnterAmount.setBackgroundResource(R.drawable.rectangle_rounded_corner_edittext);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

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
                    getDialogService_guest_user(s_Mobile);
                    mDialog.dismiss();


                  /*  if (TextUtils.isEmpty(etEnterAmount.getText().toString().trim())){
                        intraCityModel.deliveryInfo.codAmount = "0";
                    }
                    else {
                        intraCityModel.deliveryInfo.codAmount = etEnterAmount.getText().toString().trim();



                    }
                    intraCityModel.planId = prefsManager.getKeyPlanId();
                    startActivity(new Intent(context, WaysToPikndelActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra("INTER_CITY_DELIVERY", intraCityModel));
*/


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
        }
        else {
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


/*

                if (TextUtils.isEmpty(etEnterAmount.getText().toString().trim())){
                    intraCityModel.deliveryInfo.codAmount = "0";
                }
                else {
                    intraCityModel.deliveryInfo.codAmount = etEnterAmount.getText().toString().trim();



                }*/


/*

                intraCityModel.planId = prefsManager.getKeyPlanId();
                startActivity(new Intent(context, WaysToPikndelActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("INTER_CITY_DELIVERY", intraCityModel));
*/




            }
        });
        mDialog.show();
    }



    private void intercityDeliveryService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("InterCityDeliveryActivity", "_____Request_____" + String.format(RequestURL.URL_INTER_CITY_DELIVERY, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_INTER_CITY_DELIVERY, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    weightInfo.clear();
                                    startCityInfo.clear();
                                    productInfo.clear();

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

                                    if (serviceResponse.startCityInfo != null && serviceResponse.startCityInfo.size()>0){
                                        startCityInfo.addAll(serviceResponse.startCityInfo);
                                        StartCityInfo startCityInfoModel = new StartCityInfo();
                                        startCityInfoModel.startCity = "Pick-up City";
                                        startCityInfoModel.startCityId = "";
                                        startCityInfo.add(0, startCityInfoModel);
                                        startCitySpinnerAdapter.notifyDataSetChanged();
                                      /*  CityInfo cityInfoModel = new CityInfo();
                                        CommonUtils.savePreferencesString(context,cityInfoModel.cityName,AppConstant.CITY_NAME);*/

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


    private void getEndCitiesService(String cityId) {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.cityId,cityId);
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("InterCityDeliveryActivity", "_____Request_____" + RequestURL.URL_GET_END_CITIES);
                new ServiceAsync(context, false, jsonObject.toString(), RequestURL.URL_GET_END_CITIES, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    endCityInfo.clear();
                                    if (serviceResponse.endCityInfo != null && serviceResponse.endCityInfo.size()>0){
                                        endCityInfo.addAll(serviceResponse.endCityInfo);
                                        EndCityInfo endCityInfoModel = new EndCityInfo();
                                        endCityInfoModel.endCity = "Delivery City";
                                        endCityInfoModel.cityId = "";
                                        endCityInfo.add(0, endCityInfoModel);
                                        endCitySpinnerAdapter.notifyDataSetChanged();
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




    private void getDialogService_guest_user(String sMobile) {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.phoneNumber,sMobile );
            jsonObject.put(serviceRequest.maxWeightValue, etEnterWeight.getText().toString());
            jsonObject.put(serviceRequest.pickUpCityName, intraCityModel.deliveryInfo.startCityName);
            jsonObject.put(serviceRequest.deliveryCityName, intraCityModel.deliveryInfo.endCityName);
            jsonObject.put(serviceRequest.pickUpLocation, "");
            jsonObject.put(serviceRequest.deliveryLocation, "");
            jsonObject.put(serviceRequest.productType, intraCityModel.packageType);
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
                                            .putExtra(AppConstant.INTENT_USER_TYPE, "FREE_USER")
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



    private void getDialogService_registered_user() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.phoneNumber, "" );
            jsonObject.put(serviceRequest.maxWeightValue, etEnterWeight.getText().toString());
            jsonObject.put(serviceRequest.pickUpCityName, intraCityModel.deliveryInfo.startCityName);
            jsonObject.put(serviceRequest.deliveryCityName, intraCityModel.deliveryInfo.endCityName);
            jsonObject.put(serviceRequest.pickUpLocation, "");
            jsonObject.put(serviceRequest.deliveryLocation, "");
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
     * StartCitySpinnerAdapter
     */
    private class StartCitySpinnerAdapter extends BaseAdapter{
        private Context context;
        private List<StartCityInfo> startCityInfo;
        private int resource;

        public StartCitySpinnerAdapter(Context context, int resource, List<StartCityInfo> startCityInfo) {
            this.context = context;
            this.startCityInfo = startCityInfo;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return startCityInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return startCityInfo.get(position);
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

            viewHolder.textSpinner.setText(startCityInfo.get(position).startCity);
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

    /**
     * EndCitySpinnerAdapter
     */
    private class EndCitySpinnerAdapter extends BaseAdapter{
        private Context context;
        private List<EndCityInfo> endCityInfo;
        private int resource;

        public EndCitySpinnerAdapter(Context context, int resource, List<EndCityInfo> endCityInfo) {
            this.context = context;
            this.endCityInfo = endCityInfo;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return endCityInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return endCityInfo.get(position);
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

            viewHolder.textSpinner.setText(endCityInfo.get(position).endCity);
            viewHolder.textSpinner.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

            return convertView;
        }

        class ViewHolder {
            private TextView textSpinner;
        }
    }

}
