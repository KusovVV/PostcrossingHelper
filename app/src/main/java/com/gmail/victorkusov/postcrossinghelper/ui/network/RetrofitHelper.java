package com.gmail.victorkusov.postcrossinghelper.ui.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;
import com.gmail.victorkusov.postcrossinghelper.ui.adapters.DataListAdapter;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static final String TAG = "LOG RetrofitHelper";
    public static final String USER_NAME = "Ostis";

    private static volatile RetrofitHelper instance;
    private Retrofit retrofit;
    private PostalCodesList list;


    private RetrofitHelper() {
    }

    public static synchronized RetrofitHelper getInstance() {
        if (instance == null) {
            instance = new RetrofitHelper();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.geonames.org")
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .create()))
                    .build();
        }
        return retrofit;
    }
}
