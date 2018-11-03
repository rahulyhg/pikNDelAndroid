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
 * Created by Abhishek Tiwari on 7/3/17
 * for Mobiloitte Technologies (I) Pvt. Ltd.
 */
public class PrivacyPolictFragment extends Fragment {

    private Context context;
    private View view;
    private TextView tvTermsConditions;
    private String privacypolicy = "<p>&nbsp;</p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>pikndel.com (Owned by Delfinity Logistics Private Limited)</strong></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"CENTER\"><br /><br /></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"CENTER\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><u><strong>Privacy Policy</strong></u></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Copyright 2015 pikndel.com. All Rights Reserved.</span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #100f0f;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span style=\"background: #ffffff;\">The Privacy Policy is in compliance with the Information Technology (Reasonable security practices and procedures and sensitive personal data or information) Rules, 2011 contained in the Information Technology Act, 2000.</span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; background: #ffffff; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">At PiknDel, we value your trust and we respect the importance of protecting the privacy of members, visitors, and customers to our website. </span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; background: #ffffff; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #000000;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span style=\"background: #ffffff;\">Personal information is essential to meeting our goal to provide you with our services. We treat your personal information, non-public information that identifies you, with respect and in accordance with this Privacy Policy. </span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; background: #ffffff; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">By visiting this website you agree to be bound by the terms and conditions of this Privacy Policy. If you do not agree please do </span></span></span><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><u>not</u></span></span></span><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"> continue using or accessing our website.</span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; background: #ffffff; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">This Privacy Policy is incorporated into and to be read with our Terms and Conditions.</span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; background: #ffffff; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">This Privacy Policy is subject to change at any time without notice. To ensure that you are aware of any changes, please review this policy periodically.</span></span></span></p>\n" +
            "<ol>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>Personal Information</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<ol type=\"a\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">PiknDel collects, stores or uses information that is considered necessary for the purpose of providing the best service possible to all of our customers and service providers. </span></span></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">We may use the information to improve our products and services.</span></span></p>\n" +
            "</li>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">This information may include your name, address, telephone and/or mobile number, email address and other basic information. In case of companies, partnership firms and proprietorship concerns, we will also collect relevant business details such as details pertaining to owners, promoters, shareholders, officers&rsquo; names and their contact details.</span></span></span></span></p>\n" +
            "</li>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">By registering your details on the website, you consent to us using and making available such information to a limited number of service providers to enable them to provide the services requested by you. </span></span></span></span></p>\n" +
            "</li>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">By registering your details on the website, you consent to us contacting you via email, telephone or post, with relevant information pertaining to the services on our website and for other promotional activities including conducting surveys on behalf of third parties. We will not share, trade or sell your information to any third parties not directly associated with its proper use within the website, except when you give us explicit permission and you may at any time opt out of having your personal information shared.</span></span></span></span></p>\n" +
            "</li>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We may monitor or record your communication with our support team for the purpose of monitoring quality, security and training.</span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p lang=\"en-IN\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<ol start=\"2\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>Security Measures</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">All this information will be held by PiknDel using the appropriate safeguards so that the highest possible levels of security, integrity and privacy are maintained.</span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We maintain strict physical, electronic, and administrative safeguards in accordance with applicable standards to protect your personal information from unauthorized or inappropriate access. We restrict access to personal information to employees and service providers who need to know the information for legitimate business purposes to assist in responding to your inquiry or request. Employees who violate our Privacy Policy are subject to disciplinary action.</span></span></span></span></p>\n" +
            "<ol start=\"3\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>Cookies</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">Cookies are small pieces of data stored by your internet browser on your computer's hard drive. We, along with thousands of other websites, use cookies to enhance your Web viewing experience and to measure your website usage. With the help of cookies, we may present you with customized offerings or content that may be of interest to you. We may also use cookies to recognize you on subsequent visits or to remember your user name so you do not have to re-enter it each time you visit our site. You may be able to set your browser to notify you when you receive a cookie or to prevent cookies from being sent. Please note, however, that, by not accepting cookies you may limit the functionality we can provide to you when you visit our site.</span></span></span></span></p>\n" +
            "<ol start=\"4\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>Information We May Disclose and To Whom We May Disclose Information</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We may disclose the information we collect to other organizations as noted below. The others organizations to whom we disclose information are obligated to use such information only for the purposes stated. Disclosures may be made as follows:</span></span></span></span></p>\n" +
            "<ol>\n" +
            "<ol>\n" +
            "<ol type=\"a\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Disclosures</strong></span></span><span style=\"color: #000000;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong> to Service Providers</strong></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "</ol>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 2.54cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We may disclose personal information we collect to others, such service providers that are part of the PiknDel.com network, banks, accountants, and administrators that provide services to us or to you. In all cases, we will require service providers to whom we provide personal information to comply with our Privacy Policy and to use the information solely for the purposes for which we have retained them. Disclosure of personal information to these service providers is done to help us better serve you. </span></span></span></span></p>\n" +
            "<ol>\n" +
            "<ol>\n" +
            "<ol start=\"2\" type=\"a\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Disclosures to Other Affiliated Companies</strong></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "</ol>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 2.54cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We may disclose identifying information, such as name, address, telephone number and transaction or experience information to other affiliated companies. We may disclose personal information to other affiliated companies for purposes such as and including but not limited to: </span></span></span></span></p>\n" +
            "<ol type=\"i\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">Fulfilling your requests; and</span></span></span></span></p>\n" +
            "</li>\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">Offering you other products or services that we think may be of interest to you.</span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<ol>\n" +
            "<ol>\n" +
            "<ol start=\"3\" type=\"a\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Other Disclosures Permitted By Law</strong></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "</ol>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 2.54cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We may make additional disclosures of information as permitted or required by law. For example, we may disclose information to law enforcement agencies or insurance regulators.</span></span></span></span></p>\n" +
            "<ol start=\"5\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>Links to other websites</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">Our website may contain links to other websites of interest. However, once you have used these links to leave our site, you should note that we do not have any control over the other website. Therefore, we cannot be responsible for the protection and privacy of any information which you provide whilst visiting such other sites and such other sites are not governed by this Privacy Policy. You should exercise caution and look at the privacy policy applicable to the website in question.&nbsp;</span></span></span></span></p>\n" +
            "<ol start=\"6\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>Information Retention</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We will retain your personal information only as long as is necessary for the purposes to which you consent under this Privacy Policy, after which we will delete it from our systems.</span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">If you believe that any information we are holding on you is incorrect or incomplete, please write to or email us as soon as possible, at the above address. We will promptly correct any information found to be incorrect.</span></span></span></span></p>\n" +
            "<ol start=\"7\">\n" +
            "<li>\n" +
            "<p style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\"><strong>\"Opting Out\" - Consumer Selections Regarding Information Sharing</strong></span></span></span></span></p>\n" +
            "</li>\n" +
            "</ol>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">We are committed to protecting personal information, and to using or sharing it in ways that will improve or expand upon the services we provide to you. By actively accepting the terms of this Privacy Policy by ticking the acceptance box on www.PiknDel.com you agree to the sharing of personal or consumer report information as described above.</span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">While browsing this website, you will not be provided with the opportunity to opt out of the sharing of personal or consumer report information until you establish a customer relationship.</span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-left: 1.27cm; margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"color: #333333;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><span lang=\"en-IN\">The practices and policies contained in this Privacy Policy replace all previous notices or statements with respect to the same subject.</span></span></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><br /><br /></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Contacting Us</strong></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">If there are any questions regarding this Privacy Policy you may contact us using the information below:</span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><a name=\"_GoBack\"></a> <span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Delfinity Logistics Private Limited</strong></span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">, </span></span><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>1301, Vikrant Tower, 4 Rajendra Place, New Delhi - 110008 </strong></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-top: 0.39cm; margin-bottom: 0.39cm; line-height: 110%;\" align=\"JUSTIFY\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Email: support@pikndel.com</span></span></p>\n" +
            "<p class=\"western\" style=\"margin-bottom: 0.28cm;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\"><strong>Name of Grievance officer</strong></span></span></p>\n" +
            "<p class=\"western\" style=\"margin-bottom: 0.28cm;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">Tullika Batra</span></span></p>\n" +
            "<p class=\"western\" style=\"margin-bottom: 0.28cm;\"><span style=\"font-family: Verdana,serif;\"><span style=\"font-size: small;\">tullika.batra@pikndel.com</span></span></p>\n" +
            "<p class=\"western\" style=\"margin-bottom: 0.28cm;\"><br /><br /></p>";

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

        tvTermsConditions.setText(Html.fromHtml(privacypolicy));

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
