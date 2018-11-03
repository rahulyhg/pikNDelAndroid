package com.pikndel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    private TextView txtSignupSecond, tvSkipFour, tvHeader;
    private ImageView ivLeft, ivRight;
    private RecyclerView rvPlans;
    private PlansAdapter plansAdapter;
    private Context context=PlanActivity.this;
    private List<UserPlanList> userPlanList = new ArrayList<>();
    private PrefsManager prefsManager;
    private String planId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        prefsManager = new PrefsManager(context);
        findIds();
        setListeners();
        setTextAttributes();
        settingAdapter();
        getPlanListService();
    }

    private void settingAdapter(){
        userPlanList.clear();
        plansAdapter = new PlansAdapter(context, userPlanList);
        rvPlans.setAdapter(plansAdapter);
    }

    private void findIds() {
        txtSignupSecond=(TextView) findViewById(R.id.txtSignupSecond);
        tvSkipFour = (TextView)findViewById(R.id.tvSkipFour);
        tvHeader = (TextView)findViewById(R.id.tvHeader);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);

        rvPlans = (RecyclerView)findViewById(R.id.rvPlans);
        rvPlans.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        txtSignupSecond.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvSkipFour.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        ivRight.setVisibility(View.INVISIBLE);
        tvHeader.setText("Choose Your Plan");
    }

    private void setListeners() {
        tvSkipFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserPlanService();

            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getPlanListService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("PlanActivity", "_____Request_____" + RequestURL.URL_GET_PLAN_LIST);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_PLAN_LIST, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.userPlanList != null && serviceResponse.userPlanList.size()>0){
                                        userPlanList.addAll(serviceResponse.userPlanList);
                                        plansAdapter.notifyDataSetChanged();
                                        for (int i = 0; i < userPlanList.size(); i++) {
                                            if (userPlanList.get(i).amount == 0.0) {
                                                planId = userPlanList.get(i).planId;
                                                break;
                                            }

                                        }
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

    private void saveUserPlanService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("PlanActivity", "_____Request_____" + String.format(RequestURL.URL_SAVE_USER_PLAN, prefsManager.getKeyUserId(), planId));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_SAVE_USER_PLAN,  prefsManager.getKeyUserId(), planId), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    prefsManager.setKeyUserId(TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId);
                                    prefsManager.setLoggedIn(true);
                                    prefsManager.setKeyPlanId(TextUtils.isEmpty(serviceResponse.planId)?"":serviceResponse.planId);
                                    prefsManager.setKeyWalletBalance(TextUtils.isEmpty(serviceResponse.walletBalance)?"0.0":serviceResponse.walletBalance);
                                    prefsManager.setKeyUserType("REGISTERED");

                                    startActivity(new Intent(context, HomeActivity.class)
                                            .putExtra(AppConstant.INTENT_USER_TYPE, "PLAN")
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

    private class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder>{

        private Context context;
        private List<UserPlanList> userPlanList;

        public PlansAdapter(Context context, List<UserPlanList> userPlanList) {
            this.context = context;
            this.userPlanList = userPlanList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_plan, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            UserPlanList userPlan = userPlanList.get(position);
            holder.tvPlan.setText(TextUtils.isEmpty(userPlan.planName)?"":userPlan.planName);

            holder.tvPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, PlanDetailActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra(AppConstant.INTENT_PLAN_DETAILS,
                                    userPlanList.get(holder.getAdapterPosition())));
                }
            });
        }

        @Override
        public int getItemCount() {
            return userPlanList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tvPlan;
            public ViewHolder(View itemView) {
                super(itemView);
                tvPlan = (TextView)itemView.findViewById(R.id.tvPlan);
                tvPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            }
        }
    }
}
