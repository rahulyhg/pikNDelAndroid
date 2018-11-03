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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.OrderDetailActivity;
import com.pikndel.adapter.CompleteFragmentAdapter;
import com.pikndel.adapter.MySpinnerAdapter;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.OrderList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sujeet on 03-05-2016.
 */
public class CompletedFragment extends Fragment {
    private View view;
    private Spinner spMonthFrom, spTO,spYear;
    private RecyclerView rvHistory;
    private CompleteFragmentAdapter adapter;
    private String[] year={"Year", "2015", "2016", "2017", "2018", "2019", "2020"};
    private TextView tvSearch;
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
        view = inflater.inflate(R.layout.completed_fragment_layout, container, false);
        prefsManager = new PrefsManager(context);

        findIds();
        setTextAttributes();
        setSpinner();

        adapter = new CompleteFragmentAdapter(context, orderList, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                startActivity(new Intent(context, OrderDetailActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("SECTION", "completed")
                        .putExtra("orderList", orderList.get(position)));
            }
        });

        rvHistory.setAdapter(adapter);
        getCompletedOrderListService();
        return view;
    }

    private void setTextAttributes() {
        tvSearch.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    int[] monthsNum = {0,1,2,3,4,5,6,7,8,9,10,11};
    private List<String> months = new ArrayList<>();
    ArrayList<String> startMonth = new ArrayList<>();
    ArrayList<String> endMonth = new ArrayList<>();

    MySpinnerAdapter startMonthAdapter;
    MySpinnerAdapter endMonthAdapter;
    MySpinnerAdapter yearAdapter;
    private void setSpinner() {

        for (int i = 0; i < monthsNum.length; i++) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
            cal.set(Calendar.MONTH, monthsNum[i]);
            String month_name = month_date.format(cal.getTime());
            startMonth.add(month_name);
        }

        startMonth.add(0, "Month From");

        startMonthAdapter = new MySpinnerAdapter(context, R.layout.row_custom_spinner, startMonth);
        spMonthFrom.setAdapter(startMonthAdapter);
        spMonthFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spTO.setSelection(0);
                endMonth.clear();
                if (spMonthFrom.getSelectedItemPosition()>0){
                    for (int i = spMonthFrom.getSelectedItemPosition()-1; i < monthsNum.length; i++) {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
                        cal.set(Calendar.MONTH, monthsNum[i]);
                        String month_name = month_date.format(cal.getTime());
                        endMonth.add(month_name);
                    }
                    endMonth.add(0, "To");
                    endMonthAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        endMonth.add(0, "To");
        endMonthAdapter =new MySpinnerAdapter(context, R.layout.row_custom_spinner, endMonth);
        spTO.setAdapter(endMonthAdapter);
        spTO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spTO.getSelectedItemPosition()>0){
                    months.clear();
                    months.addAll(startMonth.subList(spMonthFrom.getSelectedItemPosition(), spTO.getSelectedItemPosition()+spMonthFrom.getSelectedItemPosition()));
                    if (spYear.getSelectedItemPosition()>0){
                        adapter.applyFilters(months, year[spYear.getSelectedItemPosition()]);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearAdapter =new MySpinnerAdapter(context, R.layout.row_custom_spinner, Arrays.asList(year));
        spYear.setAdapter(yearAdapter);
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spTO.getSelectedItemPosition()>0){
                    adapter.applyFilters(months, year[spYear.getSelectedItemPosition()]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void findIds() {
        rvHistory = (RecyclerView) view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(context));
        spMonthFrom=(Spinner)view. findViewById(R.id.spMonthFrom);
        spTO=(Spinner)view. findViewById(R.id.spTO);
        spYear=(Spinner)view. findViewById(R.id.spYear);
        tvSearch=(TextView) view.findViewById(R.id.tvSearch);
    }

    private void getCompletedOrderListService() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("CompletedFragment", "_____Request_____" + RequestURL.URL_GET_COMPLETED_ORDER_LIST);
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_GET_COMPLETED_ORDER_LIST, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                orderList.clear();
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.orderList != null && serviceResponse.orderList.size()>0){
                                        orderList.addAll(serviceResponse.orderList);
                                        adapter = new CompleteFragmentAdapter(context, orderList, new RecyclerItemClickListener() {
                                            @Override
                                            public void onItemClickListener(int position) {
                                                startActivity(new Intent(context, OrderDetailActivity.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        .putExtra("SECTION", "completed")
                                                        .putExtra("orderList", orderList.get(position)));
                                            }
                                        });

                                        rvHistory.setAdapter(adapter);
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