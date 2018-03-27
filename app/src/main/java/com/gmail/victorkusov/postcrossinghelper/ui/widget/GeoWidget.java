package com.gmail.victorkusov.postcrossinghelper.ui.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.RemoteViews;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.database.PostalCodeProvider;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;

import java.util.Map;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

public class GeoWidget extends AppWidgetProvider {

    private static final String TAG = "Log " + GeoWidget.class.getSimpleName();
    private static final String ACTION_NEXT = "button_next";
    private static final String ACTION_PREVIOUS = "button_prev";
    private static final String ACTION_NONE = "";

    private static Map<Integer, Integer> mapIdPostition;
    private static Integer rowsMaxId;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Log.d(TAG, "onEnabled: ");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (mapIdPostition == null) {
            mapIdPostition = new ArrayMap<>();

            Cursor cursor = context.getContentResolver()
                    .query(PostalCodeProvider.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                rowsMaxId = cursor.getCount() - 1;
                cursor.close();
            }
        }
        for (int appWidgetId : appWidgetIds) {
            DistanceCode code = getNoteWithPosition(context, appWidgetId, 0, ACTION_NONE);
            updateWidget(context, appWidgetId, code);
        }
    }

    private void updateWidget(Context context, int widgetId, DistanceCode code) {
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        updateViews.setTextViewText(R.id.widget_postal_code, code.getPostalCode());
        updateViews.setTextViewText(R.id.widget_min_distance, String.valueOf(code.getDistance()));
        updateViews.setTextViewText(R.id.widget_place, code.getPlace());

        updateViews.setOnClickPendingIntent(R.id.widget_btn_next, getPendingIntent(context, ACTION_NEXT, widgetId));
        updateViews.setOnClickPendingIntent(R.id.widget_btn_prev, getPendingIntent(context, ACTION_PREVIOUS, widgetId));
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(widgetId, updateViews);
    }

    private PendingIntent getPendingIntent(Context context, String someAction, int widgetId) {
        Intent intent = new Intent(context, GeoWidget.class);
        intent.putExtra(EXTRA_APPWIDGET_ID, widgetId);
        intent.setAction(someAction);
        return PendingIntent.getBroadcast(context, widgetId, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
        if (mapIdPostition != null) {
            Integer position = mapIdPostition.get(widgetId);
            String action = intent.getAction();
            if (action != null && position != null) {
                DistanceCode code = getNoteWithPosition(context, widgetId, position, action);
                updateWidget(context, widgetId, code);
            }
        }
    }


    private DistanceCode getNoteWithPosition(Context context, int widgetId, int position, String action) {
        Log.d(TAG, "onReceive: id:" + widgetId + " position:" + position);

        switch (action) {
            case ACTION_PREVIOUS: {
                if (position > 0) {
                    position--;
                }
                break;
            }
            case ACTION_NEXT: {
                if (position < rowsMaxId) {
                    position++;
                }
                break;
            }
            default:{
                position = 0;
            }
        }

        Uri uri = ContentUris.withAppendedId(PostalCodeProvider.CONTENT_URI, position);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        DistanceCode code = null;
        if (cursor != null && cursor.moveToFirst()) {
            code = new DistanceCode();

            code.setId(cursor.getInt(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_ID)));
            code.setDistance(cursor.getDouble(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_DISTANCE)));
            code.setRegion(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_REGION)));
            code.setPlace(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_PLACE)));
            code.setPostalCode(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_POSTAL_CODE)));
            code.setCountryCode(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_COUNTRY_CODE)));
            code.setLatitude(cursor.getDouble(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_LAT)));
            code.setLongitude(cursor.getDouble(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_LNG)));

            cursor.close();
        }

        mapIdPostition.put(widgetId, position);
        return code;
    }
}

