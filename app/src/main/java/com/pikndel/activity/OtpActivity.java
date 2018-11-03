package com.pikndel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.model.UserDetail;
import com.pikndel.model.UserPlanList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private TextView tvVerify, tvResetOtp, tvHeader, tvError,tvEnterOTP, tvReceive;
    private ImageView ivLeft, ivRight;
    private Context context=OtpActivity.this;
    private String section;
    private PrefsManager prefsManager;
    private List<UserPlanList> userPlanList = new ArrayList<>();
    private String otp;
    private static OtpActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        instance = OtpActivity.this;
        prefsManager = new PrefsManager(context);
        if (getIntent() != null){
            section = getIntent().getStringExtra(AppConstant.INTENT_OPT_SECTION);
            otp = getIntent().getStringExtra("OTP");
        }

        findIds();
        setListeners();
        setTextAttributes();
    }

    public void recivedSms(String message) {
        try {
            etOtp.setText(message);
            etOtp.setSelection(etOtp.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextAttributes() {

        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvEnterOTP.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etOtp.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvVerify.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvReceive.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvResetOtp.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvHeader.setText("Validation");
        ivRight.setVisibility(View.INVISIBLE);
        tvEnterOTP.setText(R.string.enterotpsend);
        //etOtp.setText(otp);
    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){
                    optService();
                }
            }
        });

        tvResetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtpService();
            }
        });

        etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etOtp.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findIds() {
        etOtp = (EditText) findViewById(R.id.etOtp);
        tvVerify = (TextView) findViewById(R.id.tvVerify);
        tvReceive = (TextView) findViewById(R.id.tvReceive);
        tvResetOtp = (TextView) findViewById(R.id.tvResetOtp);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvEnterOTP = (TextView) findViewById(R.id.tvEnterOTP);
        tvError = (TextView) findViewById(R.id.tvError);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);

    }


    private boolean checkValidation() {
        if (etOtp.getText().toString().isEmpty()) {
            tvError.setText("*Please enter OTP code.");
            etOtp.setBackgroundResource(R.drawable.rederror_shape);
            tvError.setVisibility(View.VISIBLE);
            etOtp.requestFocus();
            return false;
        } else if(etOtp.getText().toString().trim().length()<= 3){
            tvError.setText("*Please enter valid OTP code.");
            etOtp.setBackgroundResource(R.drawable.rederror_shape);
            tvError.setVisibility(View.VISIBLE);
            etOtp.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void resendOtpService() {
        try {
            String url = "";
            if (section.equalsIgnoreCase("FORGOT_PASSWORD")){
                url = String.format(RequestURL.URL_RESEND_OTP, prefsManager.getKeyUserId());
            }else {
                url = String.format(RequestURL.URL_RESEND_OTP, prefsManager.getKeyUserId());
            }
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("OtpActivity", "_____Request_____" + url);
                new ServiceAsync(context, true, jsonObject.toString(), url, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                    }
                                    CommonUtils.showToast(context, "New OTP sent.");
                                    LogUtils.errorLog("LoginActivity", ":::::<<<<<OTP>>>>>:::::"+serviceResponse.otp);

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

    private void optService() {
        try {
            String url = "";
            if (section.equalsIgnoreCase("FORGOT_PASSWORD")){
                url = RequestURL.URL_VERIFY_OTP;
            }else {
                url = RequestURL.URL_VERIFY_USER_BY_OTP;
            }
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest=new ServiceRequest();
            jsonObject.put(serviceRequest.userId,prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.otp,etOtp.getText().toString());
            if (!section.equalsIgnoreCase("FORGOT_PASSWORD")) {
                jsonObject.put(serviceRequest.deviceType, CommonUtils.getPreferences(context, AppConstant.DEVICE_TYPE));
                jsonObject.put(serviceRequest.androidDeviceKey, prefsManager.getKeyDeviceKey());
                jsonObject.put(serviceRequest.deviceToken, CommonUtils.getPreferences(context, AppConstant.DEVICE_TOKEN));
            }
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("OtpActivity", "_____Request_____" + url);
                new ServiceAsync(context, true, jsonObject.toString(), url, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (section.equalsIgnoreCase("FORGOT_PASSWORD")){
                                        startActivity(new Intent(context, ResetPasswordActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    }else {
                                        UserDetail userDetail = new UserDetail();
                                        userDetail.name = TextUtils.isEmpty(serviceResponse.name)?"":serviceResponse.name;
                                        userDetail.email = TextUtils.isEmpty(serviceResponse.email)?"":serviceResponse.email;
                                        userDetail.phoneNumber = TextUtils.isEmpty(serviceResponse.phoneNumber)?"":serviceResponse.phoneNumber;
                                        userDetail.profileImage = TextUtils.isEmpty(serviceResponse.profileImage)?"":serviceResponse.profileImage;
                                        userDetail.userId = TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId;
                                        prefsManager.setKeyUserId(TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId);
                                        getPlanListService();
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

    private void getPlanListService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("OtpActivity", "_____Request_____" + RequestURL.URL_GET_PLAN_LIST);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_PLAN_LIST, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                userPlanList.clear();
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.userPlanList != null && serviceResponse.userPlanList.size()>0){
                                        userPlanList.addAll(serviceResponse.userPlanList);
                                        if (userPlanList.size()>1){
                                            startActivity(new Intent(context, PlanActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }else if (userPlanList.size()>0){
                                            if (userPlanList.get(0).amount == 0.0)
                                                saveUserPlanService(userPlanList.get(0).planId);
                                        }
                                    }else {
                                        CommonUtils.showToast(context, serviceResponse.message);
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

    private void saveUserPlanService(String planId) {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("OtpActivity", "_____Request_____" + String.format(RequestURL.URL_SAVE_USER_PLAN, prefsManager.getKeyUserId(), planId));
                new ServiceAsync(context, true, jsonObject.toString(),
                        String.format(RequestURL.URL_SAVE_USER_PLAN,  prefsManager.getKeyUserId(), planId),
                        RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    prefsManager.setKeyUserId(TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId);
                                    prefsManager.setLoggedIn(true);
                                    prefsManager.setKeyPlanId(TextUtils.isEmpty(serviceResponse.planId)?"":serviceResponse.planId);
                                    prefsManager.setKeyWalletBalance(TextUtils.isEmpty(serviceResponse.walletBalance)?"0.0":serviceResponse.walletBalance);

                                    startActivity(new Intent(context, HomeActivity.class)
                                            .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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

    public static OtpActivity instance() {
        return instance;
    }
}
