package com.pikndel.notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;


import de.greenrobot.event.EventBus;

/**
 * Created by Faisal Ali on 09-09-2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    String refreshedToken;


    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.DEVICE_TOKEN, refreshedToken);
        EventBus.getDefault().post("Token");
        //  sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {


         // TODO: Implement this method to send token to your app server.
        //  sendBroadcast(new Intent("Token").putExtra("Refreshed token:",refreshedToken));

    }
}
