package com.pikndel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.pikndel.R;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity{

    private TextView tvLoginWithFacebook,tvLogin,tvForgotPassword,tvErrorEmail,tvErrorPassword, tvHeader;
    private EditText etEmail,etPassword;
    private ImageView ivLeft, ivRight;

    private String email = "", phoneNumber = "";
    private Context context = LoginActivity.this;
    //facebook integration variables
    private CallbackManager callbackManager;
    private String emailID = "", socialID = "", socialType = "", image = "";
    private PrefsManager prefsManager;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());

        prefsManager = new PrefsManager(context);

        findIds();
        setListeners();
        setFont();
    }

    @Override
    protected void onResume() {
        super.onResume();
        etEmail.setText("");
        etPassword.setText("");
        etEmail.requestFocus();
    }

    private void setFont() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvLoginWithFacebook.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorPassword.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etPassword.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvLogin.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvForgotPassword.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, WelcomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    private void findIds() {
        tvHeader=(TextView)findViewById(R.id.tvHeader);
        tvHeader.setText("Sign In");

        tvErrorEmail=(TextView)findViewById(R.id.tvErrorEmail);
        tvErrorPassword=(TextView)findViewById(R.id.tvErrorPassword);
        tvLoginWithFacebook=(TextView)findViewById(R.id.tvLoginWithFacebook);
        tvLogin=(TextView)findViewById(R.id.tvLogin);
        tvForgotPassword=(TextView)findViewById(R.id.tvForgotPassword);

        etEmail=(EditText)findViewById(R.id.etUserName);
        etPassword=(EditText)findViewById(R.id.etPassword);
        tvLoginWithFacebook=(TextView)findViewById(R.id.tvLoginWithFacebook);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivLeft.setVisibility(View.VISIBLE);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        ivRight.setVisibility(View.INVISIBLE);

    }

    private void setListeners() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()) {
                    userLoginService();
                }
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ForgotPasswordActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvLoginWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socialType = "facebook";
                loginWithFacebook();
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0&&Character.isDigit(s.charAt(0))){
                    etEmail.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                    etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                    tvErrorEmail.setVisibility(View.GONE);
                } else {
                    etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    etEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50)});
                }
                etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvErrorEmail.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPassword.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvErrorPassword.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private boolean checkValidation() {
        boolean result=false;

        if (etEmail.getText().toString().trim().equalsIgnoreCase("")) {
            tvErrorEmail.setText(getString(R.string.enter_email_id));
            tvErrorEmail.setVisibility(View.VISIBLE);
            etPassword.setBackgroundResource(R.drawable.rounded_bg_edittext);
            tvErrorPassword.setVisibility(View.GONE);
            etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
            etEmail.requestFocus();
            result = false;
        }
        else
        if (Character.isLetter(etEmail.getText().toString().trim().charAt(0))) {
            if (!CommonUtils.isValidEmail(etEmail.getText().toString().trim())) {

                tvErrorEmail.setText(getString(R.string.enter_valid_email_id));
                tvErrorEmail.setVisibility(View.VISIBLE);
                etPassword.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvErrorPassword.setVisibility(View.GONE);
                etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
                etEmail.requestFocus();
                result = false;
            }
            else if (etPassword.getText().toString().trim().equalsIgnoreCase("")) {
                tvErrorEmail.setVisibility(View.GONE);
                etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvErrorPassword.setVisibility(View.VISIBLE);
                etPassword.setBackgroundResource(R.drawable.rectangle_rounded_error);
                etPassword.requestFocus();
                result = false;
            }
            else if (etPassword.getText().length() < 4) {
                tvErrorPassword.setText(getString(R.string.enter_valid_password));
                etPassword.setBackgroundResource(R.drawable.rectangle_rounded_error);
                tvErrorPassword.setVisibility(View.VISIBLE);
                tvErrorEmail.setVisibility(View.GONE);
                etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                etPassword.requestFocus();
                result = false;


            }
            else {
                phoneNumber = "";
                email = etEmail.getText().toString().trim();
                result = true;
            }
        }
        else if (Character.isDigit(etEmail.getText().toString().trim().charAt(0)))
        {
            if (!(etEmail.getText().toString().length() == 10)) {
                tvErrorEmail.setText(getString(R.string.enter_valid_number));
                tvErrorEmail.setVisibility(View.VISIBLE);
                etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
                etPassword.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvErrorPassword.setVisibility(View.GONE);
                etEmail.requestFocus();
                result = false;
            } else if (etPassword.getText().toString().trim().equalsIgnoreCase("")) {
                tvErrorPassword.setText(getString(R.string.enter_password));
                tvErrorEmail.setVisibility(View.GONE);
                etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                tvErrorPassword.setVisibility(View.VISIBLE);
                etPassword.setBackgroundResource(R.drawable.rectangle_rounded_error);
                etPassword.requestFocus();
                result = false;
            } else if (etPassword.getText().length() < 4) {
                tvErrorPassword.setText(getString(R.string.enter_valid_password));
                etPassword.setBackgroundResource(R.drawable.rectangle_rounded_error);
                tvErrorPassword.setVisibility(View.VISIBLE);
                tvErrorEmail.setVisibility(View.GONE);
                etEmail.setBackgroundResource(R.drawable.rounded_bg_edittext);
                etPassword.requestFocus();

                result = false;
            }
            else {
                email = "";
                phoneNumber = etEmail.getText().toString().trim();
                result = true;
            }
        }
        else {
            result = true;
        }
        return result;
    }

    /**
     * login with facebook
     */
    private void loginWithFacebook(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        LogUtils.infoLog("LoginActivity", ":::facebook response:::"+response.toString());
                        try {
                            socialID = object.getString("id");
                            try {
                                image = String.valueOf(new URL("http://graph.facebook.com/" + socialID + "/picture?type=large"));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            name = object.getString("name");
                            emailID = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        LogUtils.infoLog("LoginActivity", ":::socialID:::"+ socialID);
                        LogUtils.infoLog("LoginActivity", ":::image:::"+ image);
                        LogUtils.infoLog("LoginActivity", ":::emailID:::"+emailID);

                        UserDetail userDetail = new UserDetail();
                        userDetail.name = name;
                        userDetail.email = emailID;

                        startActivity(new Intent(context, SignUpActivity.class)
                                .putExtra("UserDetail", userDetail)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,id,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();


            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "Login cancelled!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(context, "Login error!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (socialType.equalsIgnoreCase("facebook")){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void userLoginService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.email, email);
            jsonObject.put(serviceRequest.phoneNumber, phoneNumber);
            jsonObject.put(serviceRequest.password, etPassword.getText().toString());
            jsonObject.put(serviceRequest.deviceType, CommonUtils.getPreferences(context, AppConstant.DEVICE_TYPE));
            jsonObject.put(serviceRequest.androidDeviceKey, prefsManager.getKeyDeviceKey());
            jsonObject.put(serviceRequest.deviceToken, CommonUtils.getPreferences(context, AppConstant.DEVICE_TOKEN));



            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("LoginActivity", "_____Request_____" + RequestURL.URL_USER_LOGIN);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_USER_LOGIN, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    UserDetail userDetail = new UserDetail();
                                    userDetail.name = TextUtils.isEmpty(serviceResponse.name)?"":serviceResponse.name;
                                    userDetail.email = TextUtils.isEmpty(serviceResponse.email)?"":serviceResponse.email;
                                    userDetail.phoneNumber = TextUtils.isEmpty(serviceResponse.phoneNumber)?"":serviceResponse.phoneNumber;
                                    userDetail.profileImage = TextUtils.isEmpty(serviceResponse.profileImage)?"":serviceResponse.profileImage;
                                    userDetail.userId = TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId;
                                    CommonUtils.savePreferencesString(context,AppConstant.EMAIL_KEY,serviceResponse.email);
                                    CommonUtils.savePreferencesString(context,AppConstant.MOBILE_KEY,serviceResponse.phoneNumber);
                                    prefsManager.setKeyUserDetailModel(userDetail);
                                    prefsManager.setKeyUserId(TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId);
                                    prefsManager.setLoggedIn(true);
                                    prefsManager.setKeyPlanId(TextUtils.isEmpty(serviceResponse.planId)?"":serviceResponse.planId);
                                    prefsManager.setKeyWalletBalance(TextUtils.isEmpty(serviceResponse.walletBalance)?"0.0":serviceResponse.walletBalance);

                                    prefsManager.setKeyUserType("REGISTERED");

                                    startActivity(new Intent(context, HomeActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));

                                } else if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.FAILURE_CODE_202)) {
                                    //User is not verified
                                    if (!TextUtils.isEmpty(serviceResponse.userId)) {
                                        prefsManager.setKeyUserId(serviceResponse.userId);
                                    }

                                    LogUtils.errorLog("LoginActivity", ":::::<<<<<OTP>>>>>:::::"+serviceResponse.otp);
                                    startActivity(new Intent(context, OtpActivity.class)
                                            .putExtra(AppConstant.INTENT_OPT_SECTION, "LOGIN")
                                            .putExtra("OTP", serviceResponse.otp)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    CommonUtils.showToast(context, serviceResponse.message);

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

    private void socialLoginService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.socialId, socialID);
            jsonObject.put(serviceRequest.deviceType, AppConstant.DEVICE_TYPE);
            jsonObject.put(serviceRequest.androidDeviceKey, prefsManager.getKeyDeviceKey());
            jsonObject.put(serviceRequest.deviceToken, prefsManager.getKeyDeviceToken());

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("LoginActivity", "_____Request_____" + RequestURL.URL_SOCIAL_LOGIN);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_SOCIAL_LOGIN, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    CommonUtils.showToast(context, serviceResponse.message);

                                    UserDetail userDetail = new UserDetail();
                                    userDetail.name = TextUtils.isEmpty(serviceResponse.name)?"":serviceResponse.name;
                                    userDetail.email = TextUtils.isEmpty(serviceResponse.email)?"":serviceResponse.email;
                                    userDetail.phoneNumber = TextUtils.isEmpty(serviceResponse.phoneNumber)?"":serviceResponse.phoneNumber;
                                    userDetail.profileImage = TextUtils.isEmpty(serviceResponse.profileImage)?"":serviceResponse.profileImage;
                                    userDetail.userId = TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId;

                                    prefsManager.setKeyUserDetailModel(userDetail);
                                    prefsManager.setKeyUserId(TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId);
                                    prefsManager.setLoggedIn(true);
                                    prefsManager.setKeyPlanId(TextUtils.isEmpty(serviceResponse.planId)?"":serviceResponse.planId);
                                    prefsManager.setKeyUserType("REGISTERED");

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
}
