package com.pikndel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.pikndel.PicNDelApplication;
import com.pikndel.model.UserDetail;

public class PrefsManager {

    public static final String KEY_NOTIFICATION = "NOTIFICATION";
    public static final String KEY_DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String KEY_DEVICE_KEY= "DEVICE_KEY";

    public final static String KEY_USER_ID = "USER_ID";

    public final static String SKIP_KEY_USER_ID = "SKIP_USER_ID";


    public final static String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";
    public final static String KEY_USER_DETAILS = "KEY_USER_DETAILS";
    public final static String KEY_PLAN_ID = "KEY_PLAN_ID";
    public final static String KEY_USER_TYPE = "KEY_USER_TYPE";
    public final static String KEY_WALLET_BALANCE = "KEY_WALLET_BALANCE";
    public final static String KEY_IS_MULTI_LOCATION = "KEY_IS_MULTI_LOCATION";
    public final static String KEY_COUPON_ID = "KEY_COUPON_ID";

    Context context;

    SharedPreferences preferences;
    Editor editor;

    public PrefsManager(Context context) {
        super();
        this.context = context;

        this.preferences = this.context.getSharedPreferences(PicNDelApplication.PIC_N_DEL_PREF, context.MODE_PRIVATE);
        this.editor = this.preferences.edit();
    }

    public void setKeyNotification(int value){
        setIntValue(KEY_NOTIFICATION, value);
    }

    public int getKeyNotification(){
        return getIntValue(KEY_NOTIFICATION);
    }

    public String getKeyUserId() {
        return getValue(KEY_USER_ID);
    }

    public void setKeyUserId(String emailID) {
        setValue(KEY_USER_ID, emailID);
    }

    public String getKeyPlanId() {
        return getValue(KEY_PLAN_ID);
    }

    public void setKeyPlanId(String planId) {
        setValue(KEY_PLAN_ID, planId);
    }

    public String getKeyWalletBalance() {
        return getValue(KEY_WALLET_BALANCE);
    }

    public void setKeyWalletBalance(String planId) {
        setValue(KEY_WALLET_BALANCE, planId);
    }

    public String getValue(String key) {
        return this.preferences.getString(key, "");
    }

    public void setValue(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public void setKeyIsMultiLocation(String value){
        setValue(KEY_IS_MULTI_LOCATION, value);
    }

    public String getKeyIsMultiLocation(){
        return getValue(KEY_IS_MULTI_LOCATION);
    }

    public void setKeyUserType(String value){
        setValue(KEY_USER_TYPE, value);
    }

    public String getKeyUserType(){
        return getValue(KEY_USER_TYPE);
    }

    public void setKeyDeviceToken(String value){
        setValue(KEY_DEVICE_TOKEN, value);
    }

    public String getKeyDeviceToken(){
        return getValue(KEY_DEVICE_TOKEN);
    }

    public void setKeyDeviceKey(String value){
        setValue(KEY_DEVICE_KEY, value);
    }

    public String getKeyDeviceKey(){
        return getValue(KEY_DEVICE_KEY);
    }

    public boolean getBooleanValue(String key) {
        return this.preferences.getBoolean(key, false);
    }

    public void setBooleanValue(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public UserDetail getKeyUserDetailModel (){
        Gson gson = new Gson();
        String json = preferences.getString(KEY_USER_DETAILS, "");
        return gson.fromJson(json, UserDetail.class);
    }

    public void setKeyUserDetailModel (UserDetail model){
        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString(KEY_USER_DETAILS, json);
        editor.commit();
    }
    /**
     * Removes all the Fields from SharedPrefs when User Click Logout Button
     */
    public void clearPrefs() {
        //
        /*this.editor.remove(KEY_EMAIL);
        this.editor.remove(KEY_NAME);
        this.editor.remove(KEY_PHONENUMBER);
        this.editor.remove(KEY_PASSWORD);
        this.editor.remove(KEY_IS_LOGGEDIN);*/
        this.editor.clear();
        this.editor.commit();

    }
    public void removeFromPreference(String key){
        this.editor.remove(key);
        this.editor.commit();
    }

    public boolean isLoggedIn() {
        return getBooleanValue(KEY_IS_LOGGED_IN);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        setBooleanValue(KEY_IS_LOGGED_IN, isLoggedIn);
    }

    /**
     * Retrieving the value from the preference for the respective key.
     * @param key : Key for which the value is to be retrieved
     * @return return value for the respective key as string.
     */
    public int getIntValue(String key) {
        return this.preferences.getInt(key, 0);
    }

    /**
     * Saving the preference
     * @param key : Key of the preference.
     * @param value : Value of the preference.
     */
    public void setIntValue(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

}
