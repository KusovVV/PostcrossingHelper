package com.gmail.victorkusov.postcrossinghelper;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;

public class PostcrossingHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
