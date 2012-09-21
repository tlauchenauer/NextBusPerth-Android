package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import com.lauchenauer.nextbusperth.DatabaseHelper;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.SettingsHandler;
import com.lauchenauer.nextbusperth.service.OnBootReceiver;

public class NextBusActivity extends Activity {
    private SettingsHandler settingsHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextbus);

        Log.d("[NextBus - MAIN]", "onCreate");

        settingsHandler = new SettingsHandler(getApplicationContext());

        if (settingsHandler.isFirstRun()) {
            Log.d("[NextBusActivity]", "firstRun - starting Alarm");
            OnBootReceiver.startTimeTableAlarm(getApplicationContext());
        }

        SettingsHandler prefs = new SettingsHandler(getApplicationContext());
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        helper.getNextBuses(prefs.getWorkStopNumber(), 10);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("[NextBus - MAIN]", "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Settings").setIcon(android.R.drawable.ic_menu_preferences).setIntent(new Intent(this, SettingsActivity.class));
        menu.add("About").setIcon(android.R.drawable.ic_menu_info_details).setIntent(new Intent(this, AboutActivity.class));

        return true;
    }
}
