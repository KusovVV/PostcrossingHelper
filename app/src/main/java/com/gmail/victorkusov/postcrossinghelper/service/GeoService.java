package com.gmail.victorkusov.postcrossinghelper.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gmail.victorkusov.postcrossinghelper.database.PostalDBHelper;
import com.gmail.victorkusov.postcrossinghelper.Utils;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCodeList;
import com.gmail.victorkusov.postcrossinghelper.network.RetrofitHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeoService extends Service {

    private static final String TAG = "Log " + GeoService.class.getSimpleName();
    private static final long MIN_TIME = TimeUnit.MINUTES.toMillis(60);
    private static final float MIN_DISTANCE = 5000;   // 5000 meters


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manageLocationManager();
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void manageLocationManager() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            boolean isNetworkroviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkroviderEnabled && Utils.isNetworkPermissonEnabled(this)) {
                if (Utils.isNetworkPermissonEnabled(this) && Utils.isGPSPermissonEnabled(this)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            workWithLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d(TAG, "onStatusChanged: 1");
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d(TAG, "onProviderEnabled: 1");
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d(TAG, "onProviderDisabled: 1");
                        }
                    });
                }
            }
        }
    }

    private void workWithLocation(Location location) {
        Call<DistanceCodeList> listCall = RetrofitHelper.getInstance().getNearPlaces(location.getLatitude(), location.getLongitude());
        listCall.enqueue(new Callback<DistanceCodeList>() {
            @Override
            public void onResponse(@NonNull Call<DistanceCodeList> call, @NonNull Response<DistanceCodeList> response) {
                if (response.body() != null) {
                    List<DistanceCode> queryList = response.body().getDistanceCodes();
                    if (queryList != null) {
                        saveDataToDB(queryList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DistanceCodeList> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void saveDataToDB(List<DistanceCode> queryList) {
        Log.d(TAG, "saveDataToDB: save to DB");
        PostalDBHelper dbHelper = new PostalDBHelper(this);

        dbHelper.eraseTable();
        dbHelper.insert(queryList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
