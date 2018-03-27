package com.gmail.victorkusov.postcrossinghelper.network;


import com.gmail.victorkusov.postcrossinghelper.model.DistanceCodeList;
import com.gmail.victorkusov.postcrossinghelper.model.PlaceList;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCodesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGetPostcrossingData {


    @GET("/postalCodeSearchJSON")
    Call<PostalCodesList> getByPostalCode(@Query("postalcode") String postalCode);

    @GET("/postalCodeSearchJSON")
    Call<PlaceList> getDataByPlaceName(@Query("placename") String placeName);

    @GET("/findNearbyPostalCodesJSON")
    Call<DistanceCodeList> getNearPlaces(@Query("lat") Double lat, @Query("lng") Double lng);
}
