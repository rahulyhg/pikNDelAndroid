package com.pikndel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pikndel.activity.HomeActivity;
import com.pikndel.activity.LoginActivity;
import com.pikndel.activity.TutorialActivity;
import com.pikndel.activity.WelcomeActivity;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {

    private String TAG = SplashActivity.class.getName();
    private GoogleCloudMessaging gcm;
    private String regId;
    private boolean loggingStatus;
    private PrefsManager prefManager;
    private GifImageView gifFile;
    private Context context = SplashActivity.this;
    public static final int SPLASH_TIME_OUT = 6000;
    private List<String> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

//        CommonUtils.printKeyHash(context);
        prefManager = new PrefsManager(context);
        gifFile = (GifImageView) findViewById(R.id.gifFile);
        CommonUtils.savePreferencesString(context, AppConstant.DEVICE_TYPE, "android");
        showStatus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (TextUtils.isEmpty(prefManager.getKeyDeviceKey()) && TextUtils.getTrimmedLength(prefManager.getKeyDeviceKey()) <= 0) {
                    String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    prefManager.setKeyDeviceKey(android_id);
                    LogUtils.infoLog("RegistrationActivity", "_____device key_____" + prefManager.getKeyDeviceKey().trim());
                }


                if (CommonUtils.isNetworkAvailable(context)) {

                    loggingStatus = prefManager.isLoggedIn();
                    LogUtils.infoLog("RegistrationActivity", "_____Already have registration id_____" + prefManager.getKeyDeviceToken().trim());
                    LogUtils.infoLog("RegistrationActivity", "_____Is loggingStatus_____" + loggingStatus);


                    if (!TextUtils.isEmpty(CommonUtils.getPreferences(context, AppConstant.DEVICE_TOKEN))) {

                        if (loggingStatus) {
                            startActivity(new Intent(context, HomeActivity.class)
                                    .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {

                            getSplashScreenImagesService();
                        }
/*
                            startActivity(new Intent(context, HomeActivity.class)
                                    .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
                /*        if(CommonUtils.isNetworkAvailable(context)){

                            getSplashScreenImagesService();
                        }else {

                            Toast.makeText(SplashActivity.this, "Please check your Connection", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, TutorialActivity.class)
                                    .putExtra("IMAGE_LIST", (Serializable) imagesList)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }*/



                    } else {
                        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.

                        LogUtils.infoLog("RegistrationActivity", "Device Token not found");
                    }
                }
            }
        }, SPLASH_TIME_OUT);


    }

    public void showStatus() {
        if (CommonUtils.isOnline(context)) {
            gifFile.setVisibility(View.VISIBLE);
        } else {
            gifFile.setVisibility(View.INVISIBLE);
            final Dialog dialog = DialogUtils.createCustomDialog(context, R.layout.call_dialog);
            dialog.setCanceledOnTouchOutside(false);

            ImageView ivCall = (ImageView) dialog.findViewById(R.id.ivCall);
            ImageView ivSetting = (ImageView) dialog.findViewById(R.id.ivSetting);

            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    CommonUtils.setUpCall(context);
                }
            });

            ivSetting.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                    finish();
                }
            });

        }
    }

/*    private  void checkAndGetDeviceToken() {
        if (TextUtils.isEmpty(prefManager.getKeyDeviceKey()) && TextUtils.getTrimmedLength(prefManager.getKeyDeviceKey()) <= 0) {
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            prefManager.setKeyDeviceKey(android_id);
            LogUtils.infoLog("RegistrationActivity", "_____device key_____" + prefManager.getKeyDeviceKey().trim());
        }

        if (!TextUtils.isEmpty(prefManager.getKeyDeviceToken()) && TextUtils.getTrimmedLength(prefManager.getKeyDeviceToken()) > 0) {
            loggingStatus = prefManager.isLoggedIn();
            LogUtils.infoLog("RegistrationActivity", "_____Already have registration id_____" + prefManager.getKeyDeviceToken().trim());
            LogUtils.infoLog("RegistrationActivity", "_____Is loggingStatus_____" + loggingStatus);
            //CALLING ACTIVITY BASED ON RESULT
            if (loggingStatus) {
                startActivity(new Intent(context,HomeActivity.class)
                        .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }else {
                getSplashScreenImagesService();
            }
        } else {
            // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                if (CommonUtils.isOnline(context)) {
                    registerInBackground();
                } else {
                    CommonUtils.showToast(context, getString(R.string.pls_check_your_internet_connection));
                    finish();
                }

            } else {
                LogUtils.infoLog("RegistrationActivity", "No valid Google Play Services APK found.");
            }
        }
    }*/
