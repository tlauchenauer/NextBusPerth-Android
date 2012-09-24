package com.lauchenauer.nextbusperth.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import com.lauchenauer.nextbusperth.DatabaseHelper;
import com.lauchenauer.nextbusperth.SettingsHandler;
import com.lauchenauer.nextbusperth.model.Service;

import java.util.List;

public class NextBusActivity extends ListActivity {
    private SettingsHandler settingsHandler;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("[NextBus - MAIN]", "onCreate");

        settingsHandler = new SettingsHandler(getApplicationContext());

//        if (settingsHandler.isFirstRun()) {
//            Log.d("[NextBusActivity]", "firstRun - starting Alarm");
//            OnBootReceiver.startTimeTableAlarm(getApplicationContext());
//        }

        settingsHandler = new SettingsHandler(getApplicationContext());
        dbHelper = new DatabaseHelper(getApplicationContext());

        List<Service> services = dbHelper.getNextBuses(settingsHandler.getWorkStopNumber(), 5);

        RowAdapter adapter = new RowAdapter(this, services);
        setListAdapter(adapter);
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
