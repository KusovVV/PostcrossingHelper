package com.gmail.victorkusov.postcrossinghelper.database;


import io.realm.RealmConfiguration;

public class RealmDBHelper {
    private static volatile RealmConfiguration instance;

    private RealmDBHelper(){
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("postal-codes.realm")
                .schemaVersion(1)
                .build();
    }

    public static synchronized RealmConfiguration getInstance() {
        if(instance == null){
            new RealmDBHelper();
        }
        return instance;
    }






}
