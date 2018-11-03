package com.pikndel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.pikndel.R;
import com.pikndel.listeners.OnTransactionListner;
import com.pikndel.listeners.RecyclerItemClickListener;
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

import java.util.ArrayList;
import java.util.List;

public class ChangePlanActivity extends AppCompatActivity{

    private Context context=ChangePlanActivity.this;
    private TextView tvWhichPlan, tvCompPlan, tvPlanVsPlan, tvDone, tvHeader;
    private ListView lvPlans;
    private ImageView ivRight, ivLeft;
    private List <UserPlanList> userPlanList = new ArrayList<>();
    private PrefsManager prefsManager;
    private PlanAdapter planAdapter;
    private String planId="";
    private String newPlanId="";
    private float walletAmount = 0.0f;
    private float planAmount = 0.0f;
    //private float amount = 0.0f;
    private float balanceRemain = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_plan);

        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra("MY_PLAN"))){
            planId = getIntent().getStringExtra("MY_PLAN");
            newPlanId = getIntent().getStringExtra("MY_PLAN");
        }
        prefsManager = new PrefsManager(context);
        findIds();
        setListeners();
        setTextAttributes();
        settingAdapter();
        getPlanListService();
        walletAmount = Float.parseFloat(prefsManager.getKeyWalletBalance());
        //walletBalanceService();
    }

    private void settingAdapter(){
        userPlanList.clear();
        planAdapter = new PlanAdapter(context, userPlanList, planId, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                newPlanId = userPlanList.get(position).planId;
                planAmount = userPlanList.get(position).amount;
            }
        });
        lvPlans.setAdapter(planAdapter);
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvWhichPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvWhichPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvPlanVsPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDone.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCompPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

        tvPlanVsPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PlanCompareActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (planId.equalsIgnoreCase(newPlanId)) {
                    CommonUtils.showToast(context, "Already subscribed to this plan.");
                    startActivity(new Intent(context, HomeActivity.class)
                            .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    if (planAmount <= 0.0) {
                        saveUserPlanService(false, String.valueOf(System.currentTimeMillis()/1000));
                    } else {
                        if (walletAmount > planAmount) {
                            walletAmount = walletAmount - planAmount;
                            saveUserPlanService(true, String.valueOf(System.currentTimeMillis()/1000));
                        } else {
                            walletAmount = 0.0f;
                            paytmTransaction(planAmount - walletAmount);
                        }
                    }
                }
            }

        });
    }

    private void findIds() {
        lvPlans=(ListView) findViewById(R.id.lvPlans);
        View headerView = LayoutInflater.from(context).inflate(R.layout.plan_header, null);
        View footerView = LayoutInflater.from(context).inflate(R.layout.plan_footer, null);

        lvPlans.addHeaderView(headerView);
        lvPlans.addFooterView(footerView);

        tvWhichPlan=(TextView)headerView.findViewById(R.id.tvWhichPlan);

        tvCompPlan=(TextView)footerView.findViewById(R.id.tvCompPlan);
        tvPlanVsPlan=(TextView)footerView.findViewById(R.id.tvPlanVsPlan);
        tvDone=(TextView)footerView.findViewById(R.id.tvDone);

        tvHeader=(TextView)findViewById(R.id.tvHeader);
        ivLeft=(ImageView)findViewById(R.id.ivLeft);
        ivRight=(ImageView)findViewById(R.id.ivRight);
        ivLeft.setVisibility(View.VISIBLE);
        tvHeader.setText(R.string.choose_your_plan);
    }


    private void paytmTransaction(float newAmount) {
        new Utils(context, "" + newAmount, new OnTransactionListner() {
            @Override
            public void onTransactionSuccess(TransactionDetails transactionDetails) {
                saveUserPlanService(true, transactionDetails.ORDER_ID);
            }

            @Override
            public void onTransactionfailure(String failureString) {

            }
        }).startTransaction();

    }

    private void getPlanListService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("ChangePlanActivity", "_____Request_____" + RequestURL.URL_GET_PLAN_LIST);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_PLAN_LIST, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.userPlanList != null && serviceResponse.userPlanList.size()>0){
                                        userPlanList.addAll(serviceResponse.userPlanList);
                                        for (int i = 0; i < userPlanList.size(); i++) {
                                            userPlanList.get(i).setExpand(false);

                                            if (!TextUtils.isEmpty(serviceResponse.userPlanList.get(i).planId) && planId.equalsIgnoreCase(serviceResponse.userPlanList.get(i).planId)) {
                                                userPlanList.get(i).setActivated(true);
//                                                planId = serviceResponse.userPlanList.get(i).planId;
//                                                prefsManager.setKeyPlanId(planId);
                                            }else {
                                                userPlanList.get(i).setActivated(false);
                                            }
                                        }
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

    private void saveUserPlanService(final boolean isWallet, final String ORDER_ID) {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("ChangePlanActivity", "_____Request_____" + String.format(RequestURL.URL_SAVE_USER_PLAN, prefsManager.getKeyUserId(), planId));
//                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_SAVE_USER_PLAN,  prefsManager.getKeyUserId(), planId), RequestURL.GET, new ServiceStatus() {
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_SAVE_USER_PLAN,  prefsManager.getKeyUserId(), newPlanId), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                   prefsManager.setKeyPlanId(newPlanId);
                                    prefsManager.setKeyWalletBalance(TextUtils.isEmpty(serviceResponse.walletBalance)?"0.0":serviceResponse.walletBalance);
                                    if (isWallet){
                                        updateWalletBalanceService(ORDER_ID);
                                    }else {
                                        startActivity(new Intent(context, HomeActivity.class)
                                                .putExtra(AppConstant.INTENT_USER_TYPE, "REGISTERED")
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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

    private void updateWalletBalanceService(String ORDER_ID) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", prefsManager.getKeyUserId());
            jsonObject.addProperty("amount", planAmount);
            jsonObject.addProperty("sign", "-");
            jsonObject.addProperty("transactionId", ORDER_ID);

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MyWalletFragment", "_____Request_____" + RequestURL.URL_UPDATE_WALLET);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_UPDATE_WALLET, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    /*if(!TextUtils.isEmpty(serviceResponse.walletBalance)){
                                        prefsManager.setKeyWalletBalance(serviceResponse.walletBalance);
                                    }else {
                                        prefsManager.setKeyWalletBalance("0");
                                    }*/
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

    private class PlanAdapter extends BaseAdapter {

        private Context context;
        private List<UserPlanList> userPlanLists;
        private PrefsManager prefsManager;
        private String planId="";
        private RecyclerItemClickListener listener;

        public PlanAdapter(Context context, List<UserPlanList> userPlanLists, String planId, RecyclerItemClickListener listener) {
            this.context = context;
            this.userPlanLists= userPlanLists;
            this.planId= planId;
            this.listener= listener;
            prefsManager = new PrefsManager(context);
        }

        @Override
        public int getCount() {
            return userPlanLists.size();
        }

        @Override
        public Object getItem(int position) {
            return userPlanLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            final ViewHolder holder;
            if(convertView==null){
                /****** Inflate row_item_layout.xml file for each row ( Defined below ) *******/
                convertView = inflater.inflate(R.layout.row_item_layout, parent, false);
                /****** View Holder Object to contain row_item_layout.xml file elements ******/

                holder = new ViewHolder();
                holder.tvPlan = (TextView)convertView.findViewById(R.id.tvPlan);
                holder.tvPlanDesc = (TextView)convertView.findViewById(R.id.tvPlanDesc);
                holder.llPlanBg = (LinearLayout) convertView.findViewById(R.id.llPlanBg);
                /************  Set holder with LayoutInflater ************/
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final UserPlanList model = userPlanLists.get(position);

            holder.tvPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            holder.tvPlanDesc.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));

            holder.tvPlan.setText(model.planName);
            holder.tvPlanDesc.setText(model.planDesc);

            if (model.isActivated()){
                holder.llPlanBg.setBackground(context.getResources().getDrawable(R.drawable.blue_color_bg));
            }else {
                holder.llPlanBg.setBackground(context.getResources().getDrawable(R.drawable.button_shape));
            }

            if (model.isExpand()){
                if (!TextUtils.isEmpty(holder.tvPlanDesc.getText().toString().trim())) {
                    holder.tvPlan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.up_arr, 0);
                    holder.tvPlanDesc.setVisibility(View.VISIBLE);
                }
            }else{
                if (!TextUtils.isEmpty(holder.tvPlanDesc.getText().toString().trim())) {
                    holder.tvPlan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.down_arr, 0);
                    holder.tvPlanDesc.setVisibility(View.GONE);
                }
            }

            holder.tvPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < userPlanLists.size(); i++) {
                        userPlanLists.get(i).setActivated(false);
                    }
                    userPlanLists.get(position).setActivated(true);

                    if (!model.isExpand()){
                        for (int i=0;i<userPlanLists.size();i++){
                            userPlanLists.get(i).setExpand(false);
                        }
                        userPlanLists.get(position).setExpand(true);
                    }else {
                        for (int i=0;i<userPlanLists.size();i++){
                            userPlanLists.get(i).setExpand(false);
                        }
                    }

                    listener.onItemClickListener(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView tvPlan, tvPlanDesc;
            private LinearLayout llPlanBg;
        }
    }

}

