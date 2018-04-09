package com.gmail.victorkusov.diffprocess;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface
IGetPostcrossingData {
    @GET("/findNearbyPostalCodesJSON")
    Call<DistanceCodeList> getNearPlaces(@Query("lat") Double lat, @Query("lng") Double lng);
}
