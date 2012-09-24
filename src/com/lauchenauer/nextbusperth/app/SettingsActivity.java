package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.SettingsHandler;
import com.lauchenauer.nextbusperth.TimetableHelper;

public class SettingsActivity extends Activity {
    private EditText workText;
    private EditText homeText;
    private SeekBar splitTime;
    private TextView splitTimeText;
    private SettingsHandler settings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);

        workText = (EditText) findViewById(R.id.work_text);
        homeText = (EditText) findViewById(R.id.home_text);
        splitTime = (SeekBar) findViewById(R.id.split_time);
        splitTimeText = (TextView) findViewById(R.id.split_time_text);

        settings = new SettingsHandler(getApplicationContext());

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
                retrieveTimetables();
            }
        });

        ImageButton search = (ImageButton) findViewById(R.id.work_search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivity(i);
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

    private void retrieveTimetables() {
        TimetableHelper helper = new TimetableHelper(getApplicationContext());
        helper.downloadTimeTable();
    }


}
