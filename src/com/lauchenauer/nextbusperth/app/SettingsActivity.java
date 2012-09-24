package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.lauchenauer.nextbusperth.DatabaseHelper;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.SettingsHandler;
import com.lauchenauer.nextbusperth.UrlHelper;

import java.util.Date;

public class SettingsActivity extends Activity {
    private Button action;
    private EditText workText;
    private EditText homeText;
    private SeekBar splitTime;
    private TextView splitTimeText;
    private SettingsHandler settings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);

        action = (Button) findViewById(R.id.action);
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

        action.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doSomeStuff();
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

    private void doSomeStuff() {
        Log.d("doSomeStuff", "do Some stuff NOW");

        String timetableJSON = UrlHelper.readTimeTable(workText.getText().toString(), new Date());

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.writeTimeTable(timetableJSON);
    }


}
