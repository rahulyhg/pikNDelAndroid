package com.pikndel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pikndel.R;
import com.pikndel.activity.OrderDetailActivity;
import com.pikndel.adapter.PendingFragmentAdapter;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.OrderList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujeet on 02-05-2016.
 */
public class PendingFragment extends Fragment{
    private View view;
    private RecyclerView rv_history;
    private PendingFragmentAdapter adapter;
    private Context context;
    private List<OrderList> orderList = new ArrayList<>();
    private PrefsManager prefsManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.pending_fragment_layout, container, false);
        prefsManager = new PrefsManager(context);

        findIds();

        adapter=new PendingFragmentAdapter(context, orderList, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                startActivity(new Intent(context, OrderDetailActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("SECTION", "Pending")
                        .putExtra("orderList", orderList.get(position)));
            }
        });
        rv_history.setAdapter(adapter);
        getPendingOrderListService();

        return view;
    }

    private void findIds() {
        rv_history=(RecyclerView)view.findViewById(R.id.rvHistory);
        rv_history.setLayoutManager(new LinearLayoutManager(context));
    }

    private void getPendingOrderListService() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("PendingFragment", "_____Request_____" + String.format(RequestURL.URL_GET_PENDING_ORDER_LIST, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_GET_PENDING_ORDER_LIST, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                orderList.clear();
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.orderList != null && serviceResponse.orderList.size()>0){
                                        orderList.addAll(serviceResponse.orderList);
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

}