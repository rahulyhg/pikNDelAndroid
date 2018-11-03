package com.pikndel.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.adapter.PaymentHistoryAdapter;
import com.pikndel.services.PaymentHistoryList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView rvPaymentHistory;
    private TextView tvHeader, tvBalance, tvbal;
    private ImageView ivLeft,ivRight;

    private PaymentHistoryAdapter mAdapter;
    private PrefsManager prefsManager;
    private Context context= PaymentHistoryActivity.this;
    private List<PaymentHistoryList> paymentHistoryList = new ArrayList<>();
    public static final String WALLET_BALANCE = "WALLET_BALANCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        prefsManager = new PrefsManager(context);

        findIds();
        setListners();
        setTextAttributes();

        mAdapter = new PaymentHistoryAdapter(context,paymentHistoryList);
        rvPaymentHistory.setAdapter(mAdapter);
        getPaymentHistoryService();
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvBalance.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvbal.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvHeader.setText("Payment History");
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
        tvbal.setText("₹ "+prefsManager.getKeyWalletBalance());
    }

    private void setListners() {
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void findIds() {
        tvHeader = (TextView)findViewById(R.id.tvHeader);
        tvBalance = (TextView)findViewById(R.id.tvBalance);
        tvbal = (TextView)findViewById(R.id.tvbal);
        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);
        rvPaymentHistory = (RecyclerView)findViewById(R.id.rvPaymentHistory);
        rvPaymentHistory.setLayoutManager(new LinearLayoutManager(context));
    }

    private void getPaymentHistoryService() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("PaymentHistoryActivity", "_____Request_____" + String.format(RequestURL.URL_GET_PAYMENT_HISTORY, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_GET_PAYMENT_HISTORY,  prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {
                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    paymentHistoryList.clear();
                                    int balance = 0;
                                    if (serviceResponse.paymentHistoryList != null && serviceResponse.paymentHistoryList.size()>0){
                                        paymentHistoryList.addAll(serviceResponse.paymentHistoryList);

                                        for (int i = 0; i < paymentHistoryList.size(); i++) {
                                            balance = balance+paymentHistoryList.get(i).amount;
                                        }
//                                        tvbal.setText("₹ "+balance);
                                        mAdapter.notifyDataSetChanged();
                                    }else {
                                        tvbal.setText("₹ 0");
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
}
