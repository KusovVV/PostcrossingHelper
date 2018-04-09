package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

import com.gmail.victorkusov.postcrossinghelper.network.INewDataNotify;
import com.gmail.victorkusov.postcrossinghelper.network.ResponseHandler;
import com.gmail.victorkusov.postcrossinghelper.database.PostalCodeProvider;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FrgNearPlaces extends BaseFragment {

    public static final String TAG = "LOG " + FrgNearPlaces.class.getSimpleName();
    public static final int TEXT_SIZE_NORMAL = 18;
    public static final int TEXT_SIZE_SMALL = 12;
    private static final int REQUEST_FOR_UPDATE = 1;


    private static final String GEO_SERVICE_ACTION = "com.gmail.victorkusov.diffprocess.ServiceFromAnotherApp";
    private static final String GEO_SERVICE_PACKAGE = "com.gmail.victorkusov.diffprocess";
    private LinearLayout mContainerLayout;

    private List<List<TextView>> mViews = new ArrayList<>();
    private boolean mBound = false;


    public FrgNearPlaces() {
    }


    public static FrgNearPlaces newInstance() {
        FrgNearPlaces instance = new FrgNearPlaces();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView rootScrollView = new ScrollView(getContext());
        mContainerLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.setLayoutParams(layoutParams);
        mContainerLayout.setOrientation(LinearLayout.VERTICAL);
        rootScrollView.addView(mContainerLayout);

        return rootScrollView;
    }

    private ContentObserver mObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, "onChange: data changed");
            Context context = getContext();
            if (context != null) {
                bindToGeoService(context);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().registerContentObserver(PostalCodeProvider.CONTENT_URI, false, mObserver);
            Utils.startGeoService(context);
        }
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mBound = true;
            Messenger messenger = new Messenger(service);
            Message msg = Message.obtain(null, REQUEST_FOR_UPDATE);
            msg.replyTo = new Messenger(new ResponseHandler(new INewDataNotify() {
                @Override
                public void dataNotify(List<DistanceCode> dataList) {
                    addElementsToContainerLayout(dataList);
                    Log.d(TAG, "dataNotify: data notified");
                }
            }));
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    private void bindToGeoService(Context context) {
        Intent intent = new Intent(GEO_SERVICE_ACTION);
        intent.setPackage(GEO_SERVICE_PACKAGE);
        context.bindService(intent, mServiceConnection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    public void onStop() {
        super.onStop();

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(mObserver);
            if (mBound) {
                context.unbindService(mServiceConnection);
            }
        }
    }

    private void addElementsToContainerLayout(List<DistanceCode> dataList) {

        if (dataList != null) {
            Context context = getContext();

            int size = dataList.size();
            mViews.clear();
            Log.d(TAG, "Add elements. Rows:" + size);
            for (DistanceCode code : dataList) {

                List<TextView> textViews = new ArrayList<>(3);

                TextView title = new TextView(context);
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_NORMAL);
                title.setText(code.getPostalCode());
                textViews.add(title);

                TextView body = new TextView(context);
                body.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SMALL);
                String bodyText = String.format(Locale.getDefault(), "%s, %s", code.getPlace(), code.getCountryCode());
                body.setText(bodyText);
                textViews.add(body);

                TextView distance = new TextView(context);
                String distanceText = String.format(Locale.getDefault(), "distance: %f", code.getDistance());
                distance.setText(distanceText);
                textViews.add(distance);

                mViews.add(textViews);
            }

            mContainerLayout.removeAllViewsInLayout();
            for (List<TextView> views : mViews) {
                for (TextView view : views) {
                    mContainerLayout.addView(view);
                }
            }
        }
    }

    @Override
    public void getListData(String query) {

    }

    @Override
    public void restoreAdapter() {
    }
}
