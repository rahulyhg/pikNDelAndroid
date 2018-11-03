package com.pikndel;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class PicNDelApplication extends MultiDexApplication {
    public static final String PIC_N_DEL_PREF = "PIC_N_DEL_PREF";

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
    }
}
