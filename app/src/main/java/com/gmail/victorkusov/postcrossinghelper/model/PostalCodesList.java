package com.gmail.victorkusov.postcrossinghelper.model;

import com.gmail.victorkusov.postcrossinghelper.database.RealmDBHelper;
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
        Realm realm = RealmDBHelper.getInstance().getRealm();
        realm.beginTransaction();
        realm.delete(RealmPostalCode.class);
        for (PostalCode postalCode : mPostalCodes) {
            RealmPostalCode code = new RealmPostalCode(postalCode);
            realm.copyToRealm(code);
        }
        realm.commitTransaction();
    }

    @Override
    public void getListFromRealm() {
        Realm realm = RealmDBHelper.getInstance().getRealm();

        realm.beginTransaction();
        List<RealmPostalCode> listData = realm.where(RealmPostalCode.class).findAll();
        realm.commitTransaction();

        if (mPostalCodes == null) {
            mPostalCodes = new ArrayList<>();
        }
        mPostalCodes.clear();
        for (RealmPostalCode code : listData) {
            PostalCode item = new PostalCode(code);
            mPostalCodes.add(item);
        }
    }
}
