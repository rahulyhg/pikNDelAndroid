package com.pikndel.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.Retrofit.ApiInterface;
import com.pikndel.Retrofit.RetrofitModal;
import com.pikndel.services.ServiceResponse2;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashimkanshal on 29/8/16.
 */
public class CancellationRefundFragment extends Fragment{

    private Context context;
    private View view;
    private TextView tvTermsConditions;
    private String cancel = "<p>&nbsp;</p>\n" +
            "<p style=\"margin-bottom: 0cm;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: large;\"><strong>Cancellation &amp; Refund Policy by Pikndel</strong></span></span></p>\n" +
            "<ol>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">The account holder can only cancel bookings. You will need to contact our Customer Service team and provide your My Parcel Delivery booking reference.</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1cm; margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ol start=\"2\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">If you wish to cancel to your order, kindly contact us and a full refund will be given up until 10pm of the day prior to collection date. After a consignment has been collected, your order cannot be cancelled.</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1cm; margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ol start=\"3\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Any service that is cancelled after an attempt has been made by the courier to collect at the address, or is cancelled after 10pm of the day prior to collection date, will be subject to a wasted journey charge of Rs. 150 or 50% of the order value whichever is higher will be deducted from the total refund due amount. No refund shall be made with respect to cancellation, if Pikndel field executive had reached the pickup location.</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1cm; margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ol start=\"4\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">All requests for refunds must be submitted in writing to Pikndel.com and received by us within 7 days from the date the service was purchased. Refunds can take up to 7 - 10 working days to be processed and you will be notified through email once the amount is transferred. All refunds will be returned via the original method of payment unless stated in your refund request that you would prefer the funds to be added to your Pikndel account funds.</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1cm; margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ol start=\"5\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">We shall not, in any circumstances, be liable to you for any refunds where our Terms and Conditions have not been fully complied with including any items sent on the Prohibited list.</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1cm; margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ol start=\"6\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">We will not be held responsible for wrong information that is entered on a booking. We shall not be liable for any costs or losses that you may suffer that arising directly or indirectly from our failure or delay to perform any of our obligations due to this.</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1cm; margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><a name=\"_GoBack\"></a> <br /><br /></p>\n" +
            "<ol start=\"7\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.05cm; margin-bottom: 0.05cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">We shall not in any circumstances be liable for any late delivery or missed delivery or failure to deliver caused by or contributed to by any deficient or ambiguous labeling of a Consignment and you agree to be responsible</span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p>&nbsp;</p>\n" +
            "<p>&nbsp;</p>";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_terms_conditions, null);

        getIds();
        setFont();
        tvTermsConditions.setText(Html.fromHtml(cancel));
//        if (!CommonUtils.isOnline(context)) {
//            CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
//        }else {
//            getTandCService();
//        }
        return view;
    }

    private void getIds() {
        tvTermsConditions=(TextView) view.findViewById(R.id.tvTermsConditions);
    }

    private void setFont() {
        tvTermsConditions.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void getTandCService() {

        ApiInterface api = RetrofitModal.getInstance().getRetrofitmodal().create(ApiInterface.class);

        final ProgressDialog progressDialog = ProgressDialog.show(context, "", context.getString(R.string.pls_wait));

        Observable<ServiceResponse2> call = api.refundApi();
        Subscription subscription = call.subscribeOn(Schedulers.newThread()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ServiceResponse2>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        CommonUtils.showToast(context, "Please try again later.");
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException)e;
                            int code = response.code();
                        }
                    }
                    @Override
                    public void onNext(ServiceResponse2 user) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        if(user.getCode().equals("200")){
                            tvTermsConditions.setText(TextUtils.isEmpty(user.getTAndCData())?"": Html.fromHtml(user.getTAndCData()));
                        }else if(user.getCode().equals("201")){
                            CommonUtils.showToast(context, user.getMessage());
                        }else if(user == null){
                            CommonUtils.showToast(context, context.getString(R.string.server_error));
                        }
                    }

                });


    }
}


