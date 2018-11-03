package com.pikndel.fragment.signupFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.OtpActivity;
import com.pikndel.model.UserDetail;
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

/**
 * Created by govind_gautam on 29/4/16.
 */

public class SignupFragment4 extends Fragment implements TextWatcher {

    private LinearLayout llDone;
    private TextView tvConfirmPass, txtErrorOne, txtSignup;
    private EditText etPassword, etConfirmPassword;
    private Context context ;
    private LinearLayout llProgress;
    private View view;
    private PrefsManager prefsManager;
    private UserDetail userDetail = new UserDetail();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        prefsManager = new PrefsManager(context);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_signup_page4,null);

        CommonUtils.hideSoftKeyboard(context);
        if (getArguments() != null && getArguments().getSerializable(AppConstant.BUNDLE_USER_NAME) != null) {
            userDetail = (UserDetail) getArguments().getSerializable(AppConstant.BUNDLE_USER_NAME);
        }

        findIds();
        setTextAttributes();
        setListeners();

        return view;
    }

    private void setTextAttributes() {
        etPassword.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etConfirmPassword.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvConfirmPass.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtErrorOne.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtSignup.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void setListeners() {

        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    lastSignUpService();
                }
            }
        });

        etPassword.addTextChangedListener(this);
        etConfirmPassword.addTextChangedListener(this);
    }

    private void findIds() {
        txtSignup = (TextView) view.findViewById(R.id.txtSignup);
        llDone = (LinearLayout) view.findViewById(R.id.llDone);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) view.findViewById(R.id.etConfirmPassword);
        tvConfirmPass =(TextView) view.findViewById(R.id.tvConfirmPass);
        llProgress= (LinearLayout) view.findViewById(R.id.llProgress);
        txtErrorOne = (TextView) view.findViewById(R.id.txtErrorOne);
    }

    private boolean checkValidation() {
        if(etPassword.getText().toString().trim().isEmpty()){
            etPassword.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtErrorOne.setText("*Please enter password.");
            txtErrorOne.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
            return false;
        }else if(etPassword.getText().toString().trim().length()<4){
            etPassword.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtErrorOne.setText("*Password must be at least 4 characters.");
            txtErrorOne.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
            return false;
        } else if (etConfirmPassword.getText().toString().trim().isEmpty()) {
            tvConfirmPass.setText("*Please enter confirm password.");
            tvConfirmPass.setVisibility(View.VISIBLE);
            etConfirmPassword.requestFocus();
            return false;
        } else if (!CommonUtils.checkPassWordAndConfirmPassword(etPassword.getText().toString().trim(),etConfirmPassword.getText().toString().trim())) {
            tvConfirmPass.setText("*Password doesn't match.");
            etConfirmPassword.setBackgroundResource(R.drawable.rederror_shape);
            tvConfirmPass.setVisibility(View.VISIBLE);
            etConfirmPassword.requestFocus();
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
        if(etPassword.getText().toString().trim().length()!=0){
            etPassword.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            txtErrorOne.setVisibility(View.GONE);
        }
        if(etConfirmPassword.getText().toString().trim().length()!=0){
            etConfirmPassword.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvConfirmPass.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void lastSignUpService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.email, userDetail.email);
            jsonObject.put(serviceRequest.password, etPassword.getText().toString());
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("SignupFragment4", "_____Request_____" + RequestURL.URL_LAST_SIGN_UP);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_LAST_SIGN_UP, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                    }

                                    LogUtils.errorLog("SignupFragment4", ":::::<<<<<OTP>>>>>:::::"+serviceResponse.otp);
                                    startActivity(new Intent(context, OtpActivity.class)
                                            .putExtra(AppConstant.INTENT_OPT_SECTION, "SIGN_UP")
                                            .putExtra("OTP", serviceResponse.otp)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                } else if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.FAILURE_CODE_202)){
                                    //User is not verified
                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                    }

                                    LogUtils.errorLog("SignupFragment4", ":::::<<<<<OTP>>>>>:::::"+serviceResponse.otp);
                                    startActivity(new Intent(context, OtpActivity.class)
                                            .putExtra(AppConstant.INTENT_OPT_SECTION, "SIGN_UP")
                                            .putExtra("OTP", serviceResponse.otp)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    CommonUtils.showToast(context, serviceResponse.message);

                                }else {
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
