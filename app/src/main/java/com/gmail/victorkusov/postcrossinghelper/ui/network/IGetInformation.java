package com.gmail.victorkusov.postcrossinghelper.ui.network;


import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IGetInformation {


    @GET("/postalCodeSearchJSON")
    Call<PostalCodesList> getByPostalCode(@Query("postalcode") String postalCode, @Query("username") String userName);

    @GET("/postalCodeSearchJSON")
    Call<PostalCodesList> getDataByPlaceName(@Query("placename") String postalCode, @Query("username") String login);
}
