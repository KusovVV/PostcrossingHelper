package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


public abstract class BaseFragment extends Fragment {

    protected static final String KEY_QUERY = "query";

    private String query = "";


    public abstract void getListData(String query);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(KEY_QUERY, "");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString(KEY_QUERY, query);
        super.onSaveInstanceState(outState);
    }

    public void refreshData() {
        if (query != null && !query.isEmpty()) {
            getListData(query);
        }
    }

    public static BaseFragment newFragment(String frgTag) {
        if (frgTag.equals(FrgPostalCode.TAG)) {
            return FrgPostalCode.newInstance();
        }
        if (frgTag.equals(FrgPlace.TAG)) {
            return FrgPlace.newInstance();
        }
        if (frgTag.equals(FrgNearPlaces.TAG)) {
            return FrgNearPlaces.newInstance();
        }
        return null;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


}
