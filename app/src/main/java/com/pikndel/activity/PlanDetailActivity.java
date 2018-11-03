package com.pikndel.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.pikndel.R;
import com.pikndel.Retrofit.ApiInterface;
import com.pikndel.Retrofit.RetrofitModal;
import com.pikndel.listeners.OnTransactionListner;
import com.pikndel.modal.BaseResponse;
import com.pikndel.model.TransactionDetails;
import com.pikndel.model.UserPlanList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.Utils;

import org.json.JSONObject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlanDetailActivity extends AppCompatActivity{

    private TextView tvPlanDetail,tvNext,tvHeader;
    private ImageView ivLeft,ivRight;

    private Context context=PlanDetailActivity.this;
    private UserPlanList userPlan = new UserPlanList();
    private PrefsManager prefsManager;
    private float walletAmount = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        prefsManager = new PrefsManager(context);
        if (getIntent() != null){
            userPlan = (UserPlanList) getIntent().getSerializableExtra(AppConstant.INTENT_PLAN_DETAILS);
        }

        try {
            walletAmount = Float.parseFloat(prefsManager.getKeyWalletBalance());
        }catch (NumberFormatException e){
            walletAmount = 0.0f;
        }
        findIds();
        setListeners();
        setTextAttributes();
    }

    private void findIds() {
        tvPlanDetail =(TextView) findViewById(R.id.tvPlanDetail);
        tvNext=(TextView)findViewById(R.id.tvNext);
        tvHeader=(TextView)findViewById(R.id.tvHeader);
        ivLeft=(ImageView)findViewById(R.id.ivLeft);
        ivRight=(ImageView)findViewById(R.id.ivRight);

    }

    private void setTextAttributes() {
        tvHeader.setText(TextUtils.isEmpty(userPlan.planName)?"":userPlan.planName);
        tvPlanDetail.setText(TextUtils.isEmpty(userPlan.planDesc)?"":userPlan.planDesc);

        if (userPlan.amount<=0){
            tvNext.setText("Done");
        }else {
            tvNext.setText("Next");
        }

        ivRight.setVisibility(View.INVISIBLE);
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.isOnline(context)) {
                    CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
                }else {
                    if (userPlan.amount <=0.0) {
                        saveUserPlanService();
                    }else {
                        new Utils(context, "" + userPlan.amount, new OnTransactionListner() {
                            @Override
                            public void onTransactionSuccess(TransactionDetails transactionDetails) {
                                saveUserPlanService();
                            }

                            @Override
                            public void onTransactionfailure(String failureString) {

                            }
                        }).startTransaction();
                    }
                }
            }
        });
    }

    private void updateWalletBalanceService(String balance) {
        ApiInterface api = RetrofitModal.getInstance().getRetrofitmodal().create(ApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", prefsManager.getKeyUserId());
        jsonObject.addProperty("amount", balance);
        jsonObject.addProperty("sign", "+");
        final ProgressDialog progressDialog = ProgressDialog.show(context, "", context.getString(R.string.pls_wait));

        Observable<BaseResponse> call = api.someEndpoint(jsonObject);
        Subscription subscription = call.subscribeOn(Schedulers.newThread()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        if (e instanceof HttpException) {
                            HttpException response = (HttpException)e;
                            int code = response.code();
                        }
                    }
                    @Override
                    public void onNext(BaseResponse user) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        Log.e("response", ""+user.getMessage());
                        saveUserPlanService();

//                        et_addmoney.setText("");
//                        walletBalanceService();

                    }

                });
    }

    private void saveUserPlanService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("PlanDetailActivity", "_____Request_____" + String.format(RequestURL.URL_SAVE_USER_PLAN, prefsManager.getKeyUserId(), userPlan.planId));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_SAVE_USER_PLAN,  prefsManager.getKeyUserId(), userPlan.planId), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    prefsManager.setKeyUserId(TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId);
                                    prefsManager.setLoggedIn(true);
                                    prefsManager.setKeyPlanId(userPlan.planId);
                                    prefsManager.setKeyWalletBalance(TextUtils.isEmpty(serviceResponse.walletBalance)?"0.0":serviceResponse.walletBalance);
                                    prefsManager.setKeyUserType("REGISTERED");
                                    
                                    startActivity(new Intent(context, HomeActivity.class)
                                            .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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

