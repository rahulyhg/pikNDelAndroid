package com.pikndel.Retrofit;

import com.google.gson.JsonObject;
import com.pikndel.modal.AboutUsModal;
import com.pikndel.modal.BaseResponse;
import com.pikndel.services.ServiceResponse2;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ashimkanshal on 26/8/16.
 */
public interface ApiInterface {

    @POST("/pickndel/updateWallet")
    Observable<BaseResponse> someEndpoint(@Body JsonObject jsonObject);

    @GET("http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/getTandC")
    Observable<ServiceResponse2> tandcApi();

    @GET("/pickndel/get")
    Observable<AboutUsModal> aboutUsApi();

    @GET("http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/refund")
    Observable<ServiceResponse2> refundApi();




    @GET("http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/getAboutUs")
    Observable<ServiceResponse2> AboutApi();
}
