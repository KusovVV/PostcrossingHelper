package com.gmail.victorkusov.postcrossinghelper.network;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static final String USERNAME = "username";
    private static final String VALUE = "Ostis";

    private static IGetPostcrossingData sPostcrossingData;

    private RetrofitHelper() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter(USERNAME, VALUE)
                                .build();
                        request = request.newBuilder().url(url).build();
                        return chain.proceed(request);
                    }
                }).build();

        Retrofit buider = new Retrofit.Builder()
                .baseUrl("http://api.geonames.org")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .client(client)
                .build();

        sPostcrossingData = buider.create(IGetPostcrossingData.class);
    }

    public static synchronized IGetPostcrossingData getInstance() {
        if (sPostcrossingData == null) {
            new RetrofitHelper();
        }
        return sPostcrossingData;
    }

}
