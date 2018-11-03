package com.pikndel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.model.UserDetail;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.imageUtils.CircleTransform;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by govind_gautam on 30/4/16.
 */
public class ProfileFragment extends Fragment {

    private TextView tvEmail,tvMobileNumber,tvCurrentLocation,tvAccountHolderName,tvIFSCCode,tvBankAccountDetails,tvPersonalDetails,tvProfileName;
    private LinearLayout llPersonalDetails,llBankAccountDetails;

    private View view;
    private Context context;
    private TextView tvEmailBlue, tvMobileNoBlue, tvCurrentLocationBlue, tvAccountHolderNameBlue, tvAccountNumberBlue, tvAccountNumber, tvBankNameBlue
            , tvBankName, tvIFSCCodeBlue, tvAccntTypeBlue, tvAccntType;
    private boolean isHide = false;
    private PrefsManager prefsManager;
    private ImageView ivProfile;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_profile, container, false);

        prefsManager = new PrefsManager(context);
        findIds();
        setListeners();
        setTextAttributes();
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        getUserProfileService();
    }

    private void setTextAttributes() {
        tvProfileName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPersonalDetails.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEmailBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMobileNoBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMobileNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCurrentLocationBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCurrentLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvBankAccountDetails.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAccountHolderNameBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAccountHolderName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAccountNumberBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAccountNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvBankNameBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvBankName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvIFSCCodeBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvIFSCCode.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAccntTypeBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAccntType.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        llPersonalDetails.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        /*tvPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHide){
                    tvPersonalDetails.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.up_arr,0);
                    llPersonalDetails.setVisibility(View.VISIBLE);
                }else {
                    tvPersonalDetails.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.down_arr,0);
                    llPersonalDetails.setVisibility(View.GONE);
                }
                isHide = !isHide;
            }
        });*/
        /*tvPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDrawableImages();
                counter=counter+1;
                if (counter%2 ==0 || counter == 0 ){
                    tvBankAccountDetails.setCompoundDrawables(null,null,upArrow,null);
                    llBankAccountDetails.setVisibility(View.GONE);
                }else {
                    tvBankAccountDetails.setCompoundDrawables(null,null,downArrow,null);
                    llBankAccountDetails.setVisibility(View.GONE);
                }
            }
        });*/
    }



    private void findIds() {
        tvAccountHolderName = (TextView) view.findViewById(R.id.tvAccountHolderName);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvMobileNumber = (TextView) view.findViewById(R.id.tvMobileNumber);
        tvCurrentLocationBlue = (TextView) view.findViewById(R.id.tvCurrentLocationBlue);
        tvCurrentLocation = (TextView) view.findViewById(R.id.tvCurrentLocation);

        tvIFSCCode = (TextView) view.findViewById(R.id.tvIFSCCode);
        tvBankAccountDetails = (TextView) view.findViewById(R.id.tvBankAccountDetails);
        tvPersonalDetails = (TextView) view.findViewById(R.id.tvPersonalDetails);
        tvProfileName = (TextView) view.findViewById(R.id.tvProfileName);
        tvEmailBlue = (TextView) view.findViewById(R.id.tvEmailBlue);
        tvMobileNoBlue = (TextView) view.findViewById(R.id.tvMobileNoBlue);
        tvAccountHolderNameBlue = (TextView) view.findViewById(R.id.tvAccountHolderNameBlue);
        tvAccountNumberBlue = (TextView) view.findViewById(R.id.tvAccountNumberBlue);
        tvAccountNumber = (TextView) view.findViewById(R.id.tvAccountNumber);
        tvBankNameBlue = (TextView) view.findViewById(R.id.tvBankNameBlue);
        tvBankName = (TextView) view.findViewById(R.id.tvBankName);
        tvIFSCCodeBlue = (TextView) view.findViewById(R.id.tvIFSCCodeBlue);
        tvAccntTypeBlue = (TextView) view.findViewById(R.id.tvAccntTypeBlue);
        tvAccntType = (TextView) view.findViewById(R.id.tvAccntType);
        ivProfile = (ImageView) view.findViewById(R.id.ivProfile);

        llBankAccountDetails = (LinearLayout) view.findViewById(R.id.llBankAccountDetails);
        llPersonalDetails = (LinearLayout) view.findViewById(R.id.llPersonalDetails);
    }

    private void getUserProfileService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("ProfileFragment", "_____Request_____" + RequestURL.URL_GET_USER_PROFILE);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_USER_PROFILE, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    UserDetail userDetail = new UserDetail();
                                    userDetail.name = TextUtils.isEmpty(serviceResponse.userName)?"":serviceResponse.userName;
                                    userDetail.email = TextUtils.isEmpty(serviceResponse.email)?"":serviceResponse.email;
                                    userDetail.phoneNumber = TextUtils.isEmpty(serviceResponse.phoneNumber)?"":serviceResponse.phoneNumber;
                                    userDetail.profileImage = TextUtils.isEmpty(serviceResponse.profileImage)?"":serviceResponse.profileImage;
                                    userDetail.location = TextUtils.isEmpty(serviceResponse.location)?"":serviceResponse.location;
                                    userDetail.userId = TextUtils.isEmpty(serviceResponse.userId)?"":serviceResponse.userId;
                                    prefsManager.setKeyUserDetailModel(userDetail);

                                    if (TextUtils.isEmpty(userDetail.profileImage)){
                                        ivProfile.setImageResource(R.drawable.peo_placeholder);
                                    }else {
                                        Picasso.with(context).load(userDetail.profileImage)
                                                .error(R.drawable.peo_placeholder)
                                                .placeholder(R.drawable.peo_placeholder)
                                                .transform(new CircleTransform()).into(ivProfile);
                                    }
                                    tvProfileName.setText(userDetail.name);
                                    tvEmail.setText(userDetail.email);
                                    tvMobileNumber.setText(userDetail.phoneNumber);
                                    tvCurrentLocation.setText(userDetail.location);

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
