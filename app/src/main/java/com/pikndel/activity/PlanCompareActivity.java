package com.pikndel.activity;

import android.content.Context;
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
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlanCompareActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private TextView tvHeader;
    private ImageView ivLeft,ivRight;
    private Context context=PlanCompareActivity.this;
    private PlanAdapter planAdapter;
    private List<UserPlanList> userPlanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_plans);

        findIds();
        setListeners();
        setTextAttributes();
        settingAdapter();
        getPlanListService();
    }

    private void settingAdapter(){
        userPlanList.clear();
        planAdapter = new PlanAdapter(context, userPlanList);
        rvPlans.setAdapter(planAdapter);
    }

    private void setTextAttributes() {
        tvHeader.setText("Plans");
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
        rvPlans=(RecyclerView) findViewById(R.id.rvPlans);
        rvPlans.setLayoutManager(new LinearLayoutManager(context));
        tvHeader=(TextView)findViewById(R.id.tvHeader);
        ivLeft=(ImageView)findViewById(R.id.ivLeft);
        ivRight=(ImageView)findViewById(R.id.ivRight);
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
                                    if (serviceResponse.userPlanList != null && serviceResponse.userPlanList.size() > 0) {
                                        userPlanList.addAll(serviceResponse.userPlanList);
                                        planAdapter.notifyDataSetChanged();
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

private class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>{

    private Context context;
    private List<UserPlanList> userPlanList;

    public PlanAdapter(Context context, List<UserPlanList> userPlanList) {
        this.context = context;
        this.userPlanList = userPlanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_plan_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserPlanList userPlan = userPlanList.get(position);
        holder.tvPlan.setText(TextUtils.isEmpty(userPlan.planName)?"":userPlan.planName);
        holder.tvPlanDetails.setText(TextUtils.isEmpty(userPlan.planDesc)?"":userPlan.planDesc);
    }

    @Override
    public int getItemCount() {
        return userPlanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvPlan, tvPlanDetails;
        public ViewHolder(View itemView) {
            super(itemView);
            tvPlan = (TextView)itemView.findViewById(R.id.tvPlan);
            tvPlanDetails = (TextView)itemView.findViewById(R.id.tvPlanDetails);
            tvPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvPlanDetails.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        }
    }
}
}
