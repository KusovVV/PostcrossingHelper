package com.gmail.victorkusov.postcrossinghelper.model;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.delete(RealmPlace.class);
                for (Place place : mPlaces) {
                    RealmPlace code = new RealmPlace(place);
                    realm.copyToRealm(code);
                }
                realm.commitTransaction();
            }
        }).start();
    }

    @Override
    public void getListFromRealm() {
        final Object o = new Object();
        synchronized (o) {
            if (mPlaces == null) {
                mPlaces = new ArrayList<>();
            }
            mPlaces.clear();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (o) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        List<RealmPlace> listData = realm.where(RealmPlace.class).findAll();
                        realm.commitTransaction();

                        for (RealmPlace code : listData) {
                            Place item = new Place(code);
                            mPlaces.add(item);
                        }
                        o.notify();
                    }
                }
            }).start();
            try {
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
