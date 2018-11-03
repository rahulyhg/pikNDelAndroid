package com.pikndel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.InterCityDeliveryActivity;
import com.pikndel.activity.IntraCityDeliveryActivity;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;


/**
 * Created by priya.singh on 2/5/16.
 */
public class HomeFragment extends Fragment {

    private Context context;
    private ImageView ivInterCity,ivIntraCity;
    private ImageView ivInter, ivIntra;
    private String intraCityData = "", interCityData = "";
    private TextView tvInter, tvIntra;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_home, null);

        findIds();
        setListeners();
        setTextAttributes();
//        bookExecutiveService();

        return view;
    }

    private void setTextAttributes() {
        tvInter.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvIntra.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
    }

    private void setListeners() {
        ivInterCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, InterCityDeliveryActivity.class));
            }
        });

        ivIntraCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,IntraCityDeliveryActivity.class));
            }
        });

        ivInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialogBox("Intercity - Pick up from one city and delivery to another city. Eg: Pick up from Mumbai and Delivery to Delhi");
            }
        });

        ivIntra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialogBox("Intracity - Pick up & Delivery within the same City. Eg: Pick up & Delivery within Bangalore");
            }
        });
    }

    private void findIds() {
        ivInterCity=(ImageView) view.findViewById(R.id.ivInterCity);
        ivIntraCity=(ImageView) view.findViewById(R.id.ivIntraCity);

        ivInter=(ImageView) view.findViewById(R.id.ivInter);
        ivIntra=(ImageView) view.findViewById(R.id.ivIntra);

        tvInter=(TextView) view.findViewById(R.id.tvInter);
        tvIntra=(TextView) view.findViewById(R.id.tvIntra);
    }

    public void infoDialogBox(String info)  {
        ImageView ivClose;
        TextView tvInfo;
        final Dialog dialog = DialogUtils.createCustomDialog(context, R.layout.dialog_home);
        ivClose= (ImageView) dialog.findViewById(R.id.ivClose);
        tvInfo= (TextView) dialog.findViewById(R.id.tvInfo);

        tvInfo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvInfo.setText(TextUtils.isEmpty(info)?"":info);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void bookExecutiveService() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("HomeFragment", "_____Request_____" + RequestURL.URL_BOOK_EXECUTIVE);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_BOOK_EXECUTIVE, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    if (serviceResponse.data != null && serviceResponse.data.size()>0){
                                        intraCityData = serviceResponse.data.get(0).intraCityData;
                                        interCityData = serviceResponse.data.get(0).interCityData;
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
