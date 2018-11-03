package com.pikndel.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.pikndel.activity.OtpActivity;

@SuppressWarnings("ALL")
public class IncomingSmsReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if (message != null) {
                        Log.e("OTP", message + "otp");
                        message = message.replaceAll("\\D+", "");
                        Log.e("OTP", message + "otp");
                        try {
                            if (senderNum.toUpperCase().contains("-PIKDEL")) {
                                OtpActivity Sms = OtpActivity.instance();
                                if (Sms != null) {
                                    Sms.recivedSms(message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}