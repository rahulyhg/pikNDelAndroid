package com.pikndel.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pikndel.R;
import com.pikndel.adapter.WaysToPikndelAdapter;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.IntraCityModel;
import com.pikndel.model.RouteInfoModel;
import com.pikndel.model.WaysToPikndelInfo;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by govind_gautam on 2/5/16.
 */
public class WaysToPikndelActivity extends AppCompatActivity {

    private TextView tvHeader;
    private ImageView ivLeft, ivRight ;
    private RecyclerView waysRecyclerView;

    private WaysToPikndelAdapter adapter;
    private List<RouteInfoModel> routeInfo = new ArrayList<>();
    private Context context= WaysToPikndelActivity.this;
    private IntraCityModel intraCityModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ways_to_pikndel);

        if (getIntent() != null && getIntent().getSerializableExtra("INTER_CITY_DELIVERY") != null){
            intraCityModel = (IntraCityModel) getIntent().getSerializableExtra("INTER_CITY_DELIVERY");
        }

        findIds();
        setListeners();
        setTextAttributes();

        adapter = new WaysToPikndelAdapter(context, routeInfo, intraCityModel.deliveryType, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                weightAlertDialog(position);
            }
        });

        waysRecyclerView.setAdapter(adapter);

        if (!intraCityModel.deliveryType.equalsIgnoreCase("intercity")) {
            intraCityDeliveryCalculationService();
        }else {
            getWaysToPikndelService();
        }
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvHeader.setText("Ways To Pikndel");
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void findIds() {
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        waysRecyclerView = (RecyclerView)findViewById(R.id.waysRecyclerView);
        waysRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void getWaysToPikndelService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.startCityId,Integer.parseInt(intraCityModel.deliveryInfo.startCityId));
            jsonObject.put(serviceRequest.endCityId,Integer.parseInt(intraCityModel.deliveryInfo.endCityId));
            jsonObject.put(serviceRequest.productTypeId,Integer.parseInt(intraCityModel.deliveryInfo.productTypeId));
            jsonObject.put(serviceRequest.packageWeight,Float.parseFloat(intraCityModel.deliveryInfo.packageWeight));
            jsonObject.put(serviceRequest.codAmount,Float.parseFloat(intraCityModel.deliveryInfo.codAmount));


            jsonObject.put(serviceRequest.planId, intraCityModel.planId);

          //  Toast.makeText(WaysToPikndelActivity.this, "plan id"+ intraCityModel.planId, Toast.LENGTH_SHORT).show();

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("WaysToPikndelActivity", "_____Request_____" + RequestURL.URL_GET_WAYS_TO_PIKNDEL);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_WAYS_TO_PIKNDEL, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    routeInfo.clear();
                                    if (serviceResponse.routeInfo != null && serviceResponse.routeInfo.size()>0){
                                        routeInfo.addAll(serviceResponse.routeInfo);
                                        if (serviceResponse.deliveryCost != null) {
                                            routeInfo.get(0).deliveryCost = serviceResponse.deliveryCost;
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

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

    private void intraCityDeliveryCalculationService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();

            jsonObject.put(serviceRequest.city,intraCityModel.deliveryInfo.startCityName);
            jsonObject.put(serviceRequest.pickUpLocation,intraCityModel.deliveryInfo.pickupLocation);
            jsonObject.put(serviceRequest.deliveryLocation,intraCityModel.deliveryInfo.deliverylocation);
            jsonObject.put(serviceRequest.packageType,intraCityModel.packageType);
            jsonObject.put(serviceRequest.packageTypeId,intraCityModel.deliveryInfo.productTypeId);
            jsonObject.put(serviceRequest.planId, intraCityModel.planId);
            jsonObject.put(serviceRequest.packageWeightId, intraCityModel.deliveryInfo.packageWeightId);
            jsonObject.put(serviceRequest.packageWeight, intraCityModel.deliveryInfo.packageWeight);
            jsonObject.put(serviceRequest.packageWeightAbove, intraCityModel.packageWeightAbove);
            jsonObject.put(serviceRequest.distanceKm, intraCityModel.distanceKm);
            jsonObject.put(serviceRequest.roundTrip, intraCityModel.roundTrip);
            jsonObject.put(serviceRequest.codAmount,Float.parseFloat(intraCityModel.deliveryInfo.codAmount));

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("WaysToPikndelActivity", "_____Request_____" + RequestURL.URL_INTRA_CITY_DELIVERY_CALCULATION);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_INTRA_CITY_DELIVERY_CALCULATION, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    routeInfo.clear();
                                    if (serviceResponse.routeInfo != null && serviceResponse.routeInfo.size()>0){
                                        routeInfo.addAll(serviceResponse.routeInfo);
                                        if (serviceResponse.deliveryCost != null) {
                                            routeInfo.get(0).deliveryCost = serviceResponse.deliveryCost;
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

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

    private String deliveryCost = "";
    public void setPrice(String deliveryCost){
        this.deliveryCost = deliveryCost;
    }

    public void weightAlertDialog(final int position)  {
        final Dialog customDialog = DialogUtils.createCustomDialog(context, R.layout.dialog_volumetric_weight);

        TextView tvOk = (TextView) customDialog.findViewById(R.id.tvOk);
        TextView tvCase = (TextView) customDialog.findViewById(R.id.tvCase);
        TextView tvCalculate = (TextView) customDialog.findViewById(R.id.tvCalculate);

        tvCalculate.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvOk.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvCase.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                intraCityModel.waysToPikndelInfo = new WaysToPikndelInfo();
                intraCityModel.waysToPikndelInfo.packageTypeId = routeInfo.get(position).packageTypeId;
                intraCityModel.waysToPikndelInfo.packageTypeName = routeInfo.get(position).packageTypeName;
                intraCityModel.waysToPikndelInfo.deliveryByTime = routeInfo.get(position).deliveryByTime;
                intraCityModel.waysToPikndelInfo.deliveryByDate = routeInfo.get(position).deliveryByDate;
                intraCityModel.waysToPikndelInfo.deliveryCost = deliveryCost;
                startActivity(new Intent(context, DeliveryActivity.class)
                        .putExtra("INTER_CITY_DELIVERY", intraCityModel)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        tvCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
    }
}
