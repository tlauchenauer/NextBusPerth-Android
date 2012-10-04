package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;

public class SettingsActivity extends Activity {
    private SeekBar splitTime;
    private TextView splitTimeText;
    private SettingsHelper settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        splitTime = (SeekBar) findViewById(R.id.split_time);
        splitTimeText = (TextView) findViewById(R.id.split_time_text);

        settings = new SettingsHelper(getApplicationContext());

        readPreferences();
        setupUI();
    }

    private void setupUI() {
        splitTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                splitTimeText.setText(String.format("%d", i * 5 / 60) + ":" + String.format("%02d", (i * 5) % 60));
                settings.setSplitTime(i);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageButton work_search = (ImageButton) findViewById(R.id.work_search_btn);
        work_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivity(i);
            }
        });

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

    private void readPreferences() {
        int time = settings.getSplitTime();
        splitTime.setProgress(time);
        splitTimeText.setText(String.format("%d", time * 5 / 60) + ":" + String.format("%02d", (time * 5) % 60));
    }
}
