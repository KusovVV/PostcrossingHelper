package com.gmail.victorkusov.postcrossinghelper.ui.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.RemoteViews;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.database.PostalCodeProvider;
import com.gmail.victorkusov.postcrossinghelper.model.DistanceCode;
import com.gmail.victorkusov.postcrossinghelper.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

public class GeoWidget extends AppWidgetProvider {

    private static final String TAG = "Log " + GeoWidget.class.getSimpleName();
    private static final String ACTION_NEXT = "button_next";
    private static final String ACTION_PREVIOUS = "button_prev";
    private static final String ACTION_NONE = "";

    private static Map<Integer, Integer> mapIdPostition;
    private static List<DistanceCode> mDistanceList;
    private static int rowsMaxId;

    private ContentObserver mObserver;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled: ");
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if(mObserver == null){
            mObserver = new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    Log.d(TAG, "onChange: data widget for " + appWidgetIds[0]);
                    setupWidgets(context, appWidgetIds);
                }
            };
            context.getContentResolver().registerContentObserver(PostalCodeProvider.CONTENT_URI, false, mObserver);
        }
        Utils.startGeoService(context);

        setupWidgets(context, appWidgetIds);
    }

    private void setupWidgets(Context context,  int[] appWidgetIds) {
        if (mapIdPostition == null) {
            mapIdPostition = new ArrayMap<>();
        }
            makeDistanceList(context);


        for (int appWidgetId : appWidgetIds) {
            updateWidgets(context, appWidgetId, getDataWithPosition(appWidgetId,0,ACTION_NONE));
        }
    }

    private void makeDistanceList(Context context) {
        if(mDistanceList == null){
            mDistanceList = new ArrayList<>();
        } else {
            mDistanceList.clear();
        }

        Cursor cursor = context.getContentResolver()
                .query(PostalCodeProvider.CONTENT_URI, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            do {
                DistanceCode code = new DistanceCode();

                code.setId(cursor.getInt(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_ID)));
                code.setDistance(cursor.getDouble(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_DISTANCE)));
                code.setRegion(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_REGION)));
                code.setPlace(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_PLACE)));
                code.setPostalCode(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_POSTAL_CODE)));
                code.setCountryCode(cursor.getString(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_COUNTRY_CODE)));
                code.setLatitude(cursor.getDouble(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_LAT)));
                code.setLongitude(cursor.getDouble(cursor.getColumnIndex(PostalCodeProvider.TABLE_PLACES_FIELD_LNG)));

                mDistanceList.add(code);
            } while (cursor.moveToNext());
            rowsMaxId = mDistanceList.size() - 1;
            cursor.close();
        }
    }

    private void updateWidgets(Context context, int widgetId, DistanceCode code) {
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
        String action = intent.getAction();
        if (action != null) {
                int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
                if (mapIdPostition != null) {
                    Integer position = mapIdPostition.get(widgetId);
                    if (position != null) {
                        updateWidgets(context, widgetId, getDataWithPosition(widgetId, position, action));
                    }
                }
            }
    }

    private DistanceCode getDataWithPosition(int widgetId, int position, String action) {
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
        mapIdPostition.put(widgetId, position);
        return mDistanceList.get(position);
    }

}

