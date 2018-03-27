package com.gmail.victorkusov.postcrossinghelper.database;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmDBHelper {

    private static final String DB_NAME = "PostcrossingHelper.realm";


    private static volatile RealmDBHelper instance;
    private Realm mRealm;

    private RealmDBHelper() {
        RealmConfiguration builder = new RealmConfiguration.Builder()
                .name(DB_NAME)
                .schemaVersion(0)
                .build();

        mRealm = Realm.getInstance(builder);
    }

    public static synchronized RealmDBHelper getInstance() {
        if (instance == null) {
            instance = new RealmDBHelper();
        }
        return instance;
    }

    public Realm getRealm() {
        return mRealm;
    }


}
