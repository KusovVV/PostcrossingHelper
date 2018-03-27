package com.gmail.victorkusov.postcrossinghelper.model;

import com.gmail.victorkusov.postcrossinghelper.database.RealmDBHelper;
import com.gmail.victorkusov.postcrossinghelper.model.interfaces.IListRealmSaving;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class PlaceList implements IListRealmSaving {

    @SerializedName("postalCodes")
    private List<Place> mPlaces;

    public List<Place> getPlaces() {
        return mPlaces;
    }

    @Override
    public void saveListToRealm() {
        Realm realm = RealmDBHelper.getInstance().getRealm();
        realm.beginTransaction();
        realm.delete(RealmPlace.class);
        for (Place place : mPlaces) {
            RealmPlace code = new RealmPlace(place);
            realm.copyToRealm(code);
        }
        realm.commitTransaction();
    }

    @Override
    public void getListFromRealm() {
        Realm realm = RealmDBHelper.getInstance().getRealm();
        realm.beginTransaction();
        List<RealmPlace> listData = realm.where(RealmPlace.class).findAll();
        realm.commitTransaction();

        if(mPlaces == null) {
            mPlaces = new ArrayList<>();
        }
        mPlaces.clear();
        for (RealmPlace code : listData) {
            Place item = new Place(code);
            mPlaces.add(item);
        }
    }
}
