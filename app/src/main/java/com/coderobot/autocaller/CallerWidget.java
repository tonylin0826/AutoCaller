package com.coderobot.autocaller;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;



/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CallerWidgetConfigureActivity CallerWidgetConfigureActivity}
 */
public class CallerWidget extends AppWidgetProvider {

    private final static String TAG = "CallerWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        CallerDB db = CallerDB.getInstance(context);
        ArrayList<PhoneSet> phoneSets = db.getAllPhoneNum();
        final int N = appWidgetIds.length;
        log("number of widget : " + N);
        for (int i = 0; i < N; i++) {
            log("id of widget : " + appWidgetIds[i]);
            PhoneSet phoneSet = getPhoneSetFromId(phoneSets, appWidgetIds[i]);
            updateAppWidget(context, appWidgetManager, appWidgetIds[i], phoneSet);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        CallerDB db = CallerDB.getInstance(context);
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            db.deleteId(appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, PhoneSet phoneSet) {

        log("updateAppWidget id : " + phoneSet.id + " phoneNum : " + phoneSet.phoneNum + " path : " + phoneSet.path);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.caller_widget);

        Intent intentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneSet.phoneNum));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentDial, 0);

        views.setOnClickPendingIntent(R.id.btn_dial, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PhoneSet getPhoneSetFromId(ArrayList<PhoneSet> phoneSets, int id) {
        for (PhoneSet phoneSet : phoneSets) {
            if (phoneSet.id == id)
                return phoneSet;
        }
        return new PhoneSet(0, "00000000", "none");
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}


