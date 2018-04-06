package com.gmail.victorkusov.postcrossinghelper.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;

import java.util.ArrayList;
import java.util.List;

public class PostalCodeProvider extends ContentProvider {

    private static final String TAG = PostalCodeProvider.class.getSimpleName();


    private static final String AUTHORITY = "com.gmail.victorkusov.postcrossinghelper";
    public static final String PATH = "Places";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + PATH;

    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + PATH;

    private static final int MATCHER_POSTAL_CODES = 1;
    private static final int MATCHER_POSTAL_CODE_ITEM_ID = 2;

    private static final UriMatcher MATCHER;

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY, PATH, MATCHER_POSTAL_CODES);
        MATCHER.addURI(AUTHORITY, PATH + "/#", MATCHER_POSTAL_CODE_ITEM_ID);
    }


    private static final String DB_NAME = "NearbyPlaces";
    private static final int DB_VERSION = 3;

    private static final String TABLE_NAME = "Places";

    private static final String TYPE_DOUBLE = "REAL";
    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_TEXT = "CONNECTION_LOST_MESSAGE";

    public static final String TABLE_PLACES_FIELD_ID = "id";
    public static final String TABLE_PLACES_FIELD_LNG = "longitude";
    public static final String TABLE_PLACES_FIELD_LAT = "latitude";
    public static final String TABLE_PLACES_FIELD_DISTANCE = "distance";
    public static final String TABLE_PLACES_FIELD_COUNTRY_CODE = "countryCode";
    public static final String TABLE_PLACES_FIELD_POSTAL_CODE = "postalCode";
    public static final String TABLE_PLACES_FIELD_PLACE = "place";
    public static final String TABLE_PLACES_FIELD_REGION = "region";

    private static final java.lang.String CREATION = "CREATE TABLE " + TABLE_NAME + " (" +
            TABLE_PLACES_FIELD_ID + " " + TYPE_INTEGER + " PRIMARY KEY AUTOINCREMENT," +
            TABLE_PLACES_FIELD_LNG + " " + TYPE_DOUBLE + ", " +
            TABLE_PLACES_FIELD_LAT + " " + TYPE_DOUBLE + ", " +
            TABLE_PLACES_FIELD_DISTANCE + " " + TYPE_DOUBLE + ", " +
            TABLE_PLACES_FIELD_COUNTRY_CODE + " " + TYPE_TEXT + ", " +
            TABLE_PLACES_FIELD_POSTAL_CODE + " " + TYPE_TEXT + ", " +
            TABLE_PLACES_FIELD_PLACE + " " + TYPE_TEXT + ", " +
            TABLE_PLACES_FIELD_REGION + " " + TYPE_TEXT + ")";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    PostalDBHelper helper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        helper = new PostalDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String select = getSelection(uri);
        db = helper.getReadableDatabase();

        return db.query(TABLE_NAME, projection, select, selectionArgs, null, null, sortOrder);
    }

    private String getSelection(Uri uri) {
        switch (MATCHER.match(uri)) {
            case MATCHER_POSTAL_CODES: {
                return null;
            }
            case MATCHER_POSTAL_CODE_ITEM_ID: {
                return TABLE_PLACES_FIELD_ID + "=" + uri.getLastPathSegment();
            }
            default: {
                throw new IllegalArgumentException("Wrong uri:" + uri + "\n" + MATCHER.match(uri) + " instead: " + MATCHER_POSTAL_CODES + " or " + MATCHER_POSTAL_CODE_ITEM_ID);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case MATCHER_POSTAL_CODES:
                return CONTENT_DIR_TYPE;
            case MATCHER_POSTAL_CODE_ITEM_ID:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (MATCHER.match(uri) != MATCHER_POSTAL_CODES) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = helper.getWritableDatabase();
        Long rowId = db.insert(TABLE_NAME, null, values);
        uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
        //Log.d(TAG, "insert: rowId:" + rowId);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String select = getSelection(uri);

        db = helper.getWritableDatabase();
        return db.delete(TABLE_NAME, select, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String select = getSelection(uri);

        db = helper.getWritableDatabase();
        db.update(TABLE_NAME, values, select, selectionArgs);
        return 0;
    }

    public static List<DistanceCode> getDisplayedData(Context context) {
        Cursor cursor = context.getContentResolver().query(CONTENT_URI,
                null, null, null, null);

        List<DistanceCode> codeList = null;

        if (cursor != null) {
            codeList = new ArrayList<>();
            try {

                while (cursor.moveToNext()) {
                    DistanceCode code = new DistanceCode();

                    code.setId(cursor.getInt(cursor.getColumnIndex(TABLE_PLACES_FIELD_ID)));
                    code.setLongitude(cursor.getDouble(cursor.getColumnIndex(TABLE_PLACES_FIELD_LNG)));
                    code.setLatitude(cursor.getDouble(cursor.getColumnIndex(TABLE_PLACES_FIELD_LAT)));
                    code.setDistance(cursor.getDouble(cursor.getColumnIndex(TABLE_PLACES_FIELD_DISTANCE)));
                    code.setCountryCode(cursor.getString(cursor.getColumnIndex(TABLE_PLACES_FIELD_COUNTRY_CODE)));
                    code.setPostalCode(cursor.getString(cursor.getColumnIndex(TABLE_PLACES_FIELD_POSTAL_CODE)));
                    code.setPlace(cursor.getString(cursor.getColumnIndex(TABLE_PLACES_FIELD_PLACE)));
                    code.setRegion(cursor.getString(cursor.getColumnIndex(TABLE_PLACES_FIELD_REGION)));

                    Log.d(TAG, "getDisplayedData: id:" + cursor.getColumnIndex(TABLE_PLACES_FIELD_ID));

                    codeList.add(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        return codeList;
    }


    private class PostalDBHelper extends SQLiteOpenHelper {


        private PostalDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }

}
