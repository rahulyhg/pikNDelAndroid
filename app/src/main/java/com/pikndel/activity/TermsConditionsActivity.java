package com.pikndel.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
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

public class TermsConditionsActivity extends AppCompatActivity {
    private Context context=TermsConditionsActivity.this;
    private TextView tvHeader,tvTermsConditions;
    private ImageView ivLeft, ivRight;
    private String terms = "<p>&nbsp;</p>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"CENTER\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Terms and Conditions</strong></span></span></p>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Copyright 2015 PiknDel.com. All Rights Reserved.</span></span></p>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">At PiknDel.com, we aim to provide customers with access to servicemen, to carry out the service specified below, in a timely and hassle-free manner. On booking services the following terms and conditions are accepted and agreed to by the customer:</span></span></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Interpretation &amp; General&nbsp;<br /></strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">\"Client\" is the person, firm, company or organization for whom pikndel has agreed to provide the Services in accordance with these conditions; \"Contract\" is the Contract for the facilitation of Errand Services which shall be governed by these conditions; \"Services\" means the Errand Services to be provided by Pikndel to or for the Client; \"Charge\" means the Charge payable by the Client to Pikndel as notified by PIKNDEL from time to time.<br /><br />Pikndel shall be entitled to alter and vary these conditions from time to time without any liability to the Client. It is the Client&rsquo;s responsibility to check these pages periodically for any updates.<br /><br />Pikndel&rsquo;s working hours are 9am-7pm, Monday to Sunday. Where Pikndel is required to provide Services outside of these hours, Pikndel shall be entitled to make an extra Charge to the Client for this reason, and the same shall be notified to the Client ahead of time. Outside normal hours, the Client may reach Pikndel by telephone, voice, or e-mail. Pikndel will respond to all messages left by the Client as soon as possible.<br /><br />Telephone calls between Pikndel and the Client may be recorded and monitored from time to time.</span></span></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><br /><br /></p>\n" +
            "<p style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><br /><br /></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Supply of the Services<br /></strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Pikndel shall provide the Services to the Client subject to these Conditions or such other conditions as may be agreed between Pikndel and the Client.<br /></span></span><br /><br /></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">The Service permits the Client to instruct Pikndel to facilitate errands for or on the behalf of the Client. This may involve the pick up and/ or delivery of personal or other items or goods. Pikndel does not currently provide the broad spectrum of services as mainstream lifestyle management companies, and therefore reserves the right to decline to facilitate requests requiring non-specialist errand running skills. Pikndel may also decline to run an errand, which it deems immoral or unlawful.<br /><br />The turnaround time for normal errands (errands requiring no waiting, within Pikndel area of coverage, not encumbered by other agreements e.g. specified &lsquo;time-bound&rsquo; and &lsquo;urgent&rsquo;) will be decided by Pikndel Team. However Pikndel intends to facilitate all errand requests within the same working day, if Pikndel has received such request by the errand reception-cut-off time. Requests received after reception-cut-off time may be facilitated the following working day.<br /><br />Where Pikndel is unable to run any errands it will inform the Client as soon as is reasonably possible.</span></span></p>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Charges</strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><br />Subject to any special terms agreed, the Client shall pay Pikndel the Charge and any additional sums agreed between Pikndel and the Client for the provision of the Services.<br /><br />Pikndel shall be entitled to vary the Charge from time to time and shall communicate any such changes to the Client before any payment is made.</span></span></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><br />Pikndel shall be entitled to invoice the Client for any incidental costs incurred during the facilitation of the Client&rsquo;s request, including costs from unexpected delays, toll charges etc.<br /><br />All payments made to Pikndel via Credit Card transactions are liable to a handling charge. This handling charge is added to the total sum owed to Pikndel by the Client.<br /><br />All quotations given and charges mentioned will be exclusive of Service Tax unless otherwise stated.<br /><br />The Charge and any additional sum due shall be paid by the Client (without any set off, counterclaim or other deduction) in advance.<br /><br />A late payment penalty of the total invoiced charge will be payable to Pikndel if payments not received by Pikndel.</span></span></p>\n" +
            "<p style=\"margin-left: 1.25cm; margin-bottom: 0cm; widows: 0; orphans: 0;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">The applicable Charges will be based upon the characteristics of the courier/parcel/shipment actually tendered to us. If you dispute any Charges you must let Pikndel know within thirty (30) days after the date that Pikndel bills your payment instrument. We reserve the right to change Pikndel&rsquo;s rates. If Pikndel does change any of its rates, Pikndel will post the new rates to the Service, effective as of the posting date. Your continued use of the Service after the price change becomes effective constitutes your agreement to pay the changed amount.</span></span></p>\n" +
            "<p style=\"margin-left: 1.25cm; margin-bottom: 0cm; widows: 0; orphans: 0;\">&nbsp;</p>\n" +
            "<p style=\"margin-left: 1.25cm; margin-bottom: 0cm; widows: 0; orphans: 0;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Shippers/Consignors are responsible for providing accurate and complete shipment information to Pikndel, including service selected, number, weight, and dimensions of shipments. If any aspect of the shipment information is incomplete or incorrect as determined by Pikndel in its sole discretion, Pikndel may adjust Charges at any time.</span></span></p>\n" +
            "<p style=\"margin-bottom: 0cm; widows: 0; orphans: 0;\">&nbsp;</p>\n" +
            "<p style=\"margin-left: 1.25cm; margin-bottom: 0cm; widows: 0; orphans: 0;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">The price quote displayed in the Service, either on the Site or in-app, for your particular courier/parcel/shipment requirement (hereinafter &ldquo;Quote&rdquo;) is an estimate and not the exact price or the final price that you will be charged. The exact charges finally applicable to you will be based on the actual weight and dimensions of the courier once in the final packed state. </span></span></p>\n" +
            "<p style=\"margin-left: 1.25cm; margin-bottom: 0cm; widows: 0; orphans: 0;\" align=\"JUSTIFY\">&nbsp;</p>\n" +
            "<p style=\"margin-left: 1.25cm; margin-bottom: 0cm; widows: 0; orphans: 0;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">The Quote is for your reference to know the approximate cost you will incur. The Quotes are produced through Pikndel&rsquo;s statistical analysis of shipment data of items similar to the item you are requesting to be shipped. The Quotes showed to you are inclusive of all taxes, charges, and fees applicable for the shipment.<br /></span></span></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Client&rsquo;s Responsibilities<br /></strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Services provided by Pikndel are provided expressly for the Client or any person or group named by the Client to receive the Service.<br /><br />The Client shall not request of Pikndel to run errands which are immoral or unlawful in nature.<br /><br />The Client shall endeavor to provide Pikndel with as much detailed information as possible regarding their request in order for Pikndel to provide excellent service.<br /><br />The Client shall not ask Pikndel to run errands to, from and for people or places where Pikndel staff or associates may experience any form of abuse, bodily harm or death.<br /><br />If the Client should request that Pikndel use the Client&rsquo;s credit card and /or credit facilities for the purpose of rendering Services, the Client shall, promptly and upon request, provide written confirmation of such authorization (in such form as Pikndel shall request) for Pikndel to use any such credit facility. The Client acknowledges and agrees that Pikndel shall have no liability or be responsible in any way whatsoever in respect of the use of the Client&rsquo;s credit card and /or other credit card facilities provided that Pikndel acts in accordance with the instructions issued by the Client in relation thereto.</span></span></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><br /><br /></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Termination and refunds<br /></strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">When the Client has entered into an agreement with Pikndel by assigning an errand and making payment for same, requests for refunds may only be accommodated in the following circumstances:</span></span></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<ol>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">where Pikndel has not already begun to process the request.</span></span></p>\n" +
            "</li>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">where the errand does not require same day facilitation, in which case a minimum 24hour notice period by the Client is applicable.</span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p style=\"margin-left: 1.25cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Pikndel will make refunds less direct costs to itself including any handling costs<br /><br />Without prejudice to any other rights and remedies available, Pikndel shall have the right to terminate the Contract for the provision of all or any of the Services upon written notice if the Client commits a serious breach of these conditions.<br /><br />On termination for any reason whatsoever, the Client shall immediately make payment to Pikndel of all and any sums outstanding and owing to Pikndel under these conditions (including the Charge or any outstanding balance)</span></span></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Liability</strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><br />Pikndel shall not be liable for any loss, cost, expense or damage of any nature whatsoever exceeding Rs.5000 (whether direct or indirect) resulting from the use of Pikndel Services. In case of special request you can call us at 011 - 25747474 for Insurance of your parcel.<br /><br />Pikndel warrants to the Client that Pikndel shall use all of its reasonable endeavors to provide the Services using reasonable care and skill and, as far as reasonably possible, in accordance with the Clients requests and instructions.<br /><br />Pikndel shall have no liability to the Client for any loss, damage, costs, expenses or other claims for compensation arising from requests or instructions supplied by the Client which are incomplete, incorrect or inaccurate or arising from their late arrival or non-arrival, or any other fault of the Client.<br /><br />Pikndel shall not be liable or be deemed to be in breach of the Contract by reason of any delay in performing, or any failure, any of Pikndel&rsquo;s obligations in relation to the Services, if the delay or failure was due to any cause beyond Pikndel&rsquo;s reasonable control.<br /><br />Subject to the provisions of these Conditions of Service, the maximum liability of Pikndel to the Client for breach of any of its obligations hereunder shall be limited to the value of the Charge (provided that the Charge has at such time been paid by the Client in full).</span></span></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><br /><br /></p>\n" +
            "<ul>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>General</strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><br />These conditions (together with any other terms and conditions agreed in writing between Pikndel and the Client from time to time) constitute the entire agreement between the parties, and supercede any previous agreement or understanding and may not be varied except on notice from Pikndel. All other terms and conditions express or implied by a statute or otherwise are excluded to the fullest extents permitted by Law.<br /><br />Any notice required or permitted to be given by either party to the other under these conditions shall be in writing addressed to the other party at it&rsquo;s registered office or principal place of business or residential address (as the case may be) or such other address as may at the relevant time have been notified pursuant to the provision to the party giving notice. Any notice may be sent by first class post, facsimile transmission or email and notice shall be deemed to have been served on the expiry of 48 hours in the case of post or at the time of transmission in the case of facsimile or email transmission.<br /><br />No failure or delay by Pikndel in exercising any of its rights under the Contract shall be deemed to be a waiver of that right, and no waiver by Pikndel of any breach of the Contract by the Client shall be considered as a waiver of any subsequent breach of the same or any other provision.<br /><br />If any provision of these conditions is held by any competent authority to be invalid or unenforceable in whole or in parts, the validity of the other provisions of these conditions will still stand.<br /><br />These conditions and the Contract to which they relate shall be governed and construed in accordance with the Indian Law.</span></span></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<p style=\"margin-bottom: 0cm;\">&nbsp;</p>\n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        findIds();
        setTextAttributes();
        setListeners();
        tvTermsConditions.setText(Html.fromHtml(terms));
//        Toast.makeText(context, "termandCondition", Toast.LENGTH_SHORT).show();

//        getTandCService();

    }
    private void setTextAttributes() {
        tvHeader.setText("Terms & Conditions");
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
    }
    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });
    }

    private void findIds() {
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        tvTermsConditions=(TextView)findViewById(R.id.tvTermsConditions);
    }
/*
    private void getTandCService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("TermsConditionsActivity", "_____Request_____" + RequestURL.URL_GET_T_AND_C);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_T_AND_C, RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {

                                   if(!TextUtils.isEmpty(serviceResponse.tAndCData))
                                   {
                                       tvTermsConditions.setText(serviceResponse.tAndCData);
                                   }

                                } else {
                                    CommonUtils.showToast(context, serviceResponse.message);

                                    Log.e("beforeMessage","message"+serviceResponse.message);
                                }
                            } else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                                Log.e("AfterMessage", "message" + serviceResponse.message);
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
    }*/

    private void getTandCService() {

        ApiInterface api = RetrofitModal.getInstance().getRetrofitmodal().create(ApiInterface.class);

        final ProgressDialog progressDialog = ProgressDialog.show(context, "", context.getString(R.string.pls_wait));

        Observable<ServiceResponse2> call = api.tandcApi();
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
                            tvTermsConditions.setText(user.getTAndCData());
                        }else if(user.getCode().equals("201")){
                            CommonUtils.showToast(context, user.getMessage());
                        }else if(user == null){
                            CommonUtils.showToast(context, context.getString(R.string.server_error));
                        }
                    }

                });


    }
}
