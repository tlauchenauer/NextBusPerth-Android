package com.lauchenauer.nextbusperth.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.Service;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.lauchenauer.nextbusperth.app.NextBusFragment.TIME_FORMAT;

public class WidgetProvider_4x2 extends AppWidgetProvider {
    private static final int UPDATE_SECONDS = 30;
    private static final Service EMPTY_SERVICE = new Service("", "", "", "", "", null);
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

            Journey journey = DatabaseHelper.findCurrentDefaultJourney();
            remoteView.setTextViewText(R.id.journey_name, journey == null ? "" : journey.getName());
            remoteView.setTextViewText(R.id.last_update, "updated at " + TIME_FORMAT.format(new Date()));

            List<Service> services = getCurrentServices(journey);
            setService1Settings(remoteView, services.get(0));
            setService2Settings(remoteView, services.get(1));

            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
//            ComponentName cn = new ComponentName(context, WidgetProvider_4x2.class);
//            appWidgetManager.updateAppWidget(cn, remoteView);

            if (!alarmList.contains(appWidgetId)) {
                startAlarmForWidget(context, appWidgetId);
            }
        }
    }

    private List<Service> getCurrentServices(Journey journey) {
        List<Service> services = new ArrayList<Service>(2);
        if (journey != null) {
            services = DatabaseHelper.getNextBuses(journey, 2);
        }
        while (services.size() < 2) {
            services.add(EMPTY_SERVICE);
        }

        return services;
    }

    private void setService1Settings(RemoteViews remoteView, Service s) {
        remoteView.setTextViewText(R.id.s1_headsign, s.getHeadsign());
        remoteView.setTextViewText(R.id.s1_route_number, s.getRouteNumber());
        remoteView.setTextViewText(R.id.s1_stop_name, s.getStopName());
        remoteView.setTextViewText(R.id.s1_time_delta, s.getTimeDelta());
        if (s.getDepartureTime() == null) {
            remoteView.setTextViewText(R.id.s1_departure_time, "");
        } else {
            remoteView.setTextViewText(R.id.s1_departure_time, TIME_FORMAT.format(s.getDepartureTime()));
        }

        if (s.getTimeDelta().equals("Now") || s.getTimeDelta().equals("\u221e")) {
            remoteView.setViewVisibility(R.id.s1_mins, GONE);
            remoteView.setViewVisibility(R.id.s1_time_delta, GONE);
            remoteView.setViewVisibility(R.id.s1_time_center, VISIBLE);
            remoteView.setTextViewText(R.id.s1_time_center, s.getTimeDelta());
        } else {
            remoteView.setViewVisibility(R.id.s1_mins, VISIBLE);
            remoteView.setViewVisibility(R.id.s1_time_delta, VISIBLE);
            remoteView.setViewVisibility(R.id.s1_time_center, GONE);
        }
    }

    private void setService2Settings(RemoteViews remoteView, Service s) {
        remoteView.setTextViewText(R.id.s2_headsign, s.getHeadsign());
        remoteView.setTextViewText(R.id.s2_route_number, s.getRouteNumber());
        remoteView.setTextViewText(R.id.s2_stop_name, s.getStopName());
        remoteView.setTextViewText(R.id.s2_time_delta, s.getTimeDelta());
        if (s.getDepartureTime() == null) {
            remoteView.setTextViewText(R.id.s2_departure_time, "");
        } else {
            remoteView.setTextViewText(R.id.s2_departure_time, TIME_FORMAT.format(s.getDepartureTime()));
        }

        if (s.getTimeDelta().equals("Now") || s.getTimeDelta().equals("\u221e")) {
            remoteView.setViewVisibility(R.id.s2_mins, GONE);
            remoteView.setViewVisibility(R.id.s2_time_delta, GONE);
            remoteView.setViewVisibility(R.id.s2_time_center, VISIBLE);
            remoteView.setTextViewText(R.id.s2_time_center, s.getTimeDelta());
        } else {
            remoteView.setViewVisibility(R.id.s2_mins, VISIBLE);
            remoteView.setViewVisibility(R.id.s2_time_delta, VISIBLE);
            remoteView.setViewVisibility(R.id.s2_time_center, GONE);
        }
    }

    private void startAlarmForWidget(Context context, int appWidgetId) {
        Intent widgetUpdate = new Intent(context, WidgetProvider_4x2.class);
        widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});

        PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), UPDATE_SECONDS * 1000, newPending);

        alarmList.add(appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent widgetUpdate = new Intent(context, WidgetProvider_4x2.class);
            widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarms.cancel(newPending);
        }

        super.onDeleted(context, appWidgetIds);
    }
}
