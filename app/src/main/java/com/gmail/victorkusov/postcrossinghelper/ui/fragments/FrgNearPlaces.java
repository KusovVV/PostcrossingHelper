package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
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

import com.gmail.victorkusov.postcrossinghelper.database.PostalDBHelper;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;
import java.util.Locale;

public class FrgNearPlaces extends BaseFragment {

    public static final String TAG = "LOG " + FrgNearPlaces.class.getSimpleName();


    public FrgNearPlaces() {
    }


    public static FrgNearPlaces newInstance() {
        FrgNearPlaces instance = new FrgNearPlaces();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ScrollView view = new ScrollView(inflater.getContext());

        LinearLayout containerLayout = new LinearLayout(view.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        view.addView(containerLayout);

        addElementsToContainerLayout(containerLayout);
        return view;
    }

    private void addElementsToContainerLayout(LinearLayout containerLayout) {
        PostalDBHelper dbHelper = new PostalDBHelper(containerLayout.getContext());
        List<DistanceCode> dataQuery = dbHelper.query();

        Log.d(TAG, "Add elements. Rows:" + dataQuery.size());

        for (DistanceCode code : dataQuery) {

            TextView title = new TextView(containerLayout.getContext());
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            title.setText(code.getPostalCode());
            containerLayout.addView(title);

            TextView body = new TextView(containerLayout.getContext());
            body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            String bodyText = String.format(Locale.getDefault(),"%s, %s", code.getPlace(), code.getCountryCode());
            body.setText(bodyText);
            containerLayout.addView(body);

            TextView distance = new TextView(containerLayout.getContext());
            String distanceText = String.format(Locale.getDefault(),"distance: %f", code.getDistance());
            distance.setText(distanceText);
            containerLayout.addView(distance);
        }
    }


    @Override
    public void getListData(String query) {

    }

    @Override
    protected DialogInterface.OnClickListener getDialogDeleteListener(PostalCode postalCode) {
        return null;
    }

    @Override
    public void getDataFromRealm() {

    }


}
