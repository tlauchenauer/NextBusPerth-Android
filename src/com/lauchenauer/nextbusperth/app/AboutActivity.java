package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;

public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        setupUI();
    }

    private void setupUI() {
        Button reset = (Button) findViewById(R.id.reset_data);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DaoSession daoSession = NextBusApplication.getApp().getDaoSession();
                daoSession.getJourneyDao().deleteAll();
                daoSession.getJourneyRouteDao().deleteAll();
                daoSession.getStopTimeDao().deleteAll();
                daoSession.getRouteDao().deleteAll();
                daoSession.getStopDao().deleteAll();
                daoSession.clear();
            }
        });

        Button resetPrefs = (Button) findViewById(R.id.reset_prefs);
        resetPrefs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.clear();
                editor.commit();
            }
        });
    }
}
