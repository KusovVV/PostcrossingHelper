package com.gmail.victorkusov.postcrossinghelper.ui.network;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static final String TAG = "LOG RetrofitHelper";

    private static volatile Retrofit instance;

    private RetrofitHelper() {
        instance = new Retrofit.Builder()
                .baseUrl("http://api.geonames.org")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .build();
    }

    public static synchronized Retrofit getInstance() {
        if (instance == null) {
            new RetrofitHelper();
        }
        return instance;
    }

}
