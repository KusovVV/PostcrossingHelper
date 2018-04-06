package com.gmail.victorkusov.postcrossinghelper.model;

import com.gmail.victorkusov.postcrossinghelper.model.interfaces.IListRealmSaving;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class PostalCodesList implements IListRealmSaving {

    @SerializedName("postalCodes")
    private List<PostalCode> mPostalCodes;

    public List<PostalCode> getPostalCodes() {
        return mPostalCodes;
    }

    @Override
    public void saveListToRealm() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.delete(RealmPostalCode.class);
                for (PostalCode postalCode : mPostalCodes) {
                    RealmPostalCode code = new RealmPostalCode(postalCode);
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
            if (mPostalCodes == null) {
                mPostalCodes = new ArrayList<>();
            }
            mPostalCodes.clear();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (o) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        List<RealmPostalCode> listData = realm.where(RealmPostalCode.class).findAll();
                        realm.commitTransaction();

                        for (RealmPostalCode code : listData) {
                            PostalCode item = new PostalCode(code);
                            mPostalCodes.add(item);
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
