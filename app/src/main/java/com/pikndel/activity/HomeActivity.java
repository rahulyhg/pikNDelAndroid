package com.pikndel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.fragment.AboutUsFragment;
import com.pikndel.fragment.CancellationRefundFragment;
import com.pikndel.fragment.FavouriteFragment;
import com.pikndel.fragment.FragmentDrawer;
import com.pikndel.fragment.HomeFragment;
import com.pikndel.fragment.InviteAFriendFragment;
import com.pikndel.fragment.MyPlanFragment;
import com.pikndel.fragment.MyWalletFragment;
import com.pikndel.fragment.OrderStatusFragment;
import com.pikndel.fragment.PrivacyPolictFragment;
import com.pikndel.fragment.ProfileFragment;
import com.pikndel.fragment.TermsConditionsFragment;
import com.pikndel.listeners.FragmentChangeListener;
import com.pikndel.model.UserDetail;
import com.pikndel.model.UserPlanList;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CheckPermission;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.DialogUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements FragmentDrawer.DrawerListener, FragmentDrawer.FragmentDrawerListener, FragmentChangeListener{

    private Intent intent;
    private Context context=HomeActivity.this;

    private ImageView ivRight;
    private TextView tvBookExecutive,tvDeliveryStatus,tvMyPlan,tvMyProfile,tvFavourite,tvMyWallet,tvInviteFriends, tvTermsConditions, tvAboutUs, tvCancel, tvLogout, tvPrivacy;
    private LinearLayout llBookExecutive, llDeliveryStatus, llMyPlan, llMyProfile, llFavourite, llMyWallet, llInviteFriends, llAboutUs, llLogout, llCancel,  llTermsConditions, llPrivacy;
    private ImageView ivBookExecutive, ivDeliveryStatus, ivMyPlan, ivMyProfile, ivFavourite, ivTermsConditions, ivMyWallet, ivInviteFriends, ivAboutUs, ivCancel, ivLogout, ivPrivacy;
    public  ImageView ivLeft;
    public  TextView tvHeader;
    public static Toolbar toolbar;

    private DrawerLayout mDrawerLayout;

    private String section = "REGISTERED";
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefsManager = new PrefsManager(context);
        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra(AppConstant.INTENT_USER_TYPE))){
            section = getIntent().getStringExtra(AppConstant.INTENT_USER_TYPE);
        }


        if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")){
            prefsManager.setKeyUserId("-1");
            if (TextUtils.isEmpty(prefsManager.getKeyPlanId())) {
                getPlanListService();
            }
        }

        findIds();
        setListeners();
        setTextAttributes();

        if (prefsManager.getKeyUserType().equalsIgnoreCase("REGISTERED")){
            if (section.equalsIgnoreCase("PLACED_ORDER")){
                settingFragment(1);
            }else {
                settingFragment(0);
            }
        }else {
            settingFragment(0);
        }


    }


    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvBookExecutive.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDeliveryStatus.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMyPlan.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMyProfile.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvFavourite.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMyWallet.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvInviteFriends.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAboutUs.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvTermsConditions.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCancel.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvLogout.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivLeft.setImageResource(R.mipmap.menu);
        if (section.equalsIgnoreCase("FREE_USER")){
            tvLogout.setText("Exit");
        }else {
            tvLogout.setText("Logout");
        }
    }

    @Override
    public void onBackPressed() {
        if (section.equalsIgnoreCase("FREE_USER")){
            exitFromApp(false);
        }else {
            logoutFromApp();
        }
    }

    private void findIds() {

        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvBookExecutive = (TextView) findViewById(R.id.tvBookExecutive);
        tvDeliveryStatus = (TextView) findViewById(R.id.tvDeliveryStatus);
        tvMyPlan = (TextView) findViewById(R.id.tvMyPlan);
        tvMyProfile = (TextView) findViewById(R.id.tvMyProfile);
        tvFavourite = (TextView) findViewById(R.id.tvFavourite);
        tvMyWallet = (TextView) findViewById(R.id.tvMyWallet);
        tvInviteFriends = (TextView) findViewById(R.id.tvInviteFriends);
        tvTermsConditions = (TextView) findViewById(R.id.tvTermsConditions);
        tvAboutUs = (TextView) findViewById(R.id.tvAboutUs);
        tvCancel = (TextView) findViewById(R.id.tvRefund);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        tvPrivacy = (TextView) findViewById(R.id.tvPrivacy);

        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        ivTermsConditions = (ImageView) findViewById(R.id.ivTermsConditions);
        ivBookExecutive = (ImageView) findViewById(R.id.ivBookExecutive);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
        ivMyPlan = (ImageView) findViewById(R.id.ivMyPlan);
        ivMyProfile = (ImageView) findViewById(R.id.ivMyProfile);
        ivFavourite = (ImageView) findViewById(R.id.ivFavourite);
        ivMyWallet = (ImageView) findViewById(R.id.ivMyWallet);
        ivInviteFriends = (ImageView) findViewById(R.id.ivInviteFriends);
        ivAboutUs = (ImageView) findViewById(R.id.ivAboutUs);
        ivCancel = (ImageView) findViewById(R.id.ivRefund);
        ivLogout = (ImageView) findViewById(R.id.ivLogout);
        ivPrivacy = (ImageView) findViewById(R.id.ivPrivacy);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /*...............Ids for Drawer menu..............*/

        llBookExecutive=(LinearLayout) findViewById(R.id.llBookExecutive);
        llDeliveryStatus=(LinearLayout) findViewById(R.id.llDeliveryStatus);
        llMyPlan=(LinearLayout) findViewById(R.id.llMyPlan);
        llMyProfile=(LinearLayout) findViewById(R.id.llMyProfile);
        llFavourite=(LinearLayout) findViewById(R.id.llFavourite);
        llMyWallet=(LinearLayout) findViewById(R.id.llMyWallet);
        llInviteFriends=(LinearLayout) findViewById(R.id.llInviteFriends);
        llAboutUs=(LinearLayout) findViewById(R.id.llAboutUs);
        llCancel=(LinearLayout) findViewById(R.id.llRefund);
        llLogout=(LinearLayout) findViewById(R.id.llLogout);
        llTermsConditions=(LinearLayout) findViewById(R.id.llTermsConditions);
        llPrivacy=(LinearLayout) findViewById(R.id.llPrivacy);

    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    CommonUtils.hideSoftKeyboard(HomeActivity.this);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        llBookExecutive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(0);
            }
        });

        llDeliveryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(1);
            }
        });

        llFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(4);
            }
        });

        llMyWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(5);
            }
        });


        llMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(3);
            }
        });

        llInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(6);
            }
        });

        llMyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(2);
            }
        });

        llAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(7);
            }
        });

        llTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(8);
            }
        });
        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(9);
            }
        });

        llPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(10);
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(11);
            }
        });

    }

    private void settingFragment(int position){
        Fragment fragment = null;
        ivRight.setVisibility(View.INVISIBLE);
        switch (position){
            case 0:
                fragment = new HomeFragment();
                tvHeader.setText("Home");
//                callBookExecutiveBackground();
                callCancellationRefundBackground(0);
                break;
            case 1:
                if (!section.equalsIgnoreCase("FREE_USER")) {
                    fragment = new OrderStatusFragment();
                    tvHeader.setText("Delivery Status");
//                    callDeliveryStatusBackground();
                    callCancellationRefundBackground(1);
                }else {
                    CommonUtils.showToast(context, "Register or login for this service.");
                }
                break;
            case 2:
                if (!section.equalsIgnoreCase("FREE_USER")) {
                    fragment = new MyPlanFragment();
                    tvHeader.setText("My Plan");
//                    callMyPlanBackground();
                    callCancellationRefundBackground(2);
                }else {
                    CommonUtils.showToast(context, "Register or login for this service.");
                }
                break;
            case 3:
                if (!section.equalsIgnoreCase("FREE_USER")) {
                    fragment = new ProfileFragment();
                    tvHeader.setText("Profile");
//                    callMyProfileBackground();
                    callCancellationRefundBackground(3);
                }else {
                    CommonUtils.showToast(context, "Register or login for this service.");
                }
                break;
            case 4:
                if (!section.equalsIgnoreCase("FREE_USER")) {
                    fragment = new FavouriteFragment();
                    tvHeader.setText("Favourite Location");
//                    callFavoriteBackground();
                    callCancellationRefundBackground(4);
                }else {
                    CommonUtils.showToast(context, "Register or login for this service.");
                }
                break;
            case 5:
                if (!section.equalsIgnoreCase("FREE_USER")) {
                    fragment = new MyWalletFragment();
                    tvHeader.setText("My Wallet");
//                    callMyWalletBackground();
                    callCancellationRefundBackground(5);
                }else {
                    CommonUtils.showToast(context, "Register or login for this service.");
                }
                break;
            case 6:
                fragment = new InviteAFriendFragment();
                tvHeader.setText("Invite Friends");
//                callInviteFriendsBackground();
                callCancellationRefundBackground(6);
                break;
            case 7:
                fragment = new AboutUsFragment();
                tvHeader.setText("About Us");
//                callAboutUsBackground();
                callCancellationRefundBackground(7);
                break;
            case 8:
                fragment = new TermsConditionsFragment();
                tvHeader.setText(R.string.terms_conditions);
//                callTermsConditionsBackground();
                callCancellationRefundBackground(8);
                break;

            case 9:
                fragment = new CancellationRefundFragment();
                tvHeader.setText(R.string.cancellation_refund);
                callCancellationRefundBackground(9);
                break;

            case 10:
                fragment = new PrivacyPolictFragment();
                tvHeader.setText("Privacy Policy");
                callCancellationRefundBackground(10);
                break;
            case 11:
//                callLogoutBackground();
                callCancellationRefundBackground(11);
                if (section.equalsIgnoreCase("FREE_USER")){
                    exitFromApp(true);
                }else {
                    logoutFromApp();
                }
                break;
        }

        mDrawerLayout.closeDrawer(Gravity.LEFT);
        //setting fragment
        if (fragment != null) {
            if (fragment instanceof FavouriteFragment){
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setImageResource(R.mipmap.add);

                ivRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckPermission.checkIsMarshMallowVersion()) {
                            if (!CheckPermission.checkContactsPermission(context)) {
                                CheckPermission.requestContactsPermission((Activity) context, CheckPermission.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            } else {
                                intent = new Intent(HomeActivity.this, AddFavouriteLocationActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            intent = new Intent(HomeActivity.this, AddFavouriteLocationActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }else if (fragment instanceof ProfileFragment){
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setImageResource(R.mipmap.edit);
                ivRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(HomeActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    }
                });
            }else {
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setImageResource(R.mipmap.call_us);
                ivRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.setUpCall(context);
                    }
                });
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CheckPermission.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent(HomeActivity.this, AddFavouriteLocationActivity.class);
                    startActivity(intent);
                } else {
                    CommonUtils.showToast(context, "Permission Denied, Please grant permission to access contacts.");
                }

                break;
        }
    }

    public void logoutFromApp()  {
        final Dialog dialog = DialogUtils.createCustomDialog(context, R.layout.dialog_logout);

        TextView tvMsg = (TextView) dialog.findViewById(R.id.tvMsg);
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);

        tvNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMsg.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvYes.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logoutService();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void logoutService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());


            jsonObject.put(serviceRequest.deviceType, CommonUtils.getPreferences(context, AppConstant.DEVICE_TYPE));
            jsonObject.put(serviceRequest.androidDeviceKey, prefsManager.getKeyDeviceKey());
            jsonObject.put(serviceRequest.deviceToken, CommonUtils.getPreferences(context, AppConstant.DEVICE_TOKEN));

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("HomeActivity", "_____Request_____" + RequestURL.URL_USER_LOGOUT);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_USER_LOGOUT, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    prefsManager.removeFromPreference(PrefsManager.KEY_IS_LOGGED_IN);
                                    prefsManager.removeFromPreference(PrefsManager.KEY_USER_DETAILS);
                                    prefsManager.removeFromPreference(PrefsManager.KEY_USER_ID);
                                    prefsManager.removeFromPreference(PrefsManager.KEY_PLAN_ID);
                                    prefsManager.removeFromPreference(PrefsManager.KEY_WALLET_BALANCE);
                                    prefsManager.removeFromPreference(PrefsManager.KEY_USER_TYPE);
                                    prefsManager.removeFromPreference(PrefsManager.KEY_IS_MULTI_LOCATION);

                                    startActivity(new Intent(context, WelcomeActivity.class)
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

    public void exitFromApp(final boolean isLoginScreen)  {
        final Dialog dialog = DialogUtils.createCustomDialog(context, R.layout.dialog_logout);

        TextView tvMsg = (TextView) dialog.findViewById(R.id.tvMsg);
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);

        tvMsg.setText("Are you sure you want to exit from app?");
        tvMsg.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvYes.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoginScreen){

                    prefsManager.removeFromPreference(PrefsManager.KEY_IS_LOGGED_IN);
                    prefsManager.removeFromPreference(PrefsManager.KEY_USER_DETAILS);
                    prefsManager.removeFromPreference(PrefsManager.KEY_USER_ID);
                    prefsManager.removeFromPreference(PrefsManager.KEY_PLAN_ID);
                    prefsManager.removeFromPreference(PrefsManager.KEY_WALLET_BALANCE);
                    prefsManager.removeFromPreference(PrefsManager.KEY_USER_TYPE);
                    prefsManager.removeFromPreference(PrefsManager.KEY_IS_MULTI_LOCATION);

                    startActivity(new Intent(context, WelcomeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                }else {
                    finish();
                }
                dialog.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
                                        List<UserPlanList> userPlanList = new ArrayList<>();
                                        userPlanList.addAll(serviceResponse.userPlanList);
                                        for (int i = 0; i < userPlanList.size(); i++) {
                                            if (userPlanList.get(i).amount == 0.0) {
                                                String planId = userPlanList.get(i).planId;
                                                prefsManager.setKeyPlanId(planId);
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
    private void callCancellationRefundBackground(int id) {

        tvPrivacy.setTextColor(getResources().getColor(R.color.white));
        llPrivacy.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivPrivacy.setImageResource(R.mipmap.doc);

        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivTermsConditions.setImageResource(R.mipmap.doc);

        tvCancel.setTextColor(getResources().getColor(R.color.white));
        llCancel.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivCancel.setImageResource(R.mipmap.wallet);

        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivAboutUs.setImageResource(R.mipmap.user_info_2);

        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivInviteFriends.setImageResource(R.mipmap.user);


        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivMyWallet.setImageResource(R.mipmap.wallet);

        tvFavourite.setTextColor(getResources().getColor(R.color.white));
        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivFavourite.setImageResource(R.mipmap.star);

        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivMyProfile.setImageResource(R.mipmap.contact);

        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivMyPlan.setImageResource(R.mipmap.edit_1);

        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);

        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivBookExecutive.setImageResource(R.mipmap.tie_);

        tvLogout.setTextColor(getResources().getColor(R.color.white));
        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
        ivLogout.setImageResource(R.mipmap.logout);

        switch (id)
        {
            case 0:
                tvBookExecutive.setTextColor(getResources().getColor(R.color.dark_green));
                llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivBookExecutive.setImageResource(R.mipmap.tie_2);
                break;
            case 1:
                tvDeliveryStatus.setTextColor(getResources().getColor(R.color.dark_green));
                llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivDeliveryStatus.setImageResource(R.mipmap.cart_2);
                break;
            case 2:
                tvMyPlan.setTextColor(getResources().getColor(R.color.dark_green));
                llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivMyPlan.setImageResource(R.mipmap.edit_2);
                break;
            case 3:
                tvMyProfile.setTextColor(getResources().getColor(R.color.dark_green));
                llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivMyProfile.setImageResource(R.mipmap.contact_2);
                break;
            case 4:
                tvFavourite.setTextColor(getResources().getColor(R.color.dark_green));
                llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivFavourite.setImageResource(R.mipmap.user_2);
                break;
            case 5:
                tvMyWallet.setTextColor(getResources().getColor(R.color.dark_green));
                llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivMyWallet.setImageResource(R.mipmap.wallet_2);
                break;
            case 6:
                tvInviteFriends.setTextColor(getResources().getColor(R.color.dark_green));
                llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivInviteFriends.setImageResource(R.mipmap.doc_2);
                break;
            case 7:
                tvAboutUs.setTextColor(getResources().getColor(R.color.dark_green));
                llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivAboutUs.setImageResource(R.mipmap.user_info);
                break;
            case 8:
                tvTermsConditions.setTextColor(getResources().getColor(R.color.dark_green));
                llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivTermsConditions.setImageResource(R.mipmap.doc_2);
                break;
            case 9:
                tvCancel.setTextColor(getResources().getColor(R.color.dark_green));
                llCancel.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivCancel.setImageResource(R.mipmap.wallet_2);
                break;
            case 10:
                tvPrivacy.setTextColor(getResources().getColor(R.color.dark_green));
                llPrivacy.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivPrivacy.setImageResource(R.mipmap.doc_2);
                break;
            case 11:
                tvLogout.setTextColor(getResources().getColor(R.color.dark_green));
                llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
                ivLogout.setImageResource(R.mipmap.logout_2);
                break;
        }
    }

    @Override
    public void onDrawerOpened() {

    }

    @Override
    public void onDrawerClosed() {

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public void onFragmentChangeListener(int position, UserDetail userDetail) {

    }

    @Override
    public void onFragmentChangeListener(int position, String value) {
        settingFragment(position);
    }


//    private void callBookExecutiveBackground() {
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.dark_green));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivBookExecutive.setImageResource(R.mipmap.tie_2);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callDeliveryStatusBackground() {
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.dark_green));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_2);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callMyPlanBackground() {
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.dark_green));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivMyPlan.setImageResource(R.mipmap.edit_2);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callMyProfileBackground() {
//        tvMyProfile.setTextColor(getResources().getColor(R.color.dark_green));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivMyProfile.setImageResource(R.mipmap.contact_2);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callFavoriteBackground() {
//
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.dark_green));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivFavourite.setImageResource(R.mipmap.star_2);
//
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callMyWalletBackground() {
//        tvMyWallet.setTextColor(getResources().getColor(R.color.dark_green));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivMyWallet.setImageResource(R.mipmap.wallet_2);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callInviteFriendsBackground() {
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.dark_green));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivInviteFriends.setImageResource(R.mipmap.user_2);
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callAboutUsBackground() {
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.dark_green));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivAboutUs.setImageResource(R.mipmap.user_info);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }
//
//    private void callTermsConditionsBackground() {
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.dark_green));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivTermsConditions.setImageResource(R.mipmap.doc_2);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//
//        tvLogout.setTextColor(getResources().getColor(R.color.white));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivLogout.setImageResource(R.mipmap.logout);
//    }

