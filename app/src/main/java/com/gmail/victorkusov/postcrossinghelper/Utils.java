package com.gmail.victorkusov.postcrossinghelper;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isConnected(Context context) {
        boolean connected = true;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager != null) {
            connected = !(mConnectivityManager.getActiveNetworkInfo() == null || !mConnectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());
        }
        return connected;
    }

    public static boolean isEmailAndPasswordValid(String email, String password) {
        if (email.isEmpty() || password.length() < 6) {
            return false;
        }
        Matcher matcher = EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
