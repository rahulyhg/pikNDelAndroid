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
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceResponse2;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by priya.singh on 2/5/16.
 */
public class AboutUsFragment extends Fragment{

    private Context context;
    private View view;
    private TextView tvAbout;
    private PrefsManager prefsManager;
    private String aboutUs = "        <p></p>\n" +
            "<p style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #222222;\"><span style=\"font-family: Arial,serif;\"><span style=\"font-size: large;\"><span lang=\"en-IN\">We are a technology-driven company with the aim to provide end-to-end delivery solutions with a plan to revolutionize the field of Delivery and Logistics. Currently operational in on-demand hyperlocal delivery and logistics; we intend to make our presence known in all sectors of of the field by the end of 2017.</span></span></span></span></p>\n" +
            "<p lang=\"en-IN\" style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\">&nbsp;</p>\n" +
            "<p style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #222222;\"><span style=\"font-family: Arial,serif;\"><span style=\"font-size: large;\"><span lang=\"en-IN\">A team of determined, industrious professionals have put in their best ideas and efforts to provide a one-stop quality solution in order to untangle the intricate web of logistics for the ease of our customers. With the ever-growing demographics on internet-penetration and smartphone usage, as well as the booming e-commerce industry &ndash; variety, quality and efficiency have become the indispensable to all transactions.&nbsp;</span></span></span></span></p>\n" +
            "<p lang=\"en-IN\" style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\">&nbsp;</p>\n" +
            "<p style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #222222;\"><span style=\"font-family: Arial,serif;\"><span style=\"font-size: large;\"><span lang=\"en-IN\">Keeping commitments while juggling quality with promptitude has proven to be challenging in the organised delivery sector, even for the big experienced players in the field of e-commerce. Meeting the rising demand for customised delivery plans, our technology-based logistics system offers a one-day delivery solution to any and all who desire it. With our on-demand service, we provide an opportunity even to the smaller players to display competency in the market by optimising their logistics and giving them an option of having pick-up and delivery models custom-fit to their individual needs.</span></span></span></span></p>\n" +
            "<p lang=\"en-IN\" style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\">&nbsp;</p>\n" +
            "<p style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #222222;\"><span style=\"font-family: Arial,serif;\"><span style=\"font-size: large;\"><span lang=\"en-IN\">Moving beyond the traditional focus on B2B channels of logistics, our service is inclusive of the retail market with the promise of the exact same ease of delivery to an individual customer. We aim to provide the best quality pick-up and delivery service within the stipulated time; be it any urgent document or a gift for your loved one, Pikndel will make sure it reaches your doorstep, right on time.</span></span></span></span></p>\n" +
            "<p lang=\"en-IN\" style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\">&nbsp;</p>\n" +
            "<p style=\"margin-bottom: 0cm; background: #ffffff;\" align=\"JUSTIFY\"><span style=\"color: #222222;\"><span style=\"font-family: Arial,serif;\"><span style=\"font-size: large;\"><span lang=\"en-IN\">We plan to, quite literally, deliver on our promises, and so we bring to our customers a platform where engaging a Pikndel service is as effortless as calling a cab or ordering a pizza! All one needs to do is, enter the pickup and drop location and let us know what goods/documents one would like to get delivered; and we shall handle the rest.&nbsp;</span></span></span></span></p>\n" +
            "<p>&nbsp;</p>\n";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        prefsManager = new PrefsManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_about_us, null);


        getIds();
        setFont();
        tvAbout.setText(Html.fromHtml(aboutUs));
//        getAboutUsService();


//        if (!CommonUtils.isOnline(context)) {
//            CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
//        }else {
//
//            getAboutService();
////            getAboutUsService();
//
//        }




        return view;
    }

    private void setFont() {
        tvAbout.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void getIds() {
        tvAbout=(TextView) view.findViewById(R.id.tvAbout);
    }





    private void getAboutService() {

        ApiInterface api = RetrofitModal.getInstance().getRetrofitmodal().create(ApiInterface.class);

        final ProgressDialog progressDialog = ProgressDialog.show(context, "", context.getString(R.string.pls_wait));

        Observable<ServiceResponse2> call = api.AboutApi();
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
                            tvAbout.setText(user.getTAndCData());
                        }else if(user.getCode().equals("201")){
                            CommonUtils.showToast(context, user.getMessage());
                        }else if(user == null){
                            CommonUtils.showToast(context, context.getString(R.string.server_error));
                        }
                    }

                });


    }









    private void getAboutUsService() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("AboutUsFragment", "_____Request_____" + RequestURL.URL_GET_ABOUT_US);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_GET_ABOUT_US, RequestURL.GET, new ServiceStatus() {
//                new ServiceAsync(context, true, jsonObject.toString(), String.format(RequestURL.URL_GET_ABOUT_US, prefsManager.getKeyUserId()), RequestURL.GET, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse2 serviceResponse = (ServiceResponse2) o;


                            if (serviceResponse != null) {

                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {


                                    if(!TextUtils.isEmpty(serviceResponse.data))
                                    {
                                        tvAbout.setText(serviceResponse.data);
                                    }

                                }
                            }

                            else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                            }
                        }

                        catch (Exception e1) {
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
