package com.pikndel.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.pikndel.listeners.OnTransactionListner;
import com.pikndel.model.TransactionDetails;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by ashimkanshal on 25/8/16.
 */
public class Utils {

    public String URL_PAYTM_VERIFY = "https://pguat.paytm.com/oltp/HANDLER_INTERNAL/TXNSTATUS?JsonData={MID:%1$s,ORDERID:%2$s}";
    private Context context;
    private String price;
    private OnTransactionListner listner;



    public Utils(Context context, String price, OnTransactionListner listner){
        this.context = context;
        this.price = price;
        this.listner = listner;
    }


    public void startTransaction() {

        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<>();

        // these are mandatory parameters
        paramMap.put("ORDER_ID",initOrderId());
        paramMap.put("MID", AppConstant.MID);
        paramMap.put("CUST_ID", new PrefsManager(context).getKeyUserId());
        paramMap.put("CHANNEL_ID", AppConstant.CHANNEL_ID);
        paramMap.put("INDUSTRY_TYPE_ID", AppConstant.INDUSTRY_TYPE_ID);
        paramMap.put("WEBSITE", AppConstant.WEBSITE);
        paramMap.put("TXN_AMOUNT", price);
        if(new PrefsManager(context).getKeyUserId().equals("-1")){

            paramMap.put("MOBILE_NO",CommonUtils.getPreferences(context,AppConstant.MOBILE_KEY));
            paramMap.put("EMAIL", AppConstant.EMAIL_ID);

        }else {

            paramMap.put("MOBILE_NO", CommonUtils.getPreferences(context,AppConstant.MOBILE_KEY));
            paramMap.put("EMAIL", CommonUtils.getPreferences(context,AppConstant.EMAIL_KEY));

        }
//        paramMap.put("THEME", "");
        paramMap.put("REQUEST_TYPE", AppConstant.REQUEST_TYPE);
      /*  paramMap.put("MOBILE_NO","9650155182");
        paramMap.put("EMAIL", AppConstant.EMAIL_ID);*/
//        paramMap.put("CALLBACK_URL","http://54.83.7.62:8080/pickndel/validateCheckSum");
        paramMap.put("CALLBACK_URL","http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/validateCheckSum");

        PaytmOrder Order = new PaytmOrder(paramMap);

//        PaytmMerchant merchant = new PaytmMerchant("http://54.83.7.62:8080/pickndel/generateCheckSum", "http://54.83.7.62:8080/pickndel/validateCheckSum");
        PaytmMerchant merchant = new PaytmMerchant("http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/generateCheckSum",
                "http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/validateCheckSum");

        Service.initialize(Order, merchant, null);

        Service.startPaymentTransaction(context, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        listner.onTransactionfailure(inErrorMessage);
                        Log.e("someUIErrorOccurred", ""+inErrorMessage);
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initializati delhion of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        Log.e("onTransactionSuccess", "successful");
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        CommonUtils.showToast(context, "Payment Transaction Successful.");
                        TransactionDetails transactionDetails = new TransactionDetails();
                        transactionDetails.BANKNAME = String.valueOf(inResponse.get("BANKNAME"));
                        transactionDetails.BANKTXNID = String.valueOf(inResponse.get("BANKTXNID"));
                        transactionDetails.CURRENCY = String.valueOf(inResponse.get("CURRENCY"));
                        transactionDetails.GATEWAYNAME = String.valueOf(inResponse.get("GATEWAYNAME"));
                        transactionDetails.IS_CHECKSUM_VALID = String.valueOf(inResponse.get("IS_CHECKSUM_VALID"));
                        transactionDetails.MID = String.valueOf(inResponse.get("TXNAMOUNT"));
                        transactionDetails.ORDER_ID = String.valueOf(inResponse.get("ORDERID"));
                        transactionDetails.PAYMENTMODE = String.valueOf(inResponse.get("PAYMENTMODE"));
                        transactionDetails.RESPCODE = String.valueOf(inResponse.get("RESPCODE"));
                        transactionDetails.RESPMSG = String.valueOf(inResponse.get("RESPMSG"));
                        transactionDetails.STATUS = String.valueOf(inResponse.get("STATUS"));
                        transactionDetails.TXN_AMOUNT = String.valueOf(inResponse.get("TXNAMOUNT"));
                        transactionDetails.TXNDATE = String.valueOf(inResponse.get("TXNDATE"));
                        transactionDetails.TXNID = String.valueOf(inResponse.get("TXNID"));
                        verifyTransaction(transactionDetails);
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {

                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        listner.onTransactionfailure("Transaction Error. Please try again later.");
                        Log.d("onTransactionFailure", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                        Log.e("network", "failur");
                        listner.onTransactionfailure("Network Failure");
                        Toast.makeText(context, "Network unavailable", Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {


                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                });
    }

    public void verifyTransaction(final TransactionDetails transactionDetails) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                new ServiceAsync(context, true, jsonObject.toString(), String.format(URL_PAYTM_VERIFY, AppConstant.MID, transactionDetails.ORDER_ID), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (parseFloat(serviceResponse.getTXNAMOUNT())==parseFloat(transactionDetails.TXN_AMOUNT)) {
                                    listner.onTransactionSuccess(transactionDetails);
                                }else {
                                    listner.onTransactionfailure("Transaction Error. Please try again later.");
                                    Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                listner.onTransactionfailure("Transaction Error. Please try again later.");
                                Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Object o) {
                        Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                        listner.onTransactionfailure("Transaction Error. Please try again later.");

                    }
                }).execute();
            } else {
                Toast.makeText(context, "Network unavailable", Toast.LENGTH_LONG).show();
                listner.onTransactionfailure("Transaction Error. Please try again later.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Float parseFloat(String value){
        float floatValue = 0.0f;
        if (TextUtils.isEmpty(value)) {
            return floatValue;
        }
        try {
            floatValue = Float.parseFloat(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return floatValue;
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    public static String initOrderId() {

        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        return orderId;
    }

}
