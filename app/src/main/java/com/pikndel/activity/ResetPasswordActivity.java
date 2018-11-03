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
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity implements TextWatcher {

    private EditText etPswd,etConfirmPswd;
    private TextView tvConfirm,tvHeader,tvErrorPswd,tvErrorConfPswd;
    private ImageView ivLeft;
    private Context context=ResetPasswordActivity.this;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        prefsManager=new PrefsManager(context);
        findIds();
        setTextAttributes();
        setListeners();
    }
    private void findIds() {
        tvConfirm=(TextView)findViewById(R.id.tvConfirm);
        tvHeader=(TextView)findViewById(R.id.tvHeader);
        tvErrorPswd=(TextView)findViewById(R.id.tvErrorPswd);
        tvErrorConfPswd=(TextView)findViewById(R.id.tvErrorConfPswd);
        etPswd=(EditText)findViewById(R.id.etPswd);
        etConfirmPswd=(EditText)findViewById(R.id.etConfirmPswd);
        ImageView ivRight = (ImageView) findViewById(R.id.ivRight);
        ivLeft=(ImageView)findViewById(R.id.ivLeft);
        ivRight.setVisibility(View.INVISIBLE);
        tvHeader.setText("Reset Password");
    }

    private void setTextAttributes() {

        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        etPswd.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etConfirmPswd.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvConfirm.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorPswd.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorConfPswd.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){
                    resetPasswordService();
                }
            }
        });

        etPswd.addTextChangedListener(this);
        etConfirmPswd.addTextChangedListener(this);
    }

    private boolean checkValidation() {
        if (TextUtils.isEmpty(etPswd.getText().toString())) {
            tvErrorPswd.setText("*Please enter new password.");
            etPswd.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorPswd.setVisibility(View.VISIBLE);
            etPswd.requestFocus();
            return false;
        }else if (etPswd.getText().toString().trim().length()<4) {
            tvErrorPswd.setText(getString(R.string.enter_valid_password));
            etPswd.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorPswd.setVisibility(View.VISIBLE);
            etPswd.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etConfirmPswd.getText().toString())) {
            tvErrorConfPswd.setText("*Please enter confirm password.");
            etConfirmPswd.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorConfPswd.setVisibility(View.VISIBLE);
            etConfirmPswd.requestFocus();
            return false;
        } else if (!TextUtils.equals(etConfirmPswd.getText().toString(), etPswd.getText().toString())) {
            tvErrorConfPswd.setText("*Please enter valid confirm password .");
            etConfirmPswd.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorConfPswd.setVisibility(View.VISIBLE);
            etConfirmPswd.requestFocus();
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
        tvErrorPswd.setVisibility(View.GONE);
        tvErrorConfPswd.setVisibility(View.GONE);
        etPswd.setBackgroundResource(R.drawable.rectangle_rounded_corner);
        etConfirmPswd.setBackgroundResource(R.drawable.rectangle_rounded_corner);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void resetPasswordService() {
        try {

            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest=new ServiceRequest();
            jsonObject.put(serviceRequest.userId,prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.password,etPswd.getText().toString());
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("ResetPasswordActivity", "_____Request_____" + RequestURL.URL_RESET_PASSWORD);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_RESET_PASSWORD, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    startActivity(new Intent(context, LoginActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK));
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
