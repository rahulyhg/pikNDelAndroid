package com.pikndel.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class TextFonts {

    public static final String RALEWAY_BLACK = "fonts/RalewayBlack.ttf";
    public static final String RALEWAY_THIN = "fonts/RalewayThin.ttf";
    public static final String RALEWAY_REGULAR = "fonts/RalewayRegular.ttf";
    public static final String RALEWAY_MEDIUM = "fonts/RalewayMedium.ttf";
    public static final String RALEWAY_LIGHT = "fonts/RalewayLight.ttf";
    public static final String RALEWAY_EXTRA_LIGHT = "fonts/RalewayExtraLight.ttf";
    public static final String RALEWAY_EXTRA_BOLD = "fonts/RalewayExtraBold.ttf";
    public static final String RALEWAY_SEMI_BOLD = "fonts/RalewaySemiBold.ttf";
    public static final String RALEWAY_BOLD = "fonts/RalewayBold.ttf";

    public static Typeface setFontFamily(Context context, String path){
        return Typeface.createFromAsset(context.getAssets(), path);
    }

    public static String setValidText(Object o, String  value){
        if (o == null){
            return "";
        }else {
            return TextUtils.isEmpty(value)?"":value;
        }
    }

    public static String setValidText(String  value){
        return TextUtils.isEmpty(value)?"":value;
    }
}
