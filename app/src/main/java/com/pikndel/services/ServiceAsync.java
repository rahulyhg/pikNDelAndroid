package com.pikndel.services;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;
import com.pikndel.R;

/**
 * Created by abhishek.tiwari on 11/3/16.
 */
public class ServiceAsync  extends AsyncTask {
    private String request;
    private ServiceStatus serviceStatus;
    private String url,methodName;
    private Context context;
    private  ServiceCall serviceCall;
    private ProgressDialog progressDialog;
    private boolean isProgressVisible = false;

    public ServiceAsync(Context context, boolean isProgressVisible, String request, String url , String methodName, ServiceStatus serviceStatus) {
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
        try {
            if(response!=null){
                Gson gson = new Gson();
             return gson.fromJson(response, ServiceResponse.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (isProgressVisible && progressDialog != null) {
                progressDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(o!=null){
            ServiceResponse servicesResponse= (ServiceResponse)o;
            if(serviceCall.getStatusCode() == 200) {
                serviceStatus.onSuccess(servicesResponse);
            }else {
                serviceStatus.onFailed(servicesResponse);
            }
        }else{
            ServiceResponse servicesResponse = new ServiceResponse();
            servicesResponse.code="000";
            servicesResponse.status=context.getString(R.string.server_not_responding);
            serviceStatus.onFailed(servicesResponse);
        }
    }
}
