package com.lauchenauer.nextbusperth.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.lauchenauer.nextbusperth.R;

public class WidgetProvider_4x2 extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_4x2);
//            remoteView.setTextViewText(R.id.service_1, "Service 1 leaving soon");
//            remoteView.setTextViewText(R.id.service_2, "Service 1 leaving later");
            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }
    }
}
