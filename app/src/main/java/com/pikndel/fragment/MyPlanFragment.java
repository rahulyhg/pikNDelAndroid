package com.pikndel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.ChangePlanActivity;
import com.pikndel.activity.HomeActivity;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

/**
 * Created by priya.singh on 2/5/16.
 */
public class MyPlanFragment extends Fragment {

    private Context context;
    private View view;
    private TextView tvPlan, tvChangePlan, tvContinue,tvCurrntPlan;
    private String planId;
    private PrefsManager prefsManager;
    private String planDescription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_my_plan, null);

        prefsManager = new PrefsManager(context);
        findIds();
        setListeners();
        setTextAttributes();
        getUserPlanService();

        return view;
    }


    private void setTextAttributes() {
        tvCurrntPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvChangePlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvContinue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }


    private void setListeners() {
        tvChangePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ChangePlanActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("MY_PLAN", planId));
            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).onFragmentChangeListener(0, "");
            }
        });
    }


    private void findIds() {
        tvPlan=(TextView) view.findViewById(R.id.tvPlan);
        tvChangePlan=(TextView) view.findViewById(R.id.tvChangePlan);
        tvContinue=(TextView) view.findViewById(R.id.tvContinue);
        tvCurrntPlan=(TextView) view.findViewById(R.id.tvCurrntPlan);
        tvChangePlan=(TextView) view.findViewById(R.id.tvChangePlan);
    }



    private void getUserPlanService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MyPlanFragment", "_____Request_____" + RequestURL.URL_GET_USER_PLAN);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_USER_PLAN, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    tvPlan.setText(TextUtils.isEmpty(serviceResponse.planName)?"":serviceResponse.planName);
                                    planId = TextUtils.isEmpty(serviceResponse.planId)?"":serviceResponse.planId;
//                                    planId = prefsManager.getKeyPlanId();
                                    prefsManager.setKeyPlanId(planId);
                                    planDescription = TextUtils.isEmpty(serviceResponse.planDescription)?"":serviceResponse.planDescription;


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
