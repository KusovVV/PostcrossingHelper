package com.gmail.victorkusov.postcrossinghelper.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostalDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "Log " + PostalDBHelper.class.getSimpleName();

    private static final String DB_NAME = "NearbyPlaces";
    private static final String TABLE_NAME = "Places";

    private static final int DB_VERSION = 1;

    private static final String TYPE_DOUBLE = "REAL";
    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_TEXT = "TEXT";

    private static final String FIELD_ID = "id";
    private static final String FIELD_LNG = "longitude";
    private static final String FIELD_LAT = "latitude";
    private static final String FIELD_DISTANCE = "distance";
    private static final String FIELD_COUNTRY_CODE = "countryCode";
    private static final String FIELD_POSTAL_CODE = "postalCode";
    private static final String FIELD_PLACE = "place";
    private static final String FIELD_REGION = "region";

    private static final String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME;


    private static final java.lang.String CREATION = "CREATE TABLE " + TABLE_NAME + " (" +
            FIELD_ID + " " + TYPE_INTEGER + " PRIMARY KEY," +
            FIELD_LNG + " " + TYPE_DOUBLE + ", " +
            FIELD_LAT + " " + TYPE_DOUBLE + ", " +
            FIELD_DISTANCE + " " + TYPE_DOUBLE + ", " +
            FIELD_COUNTRY_CODE + " " + TYPE_TEXT + ", " +
            FIELD_POSTAL_CODE + " " + TYPE_TEXT + ", " +
            FIELD_PLACE + " " + TYPE_TEXT + ", " +
            FIELD_REGION + " " + TYPE_TEXT + ")";

    public PostalDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION);
        Log.d(TAG, "onCreate: " + CREATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(List<DistanceCode> codeList) {
        Log.d(TAG, "insert: insert values");

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        for (DistanceCode code : codeList) {
            ContentValues value = new ContentValues();

            value.put(FIELD_ID, UUID.randomUUID().getLeastSignificantBits());
            value.put(FIELD_LNG, code.getLongitude());
            value.put(FIELD_LAT, code.getLatitude());
            value.put(FIELD_DISTANCE, code.getDistance());
            value.put(FIELD_COUNTRY_CODE, code.getCountryCode());
            value.put(FIELD_POSTAL_CODE, code.getPostalCode());
            value.put(FIELD_PLACE, code.getPlace());
            value.put(FIELD_REGION, code.getRegion());

            db.insert(TABLE_NAME, null, value);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void eraseTable() {
        Log.d(TAG, "eraseTable: erasing table " + TABLE_NAME);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public List<DistanceCode> query() {
        Log.d(TAG, "query: read from DB");
        SQLiteDatabase db = getReadableDatabase();

        List<DistanceCode> codeList = new ArrayList<>();

        Cursor values = db.rawQuery(SELECT_QUERY, null);
        if (values.moveToFirst()) {

            do {
                DistanceCode code = new DistanceCode();

                code.setLongitude(values.getDouble(values.getColumnIndex(FIELD_LNG)));
                code.setLatitude(values.getDouble(values.getColumnIndex(FIELD_LAT)));
                code.setDistance(values.getDouble(values.getColumnIndex(FIELD_DISTANCE)));
                code.setCountryCode(values.getString(values.getColumnIndex(FIELD_COUNTRY_CODE)));
                code.setPostalCode(values.getString(values.getColumnIndex(FIELD_POSTAL_CODE)));
                code.setPlace(values.getString(values.getColumnIndex(FIELD_PLACE)));
                code.setRegion(values.getString(values.getColumnIndex(FIELD_REGION)));

                codeList.add(code);
            } while (values.moveToNext());
        }
        values.close();

        db.close();
        return codeList;
    }

}
