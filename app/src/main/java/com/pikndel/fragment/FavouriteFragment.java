package com.pikndel.fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pikndel.R;
import com.pikndel.adapter.FavouriteAdapter;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.UserFavouriteLocationList;
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

public class FavouriteFragment extends Fragment{

    private Context context;
    private View view;
    private RecyclerView rvShortlisted;
    private FavouriteAdapter adapter;
    private PrefsManager prefsManager;
    private List<UserFavouriteLocationList> userfavouriteLocationList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        prefsManager = new PrefsManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_favourite_location, container, false);

        findIds();
        settingAdapter();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListOfFavouriteLocationsService();
    }

    private void findIds() {
        rvShortlisted=(RecyclerView) view.findViewById(R.id.rvShortlisted);
        rvShortlisted.setLayoutManager(new LinearLayoutManager(context));
        rvShortlisted.setPadding(0, 25, 0, 0);
    }

    private void settingAdapter(){

        adapter = new FavouriteAdapter(context, userfavouriteLocationList, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {

            }
        });
        rvShortlisted.setAdapter(adapter);
    }

    private void getListOfFavouriteLocationsService() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("FavouriteFragment", "_____Request_____" + String.format(RequestURL.URL_GET_LIST_OF_FAVOURITE_LOCATIONS, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(),
                        String.format(RequestURL.URL_GET_LIST_OF_FAVOURITE_LOCATIONS, prefsManager.getKeyUserId()),
                        RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    userfavouriteLocationList.clear();
                                    if (serviceResponse.userfavouriteLocationList != null && serviceResponse.userfavouriteLocationList.size()>0){
                                        userfavouriteLocationList.addAll(serviceResponse.userfavouriteLocationList);
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
