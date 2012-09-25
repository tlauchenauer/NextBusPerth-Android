package com.lauchenauer.nextbusperth.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import com.lauchenauer.nextbusperth.DatabaseHelper;
import com.lauchenauer.nextbusperth.SettingsHandler;
import com.lauchenauer.nextbusperth.model.Service;

import java.util.ArrayList;
import java.util.List;

public class NextBusActivity extends ListActivity {
    private SettingsHandler settingsHandler;
    private DatabaseHelper dbHelper;
    private RowAdapter adapter;

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

        adapter = new RowAdapter(this, new ArrayList<Service>());
        setListAdapter(adapter);

        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateData();
        Log.d("[NextBus - MAIN]", "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Settings").setIcon(android.R.drawable.ic_menu_preferences).setIntent(new Intent(this, SettingsActivity.class));
        menu.add("About").setIcon(android.R.drawable.ic_menu_info_details).setIntent(new Intent(this, AboutActivity.class));

        return true;
    }

    private void updateData() {
        long splitTime = settingsHandler.getSplitTime();
        Time t = new Time();
        t.setToNow();
        long currentTime = t.hour * 12 + t.minute / 5;

        String stopNumber = settingsHandler.getWorkStopNumber();
        if (currentTime > splitTime) {
            stopNumber = settingsHandler.getHomeStopNumber();
        }

        List<Service> services = dbHelper.getNextBuses(stopNumber, 5);
        adapter.setServices(services);
    }
}