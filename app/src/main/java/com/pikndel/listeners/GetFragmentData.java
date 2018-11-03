package com.pikndel.listeners;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public interface GetFragmentData {

    void onGetFragmentData(String data, boolean value);
    void onGetFragmentData(String data,String refData, boolean value);
}
