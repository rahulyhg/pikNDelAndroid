package com.pikndel.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.pikndel.R;
import com.pikndel.listeners.OnTransactionListner;
import com.pikndel.model.IntraCityModel;
import com.pikndel.model.TransactionDetails;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.Utils;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

public class MoreInformationActivity extends AppCompatActivity implements TextWatcher {

    private Context context= MoreInformationActivity.this;

    private TextView tvHeader, tvPickUpFrom, tvPickName, tvDeliverTO, tvDeliverName, tvEstimate, tvApply
            , tvEstimationDis, tvTotalCostStatic, tvTotalCost , tvPay, tvTermsConditions, tvConfirmPayment, tvErrorTerms, tvErrorPromocode;
    private CheckBox cbTermsConditions;
    private RadioButton rbCreditCard, rbWallet, rbCash, rbDelivery;
    private ImageView ivLeft, ivRight,ivInfo;
    private EditText etPromoCode;
    private LinearLayout llLocation, llEstimateDistance, llAllData, llPromoCode;
    private IntraCityModel intraCityModel;
    private PrefsManager prefsManager;
    private RadioGroup rgPayment;
    private float walletAmount = 0.0f;
    String[] s,s1;
    private float updateWalletAmount = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        prefsManager = new PrefsManager(context);
        if (getIntent() != null && getIntent().getSerializableExtra("INTER_CITY_DELIVERY") != null){
            intraCityModel = (IntraCityModel) getIntent().getSerializableExtra("INTER_CITY_DELIVERY");
        }

