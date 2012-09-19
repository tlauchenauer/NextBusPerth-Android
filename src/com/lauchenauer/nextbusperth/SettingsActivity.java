package com.lauchenauer.nextbusperth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity extends Activity {
    private Button action;
    private TimePicker splitTime;
    private EditText workText;
    private EditText homeText;
    private SQLiteDatabase database;
    private SettingsHandler settings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        action = (Button) findViewById(R.id.action);
        splitTime = (TimePicker) findViewById(R.id.split_time);
        workText = (EditText) findViewById(R.id.work_text);
        homeText = (EditText) findViewById(R.id.home_text);

        settings = new SettingsHandler(getApplicationContext());

        readPreferences();
        setupUI();
    }

    private void setupUI() {
        splitTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                settings.putInt(Constants.SPLIT_TIME_HOUR_SETTING, splitTime.getCurrentHour());
                settings.putInt(Constants.SPLIT_TIME_MINUTE_SETTING, splitTime.getCurrentMinute());
            }
        });

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                settings.putString(Constants.WORK_STOP_SETTING, workText.getText().toString());
                settings.putString(Constants.HOME_STOP_SETTING, homeText.getText().toString());
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

    private void readPreferences() {
        workText.setText(settings.getString(Constants.WORK_STOP_SETTING));
        homeText.setText(settings.getString(Constants.HOME_STOP_SETTING));
        splitTime.setCurrentHour(settings.getInt(Constants.SPLIT_TIME_HOUR_SETTING));
        splitTime.setCurrentMinute(settings.getInt(Constants.SPLIT_TIME_MINUTE_SETTING));
    }

    private void doSomeStuff() {
        Log.d("doSomeStuff", "do Some stuff NOW");

        getDatabase();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(new Date());

        Log.d("today", today);

        String timetableJSON = getInputStreamFromUrl(Constants.BASE_TIMETABLE_URL + workText.getText() + "/" + today);
        Log.d("JSON from site", timetableJSON);

        try {
            JSONObject json = new JSONObject(timetableJSON);
            JSONArray array = json.getJSONArray(today);
//            for (int i = 0; i < array.length(); i++) {
                JSONObject entry = array.getJSONObject(0);
                StopTime time = new StopTime(entry);

//            }
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
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            /* Convert the Bytes read to a String. */
            return new String(baf.toByteArray());
        } catch (Exception e) {
            Log.d("[GET REQUEST]", "Network exception", e);
        }

        return "";
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = openOrCreateDatabase(Constants.TIMETABLE_DB, SQLiteDatabase.CREATE_IF_NECESSARY, null);

            database.execSQL("DROP TABLE IF EXISTS tbl_stops");
            database.execSQL("CREATE TABLE IF NOT EXISTS tbl_stops (id INTEGER PRIMARY KEY AUTOINCREMENT, stop_id STRING, name TEXT, short_name TEXT, long_name TEXT, headsign TEXT, departure DATETIME)");
            database.execSQL("CREATE INDEX IF NOT EXISTS departure_idx ON tbl_stops(departure)");
            database.execSQL("CREATE INDEX IF NOT EXISTS stop_idx ON tbl_stops(stop_id)");
        }

        return database;
    }
}