//
//
//    private void callLogoutBackground() {
//
//        tvLogout.setTextColor(getResources().getColor(R.color.dark_green));
//        llLogout.setBackgroundResource(R.drawable.drawable_drawer_single_row_white);
//        ivLogout.setImageResource(R.mipmap.logout_2);
//
//        tvTermsConditions.setTextColor(getResources().getColor(R.color.white));
//        llTermsConditions.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivTermsConditions.setImageResource(R.mipmap.doc);
//
//        tvAboutUs.setTextColor(getResources().getColor(R.color.white));
//        llAboutUs.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivAboutUs.setImageResource(R.mipmap.user_info_2);
//
//        tvInviteFriends.setTextColor(getResources().getColor(R.color.white));
//        llInviteFriends.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivInviteFriends.setImageResource(R.mipmap.user);
//
//
//        tvMyWallet.setTextColor(getResources().getColor(R.color.white));
//        llMyWallet.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyWallet.setImageResource(R.mipmap.wallet);
//
//        tvFavourite.setTextColor(getResources().getColor(R.color.white));
//        llFavourite.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivFavourite.setImageResource(R.mipmap.star);
//
//        tvMyProfile.setTextColor(getResources().getColor(R.color.white));
//        llMyProfile.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyProfile.setImageResource(R.mipmap.contact);
//
//        tvMyPlan.setTextColor(getResources().getColor(R.color.white));
//        llMyPlan.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivMyPlan.setImageResource(R.mipmap.edit_1);
//
//        tvDeliveryStatus.setTextColor(getResources().getColor(R.color.white));
//        llDeliveryStatus.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivDeliveryStatus.setImageResource(R.mipmap.cart_1);
//
//        tvBookExecutive.setTextColor(getResources().getColor(R.color.white));
//        llBookExecutive.setBackgroundResource(R.drawable.drawable_drawer_single_row);
//        ivBookExecutive.setImageResource(R.mipmap.tie_);
//    }


}