        findIds();
        setFont();
        setListener();
        settingData(intraCityModel);
        //walletBalanceService();
    }

    private void settingData(IntraCityModel intraCityModel){
        walletAmount = Float.parseFloat(TextUtils.isEmpty(prefsManager.getKeyWalletBalance())?"0":prefsManager.getKeyWalletBalance());
        rbWallet.setText("Wallet: ₹ " + (TextUtils.isEmpty(prefsManager.getKeyWalletBalance()) ? "0" : prefsManager.getKeyWalletBalance()));

        Log.e("wallet balance  :", "" + walletAmount);
        if (intraCityModel != null){

            intraCityModel.finalCost = intraCityModel.waysToPikndelInfo == null?"0"
                    :TextUtils.isEmpty(intraCityModel.waysToPikndelInfo.deliveryCost)?"0"
                    :intraCityModel.waysToPikndelInfo.deliveryCost;

            if(intraCityModel.deliveryType.equalsIgnoreCase("intracity")){

                if (intraCityModel.deliveryInfo.isMultiLocation.equalsIgnoreCase("1")){
                    tvDeliverName.setText("Multiple Locations");
                }
                else {
                    s1 = intraCityModel.deliveryInfo.deliverylocation.split(",");
                    tvDeliverName.setText(intraCityModel.deliveryInfo == null ? "No Data Available" :
                            TextUtils.isEmpty(intraCityModel.deliveryInfo.deliverylocation) ? "No Data Available" : s1[0]);
                }

                s = intraCityModel.deliveryInfo.pickupLocation.split(",");
                tvPickName.setText(intraCityModel.deliveryInfo == null ?"No Data Available":
                        TextUtils.isEmpty(intraCityModel.deliveryInfo.pickupLocation)?"No Data Available":s[0]);
                if (intraCityModel.deliveryInfo.isMultiLocation.equalsIgnoreCase("0")){
                    if (!TextUtils.isEmpty(intraCityModel.distanceKm)) {

                        double distance = Double.parseDouble(intraCityModel.distanceKm);
                        int dist = (int) Math.ceil(distance);

                        tvEstimationDis.setText(TextUtils.isEmpty(intraCityModel.distanceKm) ? "0 km" : dist + " kms");

                    }

                }
            }

            else {



                tvDeliverName.setText(intraCityModel.deliveryInfo == null?"No Data Available":
                        TextUtils.isEmpty(intraCityModel.deliveryInfo.endCityName)?"No Data Available":intraCityModel.deliveryInfo.endCityName);
                tvPickName.setText(intraCityModel.deliveryInfo == null ?"No Data Available":
                        TextUtils.isEmpty(intraCityModel.deliveryInfo.startCityName)?"No Data Available":intraCityModel.deliveryInfo.startCityName);
            }
            if (intraCityModel.deliveryInfo.isMultiLocation.equalsIgnoreCase("0")) {
                tvTotalCost.setText(intraCityModel.waysToPikndelInfo == null ? "₹ 0.00" :
                        TextUtils.isEmpty(intraCityModel.waysToPikndelInfo.deliveryCost) ? "₹ 0.00" : "₹ " + String.format(Locale.US, "%.2f", Float.parseFloat(intraCityModel.waysToPikndelInfo.deliveryCost)));
            }
        }
    }

    private void setListener() {

        tvConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    /*if (intraCityModel.paymentMode.equalsIgnoreCase("PAYTM")){
                        payTmPaymentGateWay(intraCityModel.finalCost);
                    }else*/
                    if(intraCityModel.paymentMode.equals("PAYTM")){
                        payUsingPaytm(Float.parseFloat(intraCityModel.finalCost), false);
                    }else if(intraCityModel.paymentMode.equals("WALLET")){
                        if(Float.parseFloat(intraCityModel.finalCost) > walletAmount){
                            Log.e("wallet amount  :"+walletAmount, "payment   :"+intraCityModel.finalCost);
                            updateWalletAmount = walletAmount;
                            payUsingPaytm((Float.parseFloat(intraCityModel.finalCost) - walletAmount), true);
                        }else{
                            float newWalletAmount = walletAmount - (Float.parseFloat(intraCityModel.finalCost));
                            updateWalletAmount = Float.parseFloat(intraCityModel.finalCost);
                            Log.e("wallet amount  :" + walletAmount, "payment   :" + intraCityModel.finalCost);
                            userPlaceOrderService(Utils.initOrderId(), true, newWalletAmount);
                            //updateWalletBalanceService(intraCityModel.finalCost, String.valueOf(System.currentTimeMillis()/1000));
                            //userPlaceOrderService();
                        }
                    }else {
                        userPlaceOrderService(Utils.initOrderId(), false, 0.0f);
                    }

                }
            }
        });

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCoupenValidation()) {
                    if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")){
                        CommonUtils.showToast(context, "Register or login to apply coupon.");
                    }else {
                        userValidateCoupenCodeService();
                    }
                }
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

        tvTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MoreInformationActivity.this, TermsConditionsActivity.class);
                startActivity(intent);

            }
        });

        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MoreInfoDetailActivity.class)
                        .putExtra("INTER_CITY_DELIVERY", intraCityModel)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_up, 0);
            }
        });

        etPromoCode.addTextChangedListener(this);

        cbTermsConditions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbTermsConditions.isChecked()) {
                    tvErrorTerms.setVisibility(View.GONE);
                }
            }
        });

        rgPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCreditCard:
                        intraCityModel.paymentMode = "PAYTM";
                        break;
                    case R.id.rbWallet:
                        intraCityModel.paymentMode = "WALLET";
                        break;
                    case R.id.rbCash:
                        intraCityModel.paymentMode = "COP";
                        break;
                    case R.id.rbDelivery:
                        intraCityModel.paymentMode = "COD";
                        break;
                }
            }
        });
    }

    private void payUsingPaytm(float amount, final boolean fromWallet) {
        Log.e("payment  :"+fromWallet, ""+amount);

        new Utils(context, String.format("%.2f", amount), new OnTransactionListner() {

            @Override
            public void onTransactionSuccess(TransactionDetails transactionDetails) {
                if (!CommonUtils.isOnline(context)) {
                    CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
                }else {
                    if (fromWallet) {
                        userPlaceOrderService(transactionDetails.ORDER_ID, true, 0.0f);
                        //updateWalletBalanceService(walletAmount, transactionDetails.ORDER_ID);
                    } else {
                        userPlaceOrderService(transactionDetails.ORDER_ID, false, 0.0f);
                    }
                }
            }

            @Override
            public void onTransactionfailure(String failureString) {
                Log.e("reason   ", ""+failureString);
                CommonUtils.showToast(context, failureString);
            }
        }).startTransaction();
    }



    private void setFont() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvPickUpFrom.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPickName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvDeliverTO.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorPromocode.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliverName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvEstimate.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEstimationDis.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvTotalCostStatic.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvTotalCost.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvApply.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etPromoCode.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPay.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        rbCreditCard.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        rbWallet.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        rbCash.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        rbDelivery.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        cbTermsConditions.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvTermsConditions.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvConfirmPayment.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorTerms.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvHeader.setText("Order Confirmation");
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);

        if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")){
            rbWallet.setVisibility(View.GONE);
            if (intraCityModel.deliveryType.equalsIgnoreCase("intercity")) {
                rbDelivery.setVisibility(View.GONE);
                llEstimateDistance.setVisibility(View.GONE);
            } else if (intraCityModel.deliveryType.equalsIgnoreCase("intracity")) {
                //TODO
            }
        }else {
            if (intraCityModel.deliveryType.equalsIgnoreCase("intercity")) {
                rbDelivery.setVisibility(View.GONE);
                llEstimateDistance.setVisibility(View.GONE);
            } else if (intraCityModel.deliveryType.equalsIgnoreCase("intracity")) {
                if (intraCityModel.deliveryInfo.isMultiLocation.equalsIgnoreCase("1")){
                    llPromoCode.setVisibility(View.GONE);
                    llAllData.setVisibility(View.GONE);
                    rbCreditCard.setVisibility(View.GONE);
                    rbWallet.setVisibility(View.GONE);
                }else {
                    //TODO
                }
            }
        }
    }



    private void findIds() {
        tvHeader = (TextView)findViewById(R.id.tvHeader);
        llLocation = (LinearLayout) findViewById(R.id.llLocation);
        llPromoCode = (LinearLayout) findViewById(R.id.llPromoCode);
        llAllData = (LinearLayout) findViewById(R.id.llAllData);
        llEstimateDistance = (LinearLayout) findViewById(R.id.llEstimateDistance);
        ivInfo = (ImageView) findViewById(R.id.ivInfo);
        tvErrorTerms = (TextView)findViewById(R.id.tvErrorTerms);
        tvConfirmPayment = (TextView)findViewById(R.id.tvConfirmPayment);
        tvTermsConditions = (TextView)findViewById(R.id.tvTermsConditions);
        tvPickUpFrom = (TextView)findViewById(R.id.tvPickUpFrom);
        tvPickName = (TextView)findViewById(R.id.tvPickName);
        tvDeliverTO = (TextView)findViewById(R.id.tvDeliverTO);
        tvDeliverName = (TextView)findViewById(R.id.tvDeliverName);
        tvErrorPromocode = (TextView)findViewById(R.id.tvErrorPromocode);
        tvEstimate = (TextView)findViewById(R.id.tvEstimate);
        tvEstimationDis = (TextView)findViewById(R.id.tvEstimationDis);
        tvTotalCostStatic = (TextView)findViewById(R.id.tvTotalCostStatic);
        tvTotalCost = (TextView)findViewById(R.id.tvTotalCost);
        etPromoCode = (EditText) findViewById(R.id.etPromoCode);
        tvApply = (TextView)findViewById(R.id.tvApply);
        tvPay = (TextView)findViewById(R.id.tvPay);
        rbCreditCard = (RadioButton) findViewById(R.id.rbCreditCard);
        rbWallet = (RadioButton) findViewById(R.id.rbWallet);
        rbCash = (RadioButton) findViewById(R.id.rbCash);
        rbDelivery = (RadioButton) findViewById(R.id.rbDelivery);
        cbTermsConditions = (CheckBox) findViewById(R.id.cbTermsConditions);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        rgPayment = (RadioGroup) findViewById(R.id.rgPayment);
    }

    private boolean checkCoupenValidation() {
        if(etPromoCode.getText().toString().trim().isEmpty()) {
            tvErrorPromocode.setVisibility(View.VISIBLE);
            etPromoCode.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorPromocode.setText("*Please enter promo code.");
            etPromoCode.requestFocus();
            return false;
        } else if(etPromoCode.getText().toString().trim().length()<4) {
            tvErrorPromocode.setVisibility(View.VISIBLE);
            etPromoCode.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorPromocode.setText("*Please enter valid promo code.");
            etPromoCode.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(intraCityModel.paymentMode)){
            CommonUtils.showToast(context, "Please select payment mode.");
            return false;
        }else if(!cbTermsConditions.isChecked()){
            tvErrorTerms.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(etPromoCode.getText().toString().trim().length()>0){
            tvErrorPromocode.setVisibility(View.GONE);
            etPromoCode.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
    }

    private String coupenId="0";
    private void userValidateCoupenCodeService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.coupenCode, etPromoCode.getText().toString().trim());
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MoreInformationActivity", "_____Request_____" + RequestURL.URL_USER_VALIDATE_COUPEN_CODE);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_USER_VALIDATE_COUPEN_CODE, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.discountPercent >0) {
                                        float deliveryCost = Float.parseFloat(intraCityModel.waysToPikndelInfo.deliveryCost);
                                        if (deliveryCost>serviceResponse.minValue) {
                                            float disc = (serviceResponse.discountPercent * deliveryCost) / 100;
                                            if (disc>serviceResponse.maxValue){
                                                disc = serviceResponse.maxValue;
                                            }
                                            float finalAmt = deliveryCost - disc;
                                            if (finalAmt<serviceResponse.minValue){
                                                finalAmt = serviceResponse.minValue;
                                            }
                                            intraCityModel.finalCost = String.valueOf(finalAmt);
                                            tvTotalCost.setText(TextUtils.isEmpty(intraCityModel.finalCost) ? "₹ 0.00" : "₹ " + intraCityModel.finalCost);
                                            coupenId = serviceResponse.coupenId;
                                            CommonUtils.showToast(context, "Coupon successfully applied.");
                                        }else {
                                            CommonUtils.showToast(context, "Coupon not applicable.");
                                        }
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

    private void walletBalanceService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MyWalletFragment", "_____Request_____" + String.format(RequestURL.URL_WALLET_BALANCE, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_WALLET_BALANCE, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    if(!TextUtils.isEmpty(serviceResponse.walletBalanceData)){
                                        rbWallet.setText("Wallet: ₹ "+serviceResponse.walletBalanceData);
                                        walletAmount = Float.parseFloat(serviceResponse.walletBalanceData);
                                        prefsManager.setKeyWalletBalance(serviceResponse.walletBalance);

                                    }else {
                                        rbWallet.setText("Wallet: ₹ 0");
                                        prefsManager.setKeyWalletBalance("0");
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

    private void userPlaceOrderService(final String successString, final boolean isWallet, final float newWalletAmount) {
        try {

            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();

            jsonObject.put(serviceRequest.deliveryType, intraCityModel.deliveryType);
            jsonObject.put(serviceRequest.userId,  prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.instructionToRider, TextUtils.isEmpty(intraCityModel.instructionToRider)?"":intraCityModel.instructionToRider );
            if (intraCityModel.deliveryInfo.isMultiLocation.equalsIgnoreCase("0")) {
                jsonObject.put(serviceRequest.finalCost, Float.parseFloat(intraCityModel.finalCost));
            }else {
                jsonObject.put(serviceRequest.finalCost, 0);
            }
            jsonObject.put(serviceRequest.paymentMode,TextUtils.isEmpty(intraCityModel.paymentMode)?"":intraCityModel.paymentMode);
            jsonObject.put(serviceRequest.picture,TextUtils.isEmpty(intraCityModel.picture)?"":intraCityModel.picture);
            jsonObject.put(serviceRequest.transactionId, successString);
            jsonObject.put(serviceRequest.coupenId,coupenId);
//            CommonUtils.savePreferencesString(context, successString,AppConstant.TRANSACTION_ID);

            JSONObject jsonObjectDelivery = new JSONObject();
            jsonObjectDelivery.put(serviceRequest.startCityId, intraCityModel.deliveryInfo.startCityId);
            CommonUtils.savePreferencesString(context, intraCityModel.deliveryInfo.startCityId, AppConstant.CITY_ID_PICKUP);
            jsonObjectDelivery.put(serviceRequest.endCityId, intraCityModel.deliveryInfo.endCityId);
            CommonUtils.savePreferencesString(context, intraCityModel.deliveryInfo.endCityId, AppConstant.CITY_ID_DELIVER);
            jsonObjectDelivery.put(serviceRequest.productTypeId, Integer.parseInt(TextUtils.isEmpty(intraCityModel.deliveryInfo.productTypeId) ? "0" : intraCityModel.deliveryInfo.productTypeId));
            jsonObjectDelivery.put(serviceRequest.packageWeightId, Integer.parseInt(TextUtils.isEmpty(intraCityModel.deliveryInfo.packageWeightId) ? "0" : intraCityModel.deliveryInfo.packageWeightId));
            jsonObjectDelivery.put(serviceRequest.packageWeight, Float.parseFloat(TextUtils.isEmpty(intraCityModel.deliveryInfo.packageWeight) ? "0.0" : intraCityModel.deliveryInfo.packageWeight));
            jsonObjectDelivery.put(serviceRequest.codAmount, Float.parseFloat(TextUtils.isEmpty(intraCityModel.deliveryInfo.codAmount)?"0":intraCityModel.deliveryInfo.codAmount));
            jsonObjectDelivery.put(serviceRequest.pickupLocation, intraCityModel.deliveryInfo.pickupLocation);
            jsonObjectDelivery.put(serviceRequest.deliverylocation, intraCityModel.deliveryInfo.deliverylocation);
            jsonObjectDelivery.put(serviceRequest.isMultiLocation, Integer.parseInt(TextUtils.isEmpty(intraCityModel.deliveryInfo.isMultiLocation)?"0":intraCityModel.deliveryInfo.isMultiLocation));
            jsonObjectDelivery.put(serviceRequest.locationCount, Integer.parseInt(TextUtils.isEmpty(intraCityModel.deliveryInfo.locationCount)?"0":intraCityModel.deliveryInfo.locationCount));
            jsonObjectDelivery.put(serviceRequest.isRoundTrip, Integer.parseInt(TextUtils.isEmpty(intraCityModel.deliveryInfo.isRoundTrip)?"0":intraCityModel.deliveryInfo.isRoundTrip));

            JSONObject jsonObjectWayPic = new JSONObject();
            jsonObjectWayPic.put(serviceRequest.packageTypeId, intraCityModel.waysToPikndelInfo == null?0:Integer.parseInt(TextUtils.isEmpty(intraCityModel.waysToPikndelInfo.packageTypeId) ? "0" : intraCityModel.waysToPikndelInfo.packageTypeId));
            jsonObjectWayPic.put(serviceRequest.packageTypeName, intraCityModel.waysToPikndelInfo == null?"":intraCityModel.waysToPikndelInfo.packageTypeName);
            jsonObjectWayPic.put(serviceRequest.deliveryByTime, intraCityModel.waysToPikndelInfo == null?"00:00":intraCityModel.waysToPikndelInfo.deliveryByTime);
            jsonObjectWayPic.put(serviceRequest.deliveryByDate, intraCityModel.waysToPikndelInfo == null?0:Long.parseLong(TextUtils.isEmpty(intraCityModel.waysToPikndelInfo.deliveryByDate) ? "0" : intraCityModel.waysToPikndelInfo.deliveryByDate));
            jsonObjectWayPic.put(serviceRequest.deliveryCost, intraCityModel.waysToPikndelInfo == null?0:Float.parseFloat(TextUtils.isEmpty(intraCityModel.waysToPikndelInfo.deliveryCost) ? "0" : intraCityModel.waysToPikndelInfo.deliveryCost));

            JSONObject jsonObjectPickUp = new JSONObject();
            jsonObjectPickUp.put(serviceRequest.contactName, intraCityModel.pickupAddress == null ?"":TextUtils.isEmpty(intraCityModel.pickupAddress.contactName)?"":intraCityModel.pickupAddress.contactName);
            jsonObjectPickUp.put(serviceRequest.contactNumber, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.contactNumber)?"":intraCityModel.pickupAddress.contactNumber);
            jsonObjectPickUp.put(serviceRequest.houseNumber, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.houseNumber)?"":intraCityModel.pickupAddress.houseNumber);
            jsonObjectPickUp.put(serviceRequest.floor, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.floor)?"":intraCityModel.pickupAddress.floor);
            jsonObjectPickUp.put(serviceRequest.landMark, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.landmark)?"":intraCityModel.pickupAddress.landmark);
            jsonObjectPickUp.put(serviceRequest.area, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.area)?"":intraCityModel.pickupAddress.area);
            jsonObjectPickUp.put(serviceRequest.cityId, intraCityModel.pickupAddress == null ?intraCityModel.deliveryInfo.startCityId: TextUtils.isEmpty(intraCityModel.pickupAddress.cityId)?"":intraCityModel.pickupAddress.cityId);
