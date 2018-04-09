package com.gmail.victorkusov.postcrossinghelper.utils;


import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gmail.victorkusov.postcrossinghelper.model.InputData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRACK_CODE_REGEX = Pattern.compile("^\\w{2}+\\d{9}+\\w{2}$");

    private static final String GEO_SERVICE_ACTION = "com.gmail.victorkusov.diffprocess.ServiceFromAnotherApp";
    private static final String GEO_SERVICE_PACKAGE = "com.gmail.victorkusov.diffprocess";
    private static final String GEO_SERVICE_PERMISSION = "com.gmail.victorkusov.diffprocess.START_INNER_SERVICE";


    private static final int REPORT_PAGE_WIDTH = 150;

    private static final float TITLE_TEXT_SIZE = 8;
    private static final float BODY_TEXT_SIZE = 6;

    private static final float TITLE_X_OFFSET = 30;
    private static final float TITLE_Y_OFFSET = 8;

    private static final float START_Y_POINTER = 12;
    private static final float START_X_OFFSET_LINE = 0;

    private static final float LINE_X_OFFSET = 10;
    private static final String REPORT_TITLE_TEXT = "Track events report";

    private static final String BODY_TEXT_DATE = "Date:";
    private static final String BODY_TEXT_EVENT = "Event:";
    private static final String BODY_TEXT_PLACE = "Place:";


    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean hasNetworkConnection(Context context) {
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

    public static boolean isGPSPermissonEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isNetworkPermissonEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isGeoServicePermissonEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), GEO_SERVICE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void startGeoService(Context context) {
        Intent intent = new Intent(GEO_SERVICE_ACTION);
        intent.setPackage(GEO_SERVICE_PACKAGE);
        context.startService(intent);
    }

    public static boolean isFilesPermissonEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isTrackCodeValid(String trackCode) {
        Matcher matcher = TRACK_CODE_REGEX.matcher(trackCode);
        return matcher.find();
    }

    public static File makeFile(String fileName, List<InputData> dataList) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(REPORT_PAGE_WIDTH, dataList.size() * 45, 1).create();
        PdfDocument.Page page = document.startPage(info);
        drawPage(page, dataList);
        document.finishPage(page);

        File root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
        FileOutputStream stream;
        File outFile = new File(root.getPath(), fileName.concat(".pdf"));
        try {
            stream = new FileOutputStream(outFile);
            document.writeTo(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
    }

    private static void drawPage(PdfDocument.Page page, List<InputData> dataList) {
        Canvas canvas = page.getCanvas();

        float linePointer = START_Y_POINTER;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(TITLE_TEXT_SIZE);
        canvas.drawText(REPORT_TITLE_TEXT, TITLE_X_OFFSET, TITLE_Y_OFFSET, paint);

        paint.setTextSize(BODY_TEXT_SIZE);
        for (InputData inputData : dataList) {
            canvas.drawText(BODY_TEXT_DATE, START_X_OFFSET_LINE, linePointer, paint);
            linePointer += 6;
            canvas.drawText(inputData.getDateTime(), LINE_X_OFFSET, linePointer, paint);
            linePointer += 6;
            canvas.drawText(BODY_TEXT_EVENT, START_X_OFFSET_LINE, linePointer, paint);
            linePointer += 6;
            canvas.drawText(inputData.getEvent(), LINE_X_OFFSET, linePointer, paint);
            linePointer += 6;
            canvas.drawText(BODY_TEXT_PLACE, START_X_OFFSET_LINE, linePointer, paint);
            linePointer += 6;
            canvas.drawText(inputData.getPlace(), LINE_X_OFFSET, linePointer, paint);
            linePointer += 10;
        }
    }
}
