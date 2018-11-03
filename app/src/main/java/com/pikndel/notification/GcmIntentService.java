/*
package com.pikndel.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.pikndel.R;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;

public class GcmIntentService extends IntentService {
    private String TAG=GcmIntentService.class.getSimpleName();
    private NotificationManager mNotificationManager;
    String message = null, senderID = null, entityId = null, type = null;
    private int NOTIFICATION_ID = 0;
    private PrefsManager prefManager;

    public GcmIntentService() {
        super("IntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        prefManager = new PrefsManager(getApplicationContext());
        NOTIFICATION_ID = prefManager.getKeyNotification();
        NOTIFICATION_ID++;
        if (intent.getExtras() != null) {
            LogUtils.errorLog(TAG, "*****push notification message *******" + intent.getExtras().toString());

            message = intent.getStringExtra("message");
            String info = intent.getStringExtra("info");
           */
/* try {
                JsonObject jsonObject = new JsonParser().parse(info).getAsJsonObject();
                senderID = jsonObject.get("senderID").getAsString();
                entityId = jsonObject.get("entityId").getAsString();
                type = jsonObject.get("type").getAsString();
            }catch (Exception e){
                e.printStackTrace();
            }*//*


            LogUtils.errorLog(TAG, "*****push notification message >>> *******" + message);
           */
/* LogUtils.errorLog(TAG, "*****push notification info >>> *******" + info);
            LogUtils.errorLog(TAG, "*****push notification senderID >>> *******" + senderID);
            LogUtils.errorLog(TAG, "*****push notification entityId >>> *******" + entityId);
            LogUtils.errorLog(TAG, "*****push notification type >>> *******" + type);*//*


           */
/* if(message!=null){
                if (!type.equalsIgnoreCase("message")){
                    if (type.equalsIgnoreCase("like") || type.equalsIgnoreCase("info")) {
                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstant.NOTIFICATION_BROADCAST_INTENT));
                    }
                    sendNotification(message, type);
                }else {
                    if (cultureDockApplication.isAppOpen()) {
                        if (ChatActivity.userID != null && ChatActivity.userID.equalsIgnoreCase(senderID)) {
                            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstant.MESSAGE_BROADCAST_INTENT));
                        } else {
                            sendNotification(message, type);
                        }
                    }else {
                        if (ChatActivity.userID != null && ChatActivity.userID.equalsIgnoreCase(senderID)) {
                            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstant.MESSAGE_BROADCAST_INTENT));
                        }
                        sendNotification(message, type);
                    }
                }

            }*//*

        }
    }

    private void sendNotification(String message, String type) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent;
        Intent i = new Intent();

       */
/* if (type.equalsIgnoreCase("message")){
            i = new Intent(getApplicationContext(), DashBoardActivity.class);
            i.putExtra(AppConstant.INTENT_DASHBOARD, 1);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else if (type.equalsIgnoreCase("like") || type.equalsIgnoreCase("info")) {
            i = new Intent(getApplicationContext(), NotifyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            i = new Intent(getApplicationContext(), NotificationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }*//*


        contentIntent = PendingIntent.getActivity(this, 1, i, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        prefManager.setKeyNotification(NOTIFICATION_ID);
    }
}*/