//            jsonObjectPickUp.put(serviceRequest.cityId, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.cityId)?"":intraCityModel.pickupAddress.cityId);
            jsonObjectPickUp.put(serviceRequest.pinCode, intraCityModel.pickupAddress == null ?"": TextUtils.isEmpty(intraCityModel.pickupAddress.pincode)?"":intraCityModel.pickupAddress.pincode);
            jsonObjectPickUp.put(serviceRequest.isFavourite, intraCityModel.pickupAddress == null ?"0": TextUtils.isEmpty(intraCityModel.pickupAddress.isFavourite)?"":intraCityModel.pickupAddress.isFavourite);
//            jsonObjectPickUp.put(serviceRequest.isFavourite, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.isFavourite)?"":intraCityModel.deliveryAddress.isFavourite);


            JSONObject jsonObjectDeliveryAddr = new JSONObject();
            jsonObjectDeliveryAddr.put(serviceRequest.contactName, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.contactName)?"":intraCityModel.deliveryAddress.contactName);
            jsonObjectDeliveryAddr.put(serviceRequest.contactNumber, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.contactNumber)?"":intraCityModel.deliveryAddress.contactNumber);
            jsonObjectDeliveryAddr.put(serviceRequest.houseNumber, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.houseNumber)?"":intraCityModel.deliveryAddress.houseNumber);
            jsonObjectDeliveryAddr.put(serviceRequest.floor, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.floor)?"":intraCityModel.deliveryAddress.floor);
            jsonObjectDeliveryAddr.put(serviceRequest.landMark, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.landmark)?"":intraCityModel.deliveryAddress.landmark);
            jsonObjectDeliveryAddr.put(serviceRequest.area, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.area)?"":intraCityModel.deliveryAddress.area);
            jsonObjectDeliveryAddr.put(serviceRequest.cityId, intraCityModel.deliveryAddress == null ?intraCityModel.deliveryInfo.endCityId: TextUtils.isEmpty(intraCityModel.deliveryAddress.cityId)?"":intraCityModel.deliveryAddress.cityId);
            jsonObjectDeliveryAddr.put(serviceRequest.pinCode, intraCityModel.deliveryAddress == null ?"": TextUtils.isEmpty(intraCityModel.deliveryAddress.pincode)?"":intraCityModel.deliveryAddress.pincode);
            jsonObjectDeliveryAddr.put(serviceRequest.isFavourite, intraCityModel.deliveryAddress == null ?"0": TextUtils.isEmpty(intraCityModel.deliveryAddress.isFavourite)?"":intraCityModel.deliveryAddress.isFavourite);


            jsonObject.put("deliveryInfo", jsonObjectDelivery);
            jsonObject.put("waysToPikndelInfo", jsonObjectWayPic);
            jsonObject.put("pickupAddress", jsonObjectPickUp);
            jsonObject.put("deliveryAddress", jsonObjectDeliveryAddr);

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MoreInformationActivity", "_____Request_____" + RequestURL.URL_USER_PLACE_ORDER);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_USER_PLACE_ORDER, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    if (isWallet){
                                        updateWalletBalanceService(newWalletAmount, serviceResponse.referenceNumber, successString);
                                    }else {
                                        // walletBalanceService();
                                        doneDialog(serviceResponse.referenceNumber);
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




    public void doneDialog(String referenceNumber)  {
        final Dialog customDialog = DialogUtils.createCustomDialog(context, R.layout.dialog_volumetric_weight);

        TextView tvOk = (TextView) customDialog.findViewById(R.id.tvOk);
        TextView tvCase = (TextView) customDialog.findViewById(R.id.tvCase);
        TextView tvCalculate = (TextView) customDialog.findViewById(R.id.tvCalculate);

        tvCalculate.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvOk.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvCase.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));

        if (prefsManager.getKeyUserType().equalsIgnoreCase("")){
            tvCase.setText("Your Reference No.:"+ referenceNumber +", sign up and get 10% off.");
        }else {
            tvCase.setText("Your order placed successfully with Reference Id :"+referenceNumber+" ");
        }

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                customDialog.dismiss();
                if(prefsManager.getKeyUserId().equals("-1")){
                    startActivity(new Intent(context, HomeActivity.class)
                            .putExtra(AppConstant.INTENT_USER_TYPE, "FREE_USER")
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }else {
                    startActivity(new Intent(context, HomeActivity.class)
                            .putExtra(AppConstant.INTENT_USER_TYPE, "PLACED_ORDER")
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }


            }
        });
    }

    private String initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        return orderId;
    }

    private void updateWalletBalanceService(final float newWalletAmount, final String referenceNumber, String successString) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", prefsManager.getKeyUserId());
            jsonObject.addProperty("amount", updateWalletAmount);
            jsonObject.addProperty("sign", "-");
            jsonObject.addProperty("transactionId",successString );
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MyWalletFragment", "_____Request_____" + RequestURL.URL_UPDATE_WALLET);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_UPDATE_WALLET, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

//                                    if (!TextUtils.isEmpty(serviceResponse.walletBalance)) {
//                                        rbWallet.setText("Wallet: ₹ " + serviceResponse.walletBalanceData);
//                                        prefsManager.setKeyWalletBalance(serviceResponse.walletBalance);
//
//                                    } else {
//                                        rbWallet.setText("Wallet: ₹ 0");
//                                        prefsManager.setKeyWalletBalance("0");
//                                    }
//                                    walletBalanceService();
                                    rbWallet.setText("Wallet: ₹ "+newWalletAmount);
                                    prefsManager.setKeyWalletBalance(String.valueOf(newWalletAmount));
                                    doneDialog(referenceNumber);

                                }


                                else {
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

}
