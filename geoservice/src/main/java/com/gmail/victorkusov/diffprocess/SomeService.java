package com.gmail.victorkusov.diffprocess;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SomeService extends Service {

    private static final long MIN_TIME = TimeUnit.MINUTES.toMillis(10);
    private static final float MIN_DISTANCE = 3000;
    private static final Uri DATABASE_URI = Uri.parse("content://com.gmail.victorkusov.postcrossinghelper/Places");

    public static final String TABLE_PLACES_FIELD_ID = "id";
    public static final String TABLE_PLACES_FIELD_LNG = "longitude";
    public static final String TABLE_PLACES_FIELD_LAT = "latitude";
    public static final String TABLE_PLACES_FIELD_DISTANCE = "distance";
    public static final String TABLE_PLACES_FIELD_COUNTRY_CODE = "countryCode";
    public static final String TABLE_PLACES_FIELD_POSTAL_CODE = "postalCode";
    public static final String TABLE_PLACES_FIELD_PLACE = "place";
    public static final String TABLE_PLACES_FIELD_REGION = "region";

    private static final String TAG = MainActivity.class.getSimpleName();

    final Messenger mMessenger = new Messenger(new MessageHandler());
    private static final int MESSAGE_RESPONSE = 2;
    private List<DistanceCode> mDistanceCodeData;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service");

        if (isFineLocationPermissionEnabled(getBaseContext()) && isCoarseLocationPermissionEnabled(getBaseContext())) {
            manageLocationManager();
        } else {
            startActivity(new Intent(SomeService.this, MainActivity.class));
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mMessenger.getBinder();
    }


    @SuppressLint("MissingPermission")
    public void manageLocationManager() {
        Log.d(TAG, "manageLocationManager: ");
        LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                if (isFineLocationPermissionEnabled(getBaseContext()) || isCoarseLocationPermissionEnabled(getBaseContext())) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "onLocationChanged: get new location: lat:" + location.getLatitude()
                                    + " long: " + location.getLongitude());
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
            getListDataUsingLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
    }

    private void getListDataUsingLocation(Location location) {
        Log.d(TAG, "getListDataUsingLocation: ");

        Call<DistanceCodeList> listCall = RetrofitHelper.getInstance().getNearPlaces(location.getLatitude(), location.getLongitude());
        listCall.enqueue(new Callback<DistanceCodeList>() {
            @Override
            public void onResponse(@NonNull Call<DistanceCodeList> call, @NonNull Response<DistanceCodeList> response) {
                DistanceCodeList body = response.body();
                if (body != null) {
                    final List<DistanceCode> queryList = body.getDistanceCodes();
                    if (queryList != null) {
                        mDistanceCodeData = queryList;
                        Log.d(TAG, "onResponse: got new data uses location");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                saveListDataToSQLite(queryList);
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DistanceCodeList> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void saveListDataToSQLite(List<DistanceCode> queryList) {
        Log.d(TAG, "saveListDataToSQLite: ");
        if (queryList != null) {

            // clean previous values
            ContentResolver resolver = getBaseContext().getContentResolver();
            resolver.delete(DATABASE_URI, null, null);

            List<ContentValues> values = new ArrayList<>();
            int size = queryList.size();
            for (int i = 0; i < size; i++) {
                DistanceCode code = queryList.get(i);
                ContentValues value = new ContentValues();

                value.put(TABLE_PLACES_FIELD_ID, i);
                value.put(TABLE_PLACES_FIELD_LNG, code.getLongitude());
                value.put(TABLE_PLACES_FIELD_LAT, code.getLatitude());
                value.put(TABLE_PLACES_FIELD_DISTANCE, code.getDistance());
                value.put(TABLE_PLACES_FIELD_COUNTRY_CODE, code.getCountryCode());
                value.put(TABLE_PLACES_FIELD_POSTAL_CODE, code.getPostalCode());
                value.put(TABLE_PLACES_FIELD_PLACE, code.getPlace());
                value.put(TABLE_PLACES_FIELD_REGION, code.getRegion());

                values.add(value);
            }

            for (ContentValues value : values) {
                resolver.insert(DATABASE_URI, value);
            }
            Log.d(TAG, "saveListDataToSQLite: data saved");

            //notify new data has come
            resolver.notifyChange(DATABASE_URI, null);
        }
    }


    public static boolean isFineLocationPermissionEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCoarseLocationPermissionEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    class MessageHandler extends Handler {
        private static final String KEY_MESSAGE = "message";
        private static final int REQUEST_FOR_UPDATE = 1;

        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: ");
            switch (msg.what) {
                case REQUEST_FOR_UPDATE: {
                    Messenger messenger = msg.replyTo;
                    Message message = Message.obtain(null, MESSAGE_RESPONSE);

                    message.getData().putString(KEY_MESSAGE, makeMessageWithData(mDistanceCodeData));
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private String makeMessageWithData(List<DistanceCode> distanceCodeData) {
        String result = null;

        if (distanceCodeData != null && !distanceCodeData.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for (DistanceCode code : distanceCodeData) {
                builder.append(code.getId()).append("%");
                builder.append(code.getDistance()).append("%");
                builder.append(code.getRegion()).append("%");
                builder.append(code.getPlace()).append("%");
                builder.append(code.getLand()).append("%");
                builder.append(code.getPostalCode()).append("%");
                builder.append(code.getCountryCode()).append("%");
                builder.append(code.getLatitude()).append("%");
                builder.append(code.getLongitude()).append("%");
                builder.append(code.getIso()).append("&");
            }
            builder.setLength(builder.length() - 1);
            result = new String(builder);
        }

        return result;
    }
}
