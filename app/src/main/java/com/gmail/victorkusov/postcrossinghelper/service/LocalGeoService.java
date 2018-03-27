package com.gmail.victorkusov.postcrossinghelper.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gmail.victorkusov.postcrossinghelper.ui.widget.GeoWidget;
import com.gmail.victorkusov.postcrossinghelper.database.PostalCodeProvider;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCodeList;
import com.gmail.victorkusov.postcrossinghelper.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class LocalGeoService extends Service {

    private static final String TAG = "Log " + LocalGeoService.class.getSimpleName();
    private static final long MIN_TIME = TimeUnit.MINUTES.toMillis(10);
    private static final float MIN_DISTANCE = 0;   // 5km
//    private static final Uri URI_GEO_LOCATION = Uri.parse("content://com.gmail.victorkusov.postcrossinghelper/Places");

    private final IBinder mBinder = new LocalBinder();
    private List<DistanceCode> mDistanceCodeData;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manageLocationManager();
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            getListDataUsingLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public List<DistanceCode> getDistanceCodeData() {
        return mDistanceCodeData;
    }

    @SuppressLint("MissingPermission")
    private void manageLocationManager() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            boolean isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkProviderEnabled && Utils.isNetworkPermissonEnabled(this)) {
                if (Utils.isNetworkPermissonEnabled(this) && Utils.isGPSPermissonEnabled(this)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "onLocationChanged: get new location");
                            getListDataUsingLocation(location);
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

    private void saveListDataToSQLite() {
        if(mDistanceCodeData != null) {

            // clean previous values
            getContentResolver().delete(PostalCodeProvider.CONTENT_URI, null, null);

            List<ContentValues> values = new ArrayList<>();
            int size = mDistanceCodeData.size();
            for (int i = 0; i < size; i++) {
                DistanceCode code = mDistanceCodeData.get(i);
                ContentValues value = new ContentValues();

                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_ID, i);
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_LNG, code.getLongitude());
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_LAT, code.getLatitude());
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_DISTANCE, code.getDistance());
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_COUNTRY_CODE, code.getCountryCode());
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_POSTAL_CODE, code.getPostalCode());
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_PLACE, code.getPlace());
                value.put(PostalCodeProvider.TABLE_PLACES_FIELD_REGION, code.getRegion());

                values.add(value);
            }

            for (ContentValues value : values) {
                getContentResolver().insert(PostalCodeProvider.CONTENT_URI, value);
            }
            Log.d(TAG, "saveListDataToSQLite: data saved");

            //send notification to widget
            sendBroadcastToWidget();
        }
    }

    private void sendBroadcastToWidget() {
        Intent intent = new Intent(LocalGeoService.this, GeoWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), GeoWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        sendBroadcast(intent);
        Log.d(TAG, "sendBroadcastToWidget: send broadcast to Widget");
    }

    private void getListDataUsingLocation(Location location) {
        Call<DistanceCodeList> listCall = RetrofitHelper.getInstance().getNearPlaces(location.getLatitude(), location.getLongitude());
        listCall.enqueue(new Callback<DistanceCodeList>() {

            @Override
            public void onResponse(@NonNull Call<DistanceCodeList> call, @NonNull Response<DistanceCodeList> response) {
                DistanceCodeList body = response.body();
                if (body != null) {
                    List<DistanceCode> queryList = body.getDistanceCodes();
                    if (queryList != null) {
                        mDistanceCodeData = queryList;
                        Log.d(TAG, "onResponse: got new data uses location");
                        saveListDataToSQLite();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DistanceCodeList> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    public class LocalBinder extends Binder {

       public LocalGeoService getService(){
            return LocalGeoService.this;
        }
    }
}
