package com.gmail.victorkusov.postcrossinghelper.utils;


import android.content.Context;
import android.support.v7.app.AlertDialog;

public class MessageDialog {

    private static volatile AlertDialog.Builder builder;

//    public static synchronized AlertDialog.Builder getDialog(Context context) {
//        if (builder == null) {
//            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
//            builder.create();
//        }
//        return builder;
//    }
}
