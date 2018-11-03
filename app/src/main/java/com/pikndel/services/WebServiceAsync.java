package com.pikndel.services;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.pikndel.R;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class WebServiceAsync extends AsyncTask {
    private String request;
    private ServiceStatus serviceStatus;
    private String url,methodName;
    private Context context;
    private  ServiceCall serviceCall;
    private ProgressDialog progressDialog;
    private boolean isProgressVisible = false;

    public WebServiceAsync(Context context, boolean isProgressVisible, String request, String url , String methodName, ServiceStatus serviceStatus) {
        this.request=request;
        this.serviceStatus= serviceStatus;
        this.url=url;
        this.methodName=methodName;
        this.context=context;
        this.isProgressVisible=isProgressVisible;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isProgressVisible) {
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.pls_wait));
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        serviceCall = new ServiceCall();
        String response= serviceCall.getServiceResponse(url,request,methodName);
       if (response != null){
           return response;
       }else {
           return null;
       }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (isProgressVisible && progressDialog != null){
            progressDialog.dismiss();
        }
        if(o!=null){
            serviceStatus.onSuccess(o);
        }else{
            serviceStatus.onFailed("ERROR");
        }
    }
}
