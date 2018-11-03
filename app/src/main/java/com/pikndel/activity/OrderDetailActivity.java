package com.pikndel.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.pikndel.R;
import com.pikndel.Retrofit.ApiInterface;
import com.pikndel.Retrofit.RetrofitModal;
import com.pikndel.modal.BaseResponse;
import com.pikndel.model.OrderList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.TimeStampFormatter;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvPickupType, tvPickUpFrom,tvDeliverTo,tvCompleted,tvHeader,tvCancle,tvReSend,tvTripNo,tvDateTime,tvTotalPrice,tvPickName,tvPickAddress,tvDeliverName,tvDeliverAddress,tvDocumentUpto,tvUpToKg,tvGetInvoice;
    private ImageView ivLeft, ivRight ;
    private LinearLayout llEditCancel;
    private String[] s,s1;

    private Context context=OrderDetailActivity.this;
    private OrderList orderList= new OrderList();
    private String section = "";
    private PrefsManager prefsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        prefsManager = new PrefsManager(context);
        if (getIntent() != null){
            if (getIntent().getSerializableExtra("orderList") != null)
                orderList = (OrderList) getIntent().getSerializableExtra("orderList");
            if (!TextUtils.isEmpty(getIntent().getStringExtra("SECTION")))
                section = getIntent().getStringExtra("SECTION");

        }

        findIds();
        setListeners();
        setTextAttributes();
    }

    private void setTextAttributes() {
        tvTripNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
        tvCompleted.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvDateTime.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvTotalPrice.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
        tvPickUpFrom.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvDeliverTo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvPickName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
        tvDeliverName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
        tvPickAddress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliverAddress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPickupType.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvDocumentUpto.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
        tvUpToKg.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvReSend.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCancle.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvGetInvoice.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        if(!section.equalsIgnoreCase("completed")){
            tvCompleted.setText("Pending");
        } else{
            tvCompleted.setText("Completed");
            if (orderList.billingType.equalsIgnoreCase("PER_ORDER")) {
                tvGetInvoice.setVisibility(View.VISIBLE);
            }else {
                tvGetInvoice.setVisibility(View.GONE);
            }
            llEditCancel.setVisibility(View.GONE);
        }

        tvHeader.setText("Details");
        ivLeft.setVisibility(View.INVISIBLE);
        ivRight.setImageResource(R.mipmap.cross);

        if (orderList != null){

            if (!orderList.multipleLocation) {
                if (orderList.packageWeight > 1) {
                    tvUpToKg.setText(orderList.packageWeight + " Kgs");
                } else {
                    tvUpToKg.setText(orderList.packageWeight + " Kg");
                }
                tvDocumentUpto.setText(TextUtils.isEmpty(orderList.productTypeName) ? "No Data Available" : orderList.productTypeName);
            }

            if (orderList.pickupAddress != null) {


                if (!TextUtils.isEmpty(orderList.pickUpLocation) && !TextUtils.isEmpty(orderList.deliveryLocation) ){

                    s1 = orderList.pickUpLocation.split(",");

                    tvPickName.setText(TextFonts.setValidText(orderList.pickUpLocation, s1[0]));

                }
                else {


                    tvPickName.setText(TextFonts.setValidText(orderList.fromCity, orderList.fromCity));
                }

                tvPickAddress.setText(new StringBuilder()
                        .append(TextUtils.isEmpty(orderList.pickupAddress.houseNumber) ? "" :"H.No.-" + orderList.pickupAddress.houseNumber + ", ")
                        .append(TextUtils.isEmpty(orderList.pickupAddress.floor) ? "" :"floor-" + orderList.pickupAddress.floor + ", ")
                        .append(TextUtils.isEmpty(orderList.pickupAddress.area) ? "" : orderList.pickupAddress.area + ", ")
                        .append(TextUtils.isEmpty(orderList.pickupAddress.cityName) ? "" : orderList.pickupAddress.cityName + ", ")
                        .append(TextUtils.isEmpty(orderList.pickupAddress.pincode) ? "" : orderList.pickupAddress.pincode)
                        .append(TextUtils.isEmpty(orderList.pickupAddress.landmark) ? "" : " (near " + orderList.pickupAddress.landmark + ")"));

            }
            if (orderList.deliveryAddress != null) {

                if (!TextUtils.isEmpty(orderList.pickUpLocation) && !TextUtils.isEmpty(orderList.deliveryLocation) ){

                    s = orderList.deliveryLocation.split(",");
                    tvDeliverName.setText(TextFonts.setValidText(orderList.deliveryLocation, s[0]));
                }
                else {

                    tvDeliverName.setText(TextFonts.setValidText(orderList.toCity, orderList.toCity));
                }

                tvDeliverAddress.setText(new StringBuilder()
                        .append(TextUtils.isEmpty(orderList.deliveryAddress.houseNumber) ? "" :  "H.No.-" + orderList.deliveryAddress.houseNumber + ", ")
                        .append(TextUtils.isEmpty(orderList.deliveryAddress.floor) ? "" : "floor-" + orderList.deliveryAddress.floor + ", ")
                        .append(TextUtils.isEmpty(orderList.deliveryAddress.area) ? "" : orderList.deliveryAddress.area + ", ")
                        .append(TextUtils.isEmpty(orderList.deliveryAddress.cityName) ? "" : orderList.deliveryAddress.cityName + ", ")
                        .append(TextUtils.isEmpty(orderList.deliveryAddress.pincode) ? "" : orderList.deliveryAddress.pincode)
                        .append(TextUtils.isEmpty(orderList.deliveryAddress.landmark) ? "" : " (near " + orderList.deliveryAddress.landmark + ")"));
            }

            tvTripNo.setText(TextUtils.isEmpty(orderList.referenceNumber)?"Ref : xxx":"Reference Id : "+orderList.referenceNumber);
            tvTotalPrice.setText("₹ " + String.format(Locale.US, "%.2f", orderList.finalAmount));
            tvDateTime.setText(new StringBuilder()
                    .append(TextUtils.isEmpty(orderList.deliveryByDate) ? "" : TimeStampFormatter.getValueFromTS(orderList.deliveryByDate, "MMM dd yyyy"))
                    .append(" ")
                    .append(TextUtils.isEmpty(orderList.deliveryByTime) ? "" : TimeStampFormatter.changeDateTimeFormat(orderList.deliveryByTime, "HH:mm:ss", "HH:mm"))
                    .append(" ")
                    .append("hrs"));

        }
    }

    private void setListeners() {
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvGetInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInvoiceService();
            }
        });

        tvReSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrder();
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderCancel();
            }
        });

    }

    private void findIds() {
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);

        tvTripNo = (TextView)findViewById(R.id.tvTripNo);
        tvDateTime = (TextView)findViewById(R.id.tvDateTime);
        tvTotalPrice = (TextView)findViewById(R.id.tvTotalPrice);
        tvCancle = (TextView)findViewById(R.id.tvCancle);
        llEditCancel = (LinearLayout) findViewById(R.id.llEditCancel);
        tvReSend = (TextView)findViewById(R.id.tvReSend);
        tvCompleted = (TextView)findViewById(R.id.tvCompleted);
        tvPickName = (TextView)findViewById(R.id.tvPickName);
        tvPickAddress = (TextView)findViewById(R.id.tvPickAddress);
        tvDeliverName = (TextView)findViewById(R.id.tvDeliverName);
        tvDeliverAddress = (TextView)findViewById(R.id.tvDeliverAddress);
        tvDocumentUpto = (TextView)findViewById(R.id.tvDocumentUpto);
        tvUpToKg = (TextView)findViewById(R.id.tvUpToKg);
        tvGetInvoice = (TextView)findViewById(R.id.tvGetInvoice);
        tvPickUpFrom=(TextView)findViewById(R.id.tvPickUpFrom);
        tvDeliverTo=(TextView)findViewById(R.id.tvDeliverTo);
        tvPickupType=(TextView)findViewById(R.id.tvPickupType);

    }

    public void getInvoice(String message)  {
        final Dialog mDialog = DialogUtils.createCustomDialog(context, R.layout.dialog_register_email_id);
        TextView tvThanku = (TextView) mDialog.findViewById(R.id.tvThanku);
        ImageView ivCall = (ImageView) mDialog.findViewById(R.id.ivCall);

        TextView tvGetBack = (TextView) mDialog.findViewById(R.id.tvGetBack);

        tvGetBack.setText(message);
        tvThanku.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvGetBack.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        ivCall.setVisibility(View.GONE);
    }

    public void editOrderCancel()  {
        final Dialog mDialog = DialogUtils.createCustomDialog(context, R.layout.dialog_call_to_edit_order);

        TextView tvThanku = (TextView) mDialog.findViewById(R.id.tvThanku);
        ImageView ivCall = (ImageView) mDialog.findViewById(R.id.ivCall);
        TextView tvGetBack = (TextView) mDialog.findViewById(R.id.tvGetBack);

        tvThanku.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvGetBack.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvGetBack.setText("The order cannot be cancelled online. Please call us to cancel the order.");

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.setUpCall(context);

            }
        });
    }

    public void editOrder()  {
        final Dialog mDialog = DialogUtils.createCustomDialog(context, R.layout.dialog_call_to_edit_order);

        TextView tvThanku = (TextView) mDialog.findViewById(R.id.tvThanku);
        ImageView ivCall = (ImageView) mDialog.findViewById(R.id.ivCall);
        TextView tvGetBack = (TextView) mDialog.findViewById(R.id.tvGetBack);

        tvThanku.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvGetBack.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvGetBack.setText("The required change cannot be made online. Please call us to make the required change.");

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.setUpCall(context);

            }
        });
    }


    private void getInvoiceService() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("WaysToPikndelActivity", "_____Request_____" + String.format(RequestURL.URL_GENERATE_INVOICE, orderList.referenceNumber, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_GENERATE_INVOICE, orderList.referenceNumber, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    getInvoice(serviceResponse.message);

                                } else {
                                    CommonUtils.showToast(context, serviceResponse.message);
                                }
                            } else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Object o) {
                        ServiceResponse serviceResponse = (ServiceResponse) o;
                        if (serviceResponse != null) {
                            CommonUtils.showToast(context, serviceResponse.message);
                        }

                    }
                }).execute();
            } else {
                CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
