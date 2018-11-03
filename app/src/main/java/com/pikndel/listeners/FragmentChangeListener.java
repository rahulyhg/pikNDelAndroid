package com.pikndel.listeners;

import com.pikndel.model.UserDetail;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public interface FragmentChangeListener {
    void onFragmentChangeListener(int position, UserDetail userDetail);
    void onFragmentChangeListener(int position, String value);
}
