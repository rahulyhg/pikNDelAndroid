package com.pikndel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.pikndel.R;
import com.pikndel.activity.PaymentHistoryActivity;
import com.pikndel.listeners.OnTransactionListner;
import com.pikndel.model.TransactionDetails;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.Utils;

import org.json.JSONObject;

public class MyWalletFragment extends Fragment {

    private View view;
    private TextView tvPaymentHistory,tvWalletBal,tvCardType,tvNameOnCard,tvValidUpto,tvAddMoney,tvAddNewCard, tvWalletBlnce, tvSavedCard;
    private TextView tvCardNo, tvCardTypeBlue, tvNameCard, tvValid, tvCardNumb;
    private Context context=getActivity();
    private PrefsManager prefsManager;
    private EditText et_addmoney;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_mywallet,null);

        prefsManager = new PrefsManager(context);

        findIds();
        setListeners();
        setTextAttributes();
        walletBalanceService();

        return view;

    }

    private void setTextAttributes() {
        tvWalletBal.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPaymentHistory.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvWalletBlnce.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCardNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCardNumb.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvValid.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNameCard.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCardTypeBlue.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCardType.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvNameOnCard.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvValidUpto.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvSavedCard.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvAddMoney.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvAddNewCard.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        et_addmoney.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
    }

    private void setListeners() {
        tvPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PaymentHistoryActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        tvAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = et_addmoney.getText().toString().toString();

                if(amount.length() <= 0 ){
                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_SHORT).show();
                }else if(Integer.parseInt(amount) <= 0){
                    Toast.makeText(getActivity(), "Please enter valid amount", Toast.LENGTH_SHORT).show();
                }else{
                    makeTransaction();
                }
            }


        });

        tvAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void findIds() {
        tvPaymentHistory = (TextView) view.findViewById(R.id.tvPaymentHistory);
        tvWalletBlnce = (TextView) view.findViewById(R.id.tvWalletBlnce);
        tvWalletBal = (TextView) view.findViewById(R.id.tvWalletBal);
        tvCardNumb = (TextView) view.findViewById(R.id.tvCardNumb);
        tvCardType = (TextView) view.findViewById(R.id.tvCardType);
        tvNameOnCard = (TextView) view.findViewById(R.id.tvNameOnCard);
        tvValidUpto = (TextView) view.findViewById(R.id.tvValidUpto);
        tvAddMoney = (TextView) view.findViewById(R.id.tvAddMoney);
        tvAddNewCard = (TextView) view.findViewById(R.id.tvAddNewCard);
        tvSavedCard = (TextView) view.findViewById(R.id.tvSavedCard);
        tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
        tvCardTypeBlue = (TextView) view.findViewById(R.id.tvCardTypeBlue);
        tvNameCard = (TextView) view.findViewById(R.id.tvNameCard);
        tvValid = (TextView) view.findViewById(R.id.tvValid);
        et_addmoney = (EditText) view.findViewById(R.id.et_addmoney);
    }

    private void makeTransaction() {
        if (CommonUtils.isOnline(context)) {
            new Utils(getActivity(), et_addmoney.getText().toString().trim(), new OnTransactionListner() {
                @Override
                public void onTransactionSuccess(TransactionDetails transactionDetails) {
                    if (!CommonUtils.isOnline(context)) {
                        CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
                    } else {
                        et_addmoney.setText("");
                        updateWalletBalanceService(transactionDetails);
                    }
                }

                @Override
                public void onTransactionfailure(String failureString) {

                }
            }).startTransaction();
        }else {
            CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
        }
    }

    private void walletBalanceService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MyWalletFragment", "_____Request_____" + String.format(RequestURL.URL_WALLET_BALANCE, prefsManager.getKeyUserId()));
                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_WALLET_BALANCE, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    if(!TextUtils.isEmpty(serviceResponse.walletBalance)){
                                        prefsManager.setKeyWalletBalance(serviceResponse.walletBalance);
                                        tvWalletBal.setText("₹ "+serviceResponse.walletBalance);

                                    }else {
                                        tvWalletBal.setText("₹ 0");
                                        prefsManager.setKeyWalletBalance("0");
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



    private void updateWalletBalanceService(TransactionDetails amount) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", prefsManager.getKeyUserId());
            jsonObject.addProperty("amount", Float.parseFloat(amount.TXN_AMOUNT));
            jsonObject.addProperty("sign", "+");
            jsonObject.addProperty("transactionId", amount.ORDER_ID);
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("MyWalletFragment", "_____Request_____" + RequestURL.URL_UPDATE_WALLET);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_UPDATE_WALLET, RequestURL.POST , new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                    if(!TextUtils.isEmpty(serviceResponse.walletBalance)){
                                        prefsManager.setKeyWalletBalance(serviceResponse.walletBalance);
                                        tvWalletBal.setText("₹ "+serviceResponse.walletBalance);
                                    }else {
                                        tvWalletBal.setText("₹ 0");
                                        prefsManager.setKeyWalletBalance("0");
                                    }
                                    walletBalanceService();

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
