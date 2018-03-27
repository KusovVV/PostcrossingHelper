package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    private static final String KEY_LIST_ELEMENT_POSITION = "elementPosition";
    private static final java.lang.String KEY_SHOW_SAVED = "isSaved";

    private int mElementPosition;
    private boolean mShowSaved;

    protected abstract void getListData(String query);
    public abstract void restoreAdapter();


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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clrElemPosition();

        mShowSaved = false;
        if (savedInstanceState != null) {
            mElementPosition = savedInstanceState.getInt(KEY_LIST_ELEMENT_POSITION, -1);
            mShowSaved = savedInstanceState.getBoolean(KEY_SHOW_SAVED, true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreAdapter();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_LIST_ELEMENT_POSITION, mElementPosition);
        outState.putBoolean(KEY_SHOW_SAVED, mShowSaved);
        super.onSaveInstanceState(outState);
    }

    public void refreshData(String query) {
        if (query != null && !query.isEmpty()) {
            getListData(query);
        }
    }

    protected void clrElemPosition() {
        mElementPosition = -1;
    }

    public int getElementPosition() {
        return mElementPosition;
    }

    public void setElementPosition(int elementPosition) {
        mElementPosition = elementPosition;
    }

    public boolean isShowSaved() {
        return mShowSaved;
    }

    public void setShowSaved(boolean showSaved) {
        mShowSaved = showSaved;
    }
}
