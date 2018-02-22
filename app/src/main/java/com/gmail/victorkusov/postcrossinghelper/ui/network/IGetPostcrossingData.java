package com.gmail.victorkusov.postcrossinghelper.ui.network;


import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IGetPostcrossingData {


    @GET("/postalCodeSearchJSON?username=Ostis")
    Call<PostalCodesList> getByPostalCode(@Query("postalcode") String postalCode);

    @GET("/postalCodeSearchJSON?username=Ostis")
    Call<PostalCodesList> getDataByPlaceName(@Query("placename") String placeName);
}
