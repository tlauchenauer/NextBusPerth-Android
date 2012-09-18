package com.lauchenauer.nextbusperth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity extends Activity {
    public static final String PREFERENCE_FILENAME = "NextBusPerth_Preferences";
    public static final String WORK_STOP_SETTING = "Work-Stop";
    public static final String HOME_STOP_SETTING = "Home-Stop";
    public static final String SPLIT_TIME_HOUR_SETTING = "SplitTime-Hour";
    public static final String SPLIT_TIME_MINUTE_SETTING = "SplitTime-Minute";
    public static final String BASE_TIMETABLE_URL = "http://perth-timetable.herokuapp.com/time_table/";

    private Button action;
    private TimePicker splitTime;
    private EditText workText;
    private EditText homeText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        action = (Button)findViewById(R.id.action);
        splitTime = (TimePicker)findViewById(R.id.split_time);
        workText = (EditText)findViewById(R.id.work_text);
        homeText = (EditText)findViewById(R.id.home_text);

        readPreferences();
        setupUI();
    }

    private void setupUI() {
        splitTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                Log.d("Split Time", "onTimeChanged");
                saveSettings();
            }
        });

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                Log.d("Text Changed", "afterTextChanged");
                saveSettings();
            }
        };

        workText.addTextChangedListener(watcher);
        homeText.addTextChangedListener(watcher);

        action.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doSomeStuff();
            }
        });
    }

    private void saveSettings() {
        Log.d("Save Settings", "saveSettings " + workText.getText() + " - " + homeText.getText() + " - " + splitTime.getCurrentHour() + ":" + splitTime.getCurrentMinute());
        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(WORK_STOP_SETTING, workText.getText().toString());
        editor.putString(HOME_STOP_SETTING, homeText.getText().toString());
        editor.putInt(SPLIT_TIME_HOUR_SETTING, splitTime.getCurrentHour());
        editor.putInt(SPLIT_TIME_MINUTE_SETTING, splitTime.getCurrentMinute());

        editor.commit();
    }

    private void readPreferences() {
        Log.d("Read Settings", "readPreferences");
        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);

        workText.setText(settings.getString(WORK_STOP_SETTING, ""));
        homeText.setText(settings.getString(HOME_STOP_SETTING, ""));
        splitTime.setCurrentHour(settings.getInt(SPLIT_TIME_HOUR_SETTING, 17));
        splitTime.setCurrentMinute(settings.getInt(SPLIT_TIME_MINUTE_SETTING, 0));
    }

    private void doSomeStuff() {
        Log.d("doSomeStuff", "do Some stuff NOW");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(new Date());

        Log.d("today", today);

        String timetableJSON = getInputStreamFromUrl(BASE_TIMETABLE_URL + workText.getText() + "/" + today);
        Log.d("JSON from site", timetableJSON);

        try {
            JSONObject json = new JSONObject(timetableJSON);
            JSONArray array = json.getJSONArray(today);
            JSONObject entry = array.getJSONObject(0);
            Log.d("[Timetable Entry]", "stop_id - " + entry.getString("stop_id"));
            Log.d("[Timetable Entry]", "name - " + entry.getString("name"));
            Log.d("[Timetable Entry]", "short_name - " + entry.getString("short_name"));
            Log.d("[Timetable Entry]", "long_name - " + entry.getString("long_name"));
            Log.d("[Timetable Entry]", "headsign - " + entry.getString("headsign"));
            Log.d("[Timetable Entry]", "departure_time - " + entry.getString("departure_time"));
            Log.d("[Timetable Entry]", "departure - " + entry.getString("departure"));
        } catch (JSONException e) {
            Log.e("[JSON]", e.getMessage(), e);
        }

    }

    public static String getInputStreamFromUrl(String url) {
      InputStream content = null;
      try {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(new HttpGet(url));
        content = response.getEntity().getContent();

          BufferedInputStream bis = new BufferedInputStream(content);
          ByteArrayBuffer baf = new ByteArrayBuffer(50);

          int current = 0;
          while((current = bis.read()) != -1){
              baf.append((byte)current);
          }

          /* Convert the Bytes read to a String. */
          return new String(baf.toByteArray());
      } catch (Exception e) {
        Log.d("[GET REQUEST]", "Network exception", e);
      }

      return "";
    }
}
