package com.pikndel.Retrofit;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by ashimkanshal on 26/8/16.
 */
public class RetrofitModal {

    private static RetrofitModal modal;
    public static final String BASE_URL = "http://172.16.5.16:8080/pickndel/";
    //public static final String BASE_URL = "http://54.83.7.62:8080/pickndel/";

    public static RetrofitModal getInstance() {
        if(modal == null){
            modal = new RetrofitModal();
        }

        return modal;
    }

    public Retrofit getRetrofitmodal(){
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.addInterceptor(logging);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
//                .client(builder.build())
                .build();
        return retrofit;
    }


}
