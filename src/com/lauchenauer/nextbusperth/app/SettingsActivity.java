package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;
import com.lauchenauer.nextbusperth.helper.TimetableHelper;

public class SettingsActivity extends Activity {
    private EditText workText;
    private EditText homeText;
    private SeekBar splitTime;
    private TextView splitTimeText;
    private SettingsHelper settings;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);

        workText = (EditText) findViewById(R.id.work_text);
        homeText = (EditText) findViewById(R.id.home_text);
        splitTime = (SeekBar) findViewById(R.id.split_time);
        splitTimeText = (TextView) findViewById(R.id.split_time_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        settings = new SettingsHelper(getApplicationContext());

        readPreferences();
        setupUI();
    }

    private void setupUI() {
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                settings.setWorkStopNumber(workText.getText().toString());
                settings.setHomeStopNumber(homeText.getText().toString());
            }
        };

        workText.addTextChangedListener(watcher);
        homeText.addTextChangedListener(watcher);

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

        Button action = (Button) findViewById(R.id.action);
        action.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                retrieveData();
            }
        });

        ImageButton work_search = (ImageButton) findViewById(R.id.work_search_btn);
        work_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivity(i);
            }
        });

        ImageButton home_search = (ImageButton) findViewById(R.id.home_search_btn);
        home_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivity(i);
            }
        });

        Button reset = (Button) findViewById(R.id.reset_data);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(SettingsActivity.this);
                db.clearDB();

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
        workText.setText(settings.getWorkStopNumber());
        homeText.setText(settings.getHomeStopNumber());

        int time = settings.getSplitTime();
        splitTime.setProgress(time);
        splitTimeText.setText(String.format("%d", time * 5 / 60) + ":" + String.format("%02d", (time * 5) % 60));
    }

    private void retrieveData() {
        progressBar.setVisibility(View.VISIBLE);
        new TimetableDownloadTask().execute(this);
    }

    private class TimetableDownloadTask extends AsyncTask<SettingsActivity, Integer, Long> {
        @Override
        protected Long doInBackground(SettingsActivity... settingsActivities) {
            TimetableHelper helper = new TimetableHelper(settingsActivities[0].getApplicationContext());
            helper.downloadTimeTable();

            return 1l;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            SettingsActivity.this.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
