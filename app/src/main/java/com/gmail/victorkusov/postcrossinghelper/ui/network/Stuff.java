package com.gmail.victorkusov.postcrossinghelper.ui.network;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

public class Stuff {

    public static void hideKeyboard(Activity activity) throws NullPointerException {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean checkConnection(Activity activity) throws NullPointerException {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return mConnectivityManager.getActiveNetworkInfo() == null || !mConnectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
