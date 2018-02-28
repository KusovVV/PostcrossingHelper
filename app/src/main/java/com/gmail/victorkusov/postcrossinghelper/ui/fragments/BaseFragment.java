package com.gmail.victorkusov.postcrossinghelper.ui.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.gmail.victorkusov.postcrossinghelper.ui.adapters.OnItemClickListener;
import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.Utils;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

import io.realm.Realm;


public abstract class BaseFragment extends Fragment {

    protected static final String KEY_QUERY = "query";
    public static final String TEXT_SAVED = "Saved";
    private static final String TAG = "Log" + BaseFragment.class.getSimpleName();
    private Realm realm;

    private String query = "";
    private AlertDialog.Builder builder;
    private OnItemClickListener saveRealmListener;


    protected abstract void getListData(String query);

    protected abstract DialogInterface.OnClickListener getDialogDeleteListener(PostalCode postalCode);

    public abstract void getDataFromRealm();

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
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(KEY_QUERY, "");
        }
        saveRealmListener = new OnItemClickListener() {
            @Override
            public void onItemClick(PostalCode postalCode) {
                saveEntry(postalCode);
            }
        };
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
        if (!Utils.hasNetworkConnection(getContext())) {
            Toast.makeText(getContext(), "No network connection! Check connection and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        if (query != null && !query.isEmpty()) {
            getListData(query);
        }
    }

    private void saveEntry(final PostalCode postalCode) {
        if (builder == null) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        }
        builder.setTitle(String.format(getResources().getString(R.string.manage_item), getResources().getString(R.string.want_to_save)))
                .setMessage(postalCode.getPostalCode() + " " + postalCode.getPlace())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(builder.getContext(), TEXT_SAVED, Toast.LENGTH_SHORT).show();
                        savePostalCodeToRealm(postalCode);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void savePostalCodeToRealm(PostalCode postalCode) {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        PostalCode code = realm.copyToRealm(postalCode);
        realm.commitTransaction();
        Log.d(TAG, "onItemClick: add to realm");
    }

    public List<PostalCode> getPostalCodeListDataFromRealm() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        List<PostalCode> listData = realm.where(PostalCode.class).findAll();
        realm.commitTransaction();
        Log.d(TAG, "getPostalCodeListDataFromRealm: get from realm");
        return listData;
    }

    protected void deleteEntry(final PostalCode postalCode) {
        if (builder == null) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        }
        builder.setTitle(String.format(getResources().getString(R.string.manage_item), getResources().getString(R.string.want_to_delete)))
                .setMessage(postalCode.getPostalCode() + " " + postalCode.getPlace())
                .setPositiveButton(android.R.string.yes, getDialogDeleteListener(postalCode))
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    protected void deletePostalCodeFromRealm(PostalCode postalCode) {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        realm.where(PostalCode.class)
                .equalTo("postalCode", postalCode.getPostalCode()).findFirst().deleteFromRealm();
        realm.commitTransaction();
        Log.d(TAG, "getPostalCodeListDataFromRealm: get from realm");
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public OnItemClickListener getSaveRealmListener() {
        return saveRealmListener;
    }
}
