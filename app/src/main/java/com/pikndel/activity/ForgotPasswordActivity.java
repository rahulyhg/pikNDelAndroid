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

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextView tvHeader,tvSubmit,tvError, tvForgot;
    private ImageView ivLeft, ivRight ;
    private EditText etEmail;
    private Context context=ForgotPasswordActivity.this;
    private PrefsManager prefsManager;
    private String phoneNumber="",email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        prefsManager  = new PrefsManager(context);

        findIds();
        setListeners();
        setTextAttributes();
    }

    private void findIds() {
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        tvForgot=(TextView) findViewById(R.id.tvForgot);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        tvSubmit=(TextView) findViewById(R.id.tvSubmit);
        tvError=(TextView) findViewById(R.id.tvError);
        etEmail=(EditText) findViewById(R.id.etMobileNo);

    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvForgot.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvSubmit.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvHeader.setText(R.string.forgot_password);
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.INVISIBLE);

    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){
                    userForgotPassword();
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkValidation() {
        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            tvError.setText(getString(R.string.enter_email_id));
            tvError.setVisibility(View.VISIBLE);
            etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
            etEmail.requestFocus();
            return false;
        } else if (Character.isLetter(etEmail.getText().toString().trim().charAt(0))) {
            if (!CommonUtils.isValidEmail(etEmail.getText().toString().trim())) {
                tvError.setText(getString(R.string.enter_valid_email_id));
                tvError.setVisibility(View.VISIBLE);
                etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
                etEmail.requestFocus();
                return false;
            } else {
                email=etEmail.getText().toString().trim();
                return true;
            }
        } else if (Character.isDigit(etEmail.getText().toString().trim().charAt(0))) {
            if (!(etEmail.getText().toString().length() == 10)) {
                tvError.setText(getString(R.string.enter_valid_number));
                tvError.setVisibility(View.VISIBLE);
                etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
                etEmail.requestFocus();
                return false;
            } else {
                phoneNumber=etEmail.getText().toString().trim();
                return true;
            }
        }
        return true;
    }

    private void userForgotPassword() {
        try {

            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest=new ServiceRequest();
            jsonObject.put(serviceRequest.phoneNumber,phoneNumber);
            jsonObject.put(serviceRequest.email,email);
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("ForgotPasswordActivity", "_____Request_____" + RequestURL.URL_POST_FORGOT_PASSWORD);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_POST_FORGOT_PASSWORD, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    LogUtils.errorLog("ForgotPasswordActivity", ":::::<<<<<accessCode>>>>>:::::"+serviceResponse.accessCode);
                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                    }

                                    startActivity(new Intent(context, OtpActivity.class)
                                            .putExtra(AppConstant.INTENT_OPT_SECTION, "FORGOT_PASSWORD")
                                            .putExtra("OTP", serviceResponse.accessCode)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();

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

}
