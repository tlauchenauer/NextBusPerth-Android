package com.lauchenauer.nextbusperth.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.lauchenauer.nextbusperth.R;

public class WidgetConfiguration extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get any data we were launched with
        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Intent cancelResultValue = new Intent();
            cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_CANCELED, cancelResultValue);
        } else {
            // only launch if it's for configuration
            // Note: when you launch for debugging, this does prevent this
            // activity from running. We could also turn off the intent
            // filtering for main activity.
            // But, to debug this activity, we can also just comment the
            // following line out.
            finish();
        }

        setContentView(R.layout.widget_configuration);

        Button saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

                    // tell the app widget_provider_4x2 manager that we're now configured
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                }

                finish();
            }
        });
    }
}
