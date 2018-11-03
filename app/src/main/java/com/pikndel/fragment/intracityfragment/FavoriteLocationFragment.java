package com.pikndel.fragment.intracityfragment;

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
import com.pikndel.activity.intercity.DeliveryDetailsActivity;
import com.pikndel.activity.intercity.PickUpDetailsActivity;
import com.pikndel.activity.intracity.DeliveryLocationActivity;
import com.pikndel.activity.intracity.PickUpLocationActivity;
import com.pikndel.adapter.FavouriteAdapter;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.DeliveryAddress;
import com.pikndel.model.PickupAddress;
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

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class FavoriteLocationFragment extends Fragment {

    private Context context;
    private View view;
    private RecyclerView rvShortlisted;
    private FavouriteAdapter adapter;
    private PrefsManager prefsManager;
    private List<UserFavouriteLocationList> userfavouriteLocationList = new ArrayList<>();
    private String cityName = "";
    private String section = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_favourite_location, container, false);
        CommonUtils.hideSoftKeyboard(context);
        prefsManager = new PrefsManager(context);
        if (getArguments() != null){
            section = getArguments().getString("SECTION", "");
            cityName = getArguments().getString("CITY_INFO", "");
        }
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
    }

    private void settingAdapter(){

        adapter = new FavouriteAdapter(context, userfavouriteLocationList, new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                if (!TextUtils.isEmpty(userfavouriteLocationList.get(position).area)) {
                    if (cityName.equalsIgnoreCase("Delhi/NCR")){


                        cityName = "delhi";

                    }

                    boolean cond1 = cityName.trim().toLowerCase().contains(userfavouriteLocationList.get(position).city.trim().toLowerCase());
                    boolean cond2 = userfavouriteLocationList.get(position).city.trim().toLowerCase().contains(cityName.trim().toLowerCase());
                    if (cond1 || cond2) {
                        if (section.equalsIgnoreCase("DeliveryLocationActivity")) {
//                            ((DeliveryLocationActivity) context).onGetFragmentData(userfavouriteLocationList.get(position).area+","+userfavouriteLocationList.get(position).city+","+userfavouriteLocationList.get(position).pinCode, true);
                            ((DeliveryLocationActivity) context).onGetFragmentData(userfavouriteLocationList.get(position).area, true);
                        } else if (section.equalsIgnoreCase("PickUpLocationActivity")) {
//                            ((PickUpLocationActivity) context).onGetFragmentData(userfavouriteLocationList.get(position).area+","+userfavouriteLocationList.get(position).city+","+userfavouriteLocationList.get(position).pinCode, true);
                            ((PickUpLocationActivity) context).onGetFragmentData(userfavouriteLocationList.get(position).area, true);
                        } else if (section.equalsIgnoreCase("PickUpDetailsActivity")) {
                            PickupAddress pickupAddress = new PickupAddress();
                            pickupAddress.contactName = userfavouriteLocationList.get(position).name;
                            pickupAddress.contactNumber = userfavouriteLocationList.get(position).contactNo;
                            pickupAddress.houseNumber = userfavouriteLocationList.get(position).houseNo;
                            pickupAddress.floor = userfavouriteLocationList.get(position).floor;
                            pickupAddress.landmark = userfavouriteLocationList.get(position).landMark;
                            pickupAddress.pincode = userfavouriteLocationList.get(position).pinCode;
                            pickupAddress.area = userfavouriteLocationList.get(position).area;
                            pickupAddress.isFavourite = "1";
                            ((PickUpDetailsActivity) context).onGetPickupAddress(pickupAddress);
                        } else if (section.equalsIgnoreCase("DeliveryDetailsActivity")) {
                            DeliveryAddress deliveryAddress = new DeliveryAddress();
                            deliveryAddress.contactName = userfavouriteLocationList.get(position).name;
                            deliveryAddress.contactNumber = userfavouriteLocationList.get(position).contactNo;
                            deliveryAddress.houseNumber = userfavouriteLocationList.get(position).houseNo;
                            deliveryAddress.floor = userfavouriteLocationList.get(position).floor;
                            deliveryAddress.landmark = userfavouriteLocationList.get(position).landMark;
                            deliveryAddress.pincode = userfavouriteLocationList.get(position).pinCode;
                            deliveryAddress.area = userfavouriteLocationList.get(position).area;
                            deliveryAddress.isFavourite = "1";
                            ((DeliveryDetailsActivity) context).onGetDeliveryAddress(deliveryAddress);
                        }
                    } else {
                        if(cityName.equals("delhi")){
                            CommonUtils.showToast(context, "Please select pick up location for " + "Delhi/NCR" + ".");
                        }
                        else {
                            CommonUtils.showToast(context, "Please select pick up location for " + cityName + ".");
                        }
                    }
                }else {
                    CommonUtils.showToast(context, "Favourite location doesn't have area.");
                }
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

