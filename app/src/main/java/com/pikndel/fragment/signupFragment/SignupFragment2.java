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
import com.pikndel.activity.SignUpActivity;
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

public class SignupFragment2 extends Fragment {

    private LinearLayout llDone,llMobileNumber;

    private TextView txtErrorOne, txtSignup, tvNumber;
    private EditText etMobileNumber;
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
        view = LayoutInflater.from(context).inflate(R.layout.fragment_signup_page2,null);

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
        txtSignup.setText("Hey "+userDetail.name+", where can we contact you?");

        etMobileNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtSignup.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtErrorOne.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void setListeners() {
        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    firstSignUpService();
                }
            }
        });

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                llMobileNumber.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                txtErrorOne.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findIds() {
        txtSignup = (TextView) view.findViewById(R.id.txtSignup);
        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        llDone = (LinearLayout) view.findViewById(R.id.llDone);
        txtErrorOne =(TextView) view.findViewById(R.id.txtErrorOne);
        llMobileNumber= (LinearLayout) view.findViewById(R.id.llMobileNumber);
        etMobileNumber= (EditText) view.findViewById(R.id.etMobileNumber);
        llProgress= (LinearLayout) view.findViewById(R.id.llProgress);
    }

    private boolean checkValidation() {
        if(etMobileNumber.getText().toString().trim().isEmpty()){
            llMobileNumber.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtErrorOne.setText("*Please enter mobile no.");
            txtErrorOne.setVisibility(View.VISIBLE);
            return false;
        }else if(etMobileNumber.getText().toString().trim().length()<10){
            llMobileNumber.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtErrorOne.setText("*Please enter valid mobile no.");
            txtErrorOne.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }

    private void firstSignUpService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.name, userDetail.name);
            jsonObject.put(serviceRequest.phoneNumber, etMobileNumber.getText().toString().trim());

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("SignupFragment2", "_____Request_____" + RequestURL.URL_FIRST_SIGN_UP);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_FIRST_SIGN_UP, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                        userDetail.phoneNumber = etMobileNumber.getText().toString().trim();
                                        ((SignUpActivity)context).onFragmentChangeListener(2, userDetail);
                                    }else {
                                        CommonUtils.showToast(context, "Error. Try again!");
                                    }

                                } else if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.FAILURE_CODE_205)) {
                                    //User's Sign Up is not complete
                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                        ((SignUpActivity)context).onFragmentChangeListener(2, userDetail);
                                    }
                                    CommonUtils.showToast(context, serviceResponse.message);
                                }else if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.FAILURE_CODE_202)){
                                    //User is not verified
                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                    }

                                    LogUtils.errorLog("LoginActivity", ":::::<<<<<OTP>>>>>:::::"+serviceResponse.otp);
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