/*
    private boolean checkPlayServices() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
                } else {
                    LogUtils.infoLog("RegistrationActivity", "This device is not supported.");
                    finish();
                }
                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return true;
    }*/

/*    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                //CommonMethods.printLog(context, "Do in background");
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(AppConstant.SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    prefManager.setKeyDeviceToken(regId);
                    LogUtils.infoLog("RegistrationActivity", "_____The registration id is now_____" + regId);
                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

                if (!TextUtils.isEmpty(prefManager.getKeyDeviceToken()) && TextUtils.getTrimmedLength(prefManager.getKeyDeviceToken()) > 0) {
                    //loggingStatus = prefManager.getKeyIsLoggedIn();
                    LogUtils.infoLog("RegistrationActivity", "_____Already have registration id_____" + prefManager.getKeyDeviceToken().trim());
                    LogUtils.infoLog("RegistrationActivity", "_____Is loggingStatus_____" + loggingStatus);
                    if (loggingStatus) {
                        startActivity(new Intent(context,HomeActivity.class)
                                .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }else {
                        getSplashScreenImagesService();
                    }

                }else {
                    CommonUtils.showToast(context, getString(R.string.unable_to_fetch_device_token));
                    finish();
                }
            }
        }.execute(null, null, null);
    }*/


    @Subscribe
    public void onEventMainThread(String Token) {
        Log.e(TAG, ":::::::: colorType:::" + Token);

        if (TextUtils.isEmpty(prefManager.getKeyDeviceKey()) && TextUtils.getTrimmedLength(prefManager.getKeyDeviceKey()) <= 0) {
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            prefManager.setKeyDeviceKey(android_id);
            LogUtils.infoLog("RegistrationActivity", "_____device key_____" + prefManager.getKeyDeviceKey().trim());
        }


        if (CommonUtils.isNetworkAvailable(context)) {

            loggingStatus = prefManager.isLoggedIn();
            LogUtils.infoLog("RegistrationActivity", "_____Already have registration id_____" + prefManager.getKeyDeviceToken().trim());
            LogUtils.infoLog("RegistrationActivity", "_____Is loggingStatus_____" + loggingStatus);


            if (!TextUtils.isEmpty(CommonUtils.getPreferences(context, AppConstant.DEVICE_TOKEN))) {
                if (loggingStatus) {
                    startActivity(new Intent(context, HomeActivity.class)
                            .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {

                    getSplashScreenImagesService();
                }
            } else {
                // Check device for Play Services APK. If check succeeds, proceed with GCM registration.

                LogUtils.infoLog("RegistrationActivity", "Device Token not found");
            }
        }
    }


    private void getSplashScreenImagesService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("TutorialActivity", "_____Request_____" + RequestURL.URL_GET_SPLASH_SCREEN_IMAGES);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_SPLASH_SCREEN_IMAGES, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    imagesList.clear();
                                    if (serviceResponse.splashScreenImages != null && serviceResponse.splashScreenImages.size() > 0) {
                                        imagesList.addAll(serviceResponse.splashScreenImages);
                                        startActivity(new Intent(context, TutorialActivity.class)
                                                .putExtra("IMAGE_LIST", (Serializable) imagesList)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    } else {
                                        startActivity(new Intent(context, WelcomeActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    }

                                } else {
                                    startActivity(new Intent(context, WelcomeActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
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
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
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

