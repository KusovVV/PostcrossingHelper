package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.database.PostalCodeProvider;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;
import com.gmail.victorkusov.postcrossinghelper.service.LocalGeoService;

import java.util.List;
import java.util.Locale;

public class FrgNearPlaces extends BaseFragment {

    public static final String TAG = "LOG " + FrgNearPlaces.class.getSimpleName();
    public static final int TEXT_SIZE_NORMAL = 18;
    public static final int TEXT_SIZE_SMALL = 12;
    private ScrollView mRootScrollView;
    private LocalGeoService mService;

    private boolean hasBounded;


    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalGeoService.LocalBinder binder = (LocalGeoService.LocalBinder) service;
            mService = binder.getService();
            hasBounded = true;

            addElementsToContainerLayout();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            hasBounded = false;
        }
    };

    public FrgNearPlaces() {
    }


    public static FrgNearPlaces newInstance() {
        FrgNearPlaces instance = new FrgNearPlaces();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = inflater.getContext();
        mRootScrollView = new ScrollView(context);

        return mRootScrollView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = getContext();
        if (context != null) {
            context.bindService(new Intent(context, LocalGeoService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (hasBounded) {
            Context context = getContext();
            if (context != null) {
                context.unbindService(mConnection);
            }
        }
    }

    private void addElementsToContainerLayout() {

        LinearLayout containerLayout = new LinearLayout(mRootScrollView.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        mRootScrollView.addView(containerLayout);

//        List<DistanceCode> dataQuery = getDataFromService();
        List<DistanceCode> dataQuery = getQueryFromSQLiteDatabase();

        if (dataQuery != null) {
            Log.d(TAG, "Add elements. Rows:" + dataQuery.size());

            for (DistanceCode code : dataQuery) {

                TextView title = new TextView(containerLayout.getContext());
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_NORMAL);
                title.setText(code.getPostalCode());
                containerLayout.addView(title);

                TextView body = new TextView(containerLayout.getContext());
                body.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SMALL);
                String bodyText = String.format(Locale.getDefault(), "%s, %s", code.getPlace(), code.getCountryCode());
                body.setText(bodyText);
                containerLayout.addView(body);

                TextView distance = new TextView(containerLayout.getContext());
                String distanceText = String.format(Locale.getDefault(), "distance: %f", code.getDistance());
                distance.setText(distanceText);
                containerLayout.addView(distance);


                Log.d(TAG, "addElementsToContainerLayout: id:"+code.getId());
            }
        }
    }

    private List<DistanceCode> getDataFromService() {
        return mService.getDistanceCodeData();
    }

    private List<DistanceCode> getQueryFromSQLiteDatabase() {
        Context context = getActivity();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                return PostalCodeProvider.getDisplayedData(context);
            }
        }
        return null;
    }


    @Override
    public void getListData(String query) {

    }

    @Override
    public void restoreAdapter() {
    }
}
