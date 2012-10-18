package com.lauchenauer.nextbusperth.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import com.lauchenauer.nextbusperth.R;

import java.util.ArrayList;
import java.util.List;

public class WidgetProvider_4x2 extends AppWidgetProvider {
    private static final String URI_SCHEME = "NextBusWidget";
    private static final int UPDATE_SECONDS = 5;

    private static int counter = 0;
    private static List<Integer> alarmList = new ArrayList<Integer>();

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("WIDGET", "updating");
        for (int appWidgetId : appWidgetIds) {
            Log.d("updating", "ID - " + appWidgetId);
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_4x2);
            remoteView.setTextViewText(R.id.s1_headsign, "s1 " + counter);
//            remoteView.setTextViewText(R.id.service_2, "Service 1 leaving later");
            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
            ComponentName cn = new ComponentName(context, WidgetProvider_4x2.class);
            appWidgetManager.updateAppWidget(cn, remoteView);

            if (!alarmList.contains(appWidgetId)) {
                startAlarmForWidget(context, appWidgetId);
            }
        }

        counter++;
    }

    private void startAlarmForWidget(Context context, int appWidgetId) {
        Log.d("WIget", "starting alarm for " + appWidgetId + " - " + Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)).getPath());
        Intent widgetUpdate = new Intent(context, WidgetProvider_4x2.class);
        widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});

        // make this pending intent unique
//        widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)));
        PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // schedule the new widget for updating
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), UPDATE_SECONDS * 1000, newPending);

        alarmList.add(appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Log.d("Widget", "DELETERD " + appWidgetId);
            Intent widgetUpdate = new Intent(context, WidgetProvider_4x2.class);
            widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)));
            PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarms.cancel(newPending);
        }

        super.onDeleted(context, appWidgetIds);
    }
}
