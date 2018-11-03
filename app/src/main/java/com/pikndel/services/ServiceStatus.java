package com.pikndel.services;

/**
 * Created by abhishek.tiwari on 11/3/16.
 */

public abstract class ServiceStatus {
    public abstract  void onSuccess(Object o);
    public abstract  void onFailed(Object o);
}
