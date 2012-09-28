package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.util.AttributeSet;
import com.lauchenauer.nextbusperth.R;

public class AboutActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference workStopNumberPref;
    private EditTextPreference homeStopNumberPref;
    private PreferenceScreen workRoutesScreenPref;
    private PreferenceScreen homeRoutesScreenPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.about);
        addPreferencesFromResource(R.xml.preferences);

        workStopNumberPref = (EditTextPreference)getPreferenceScreen().findPreference("Work-Stop");
        homeStopNumberPref = (EditTextPreference)getPreferenceScreen().findPreference("Home-Stop");
        homeRoutesScreenPref = (PreferenceScreen)getPreferenceScreen().findPreference("routes-home");
        workRoutesScreenPref = (PreferenceScreen)getPreferenceScreen().findPreference("routes-work");
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Work-Stop")) {
            workStopNumberPref.setSummary(sharedPreferences.getString("Work-Stop", "6 digit stop number"));

            CheckBoxPreference p = new CheckBoxPreference(this);
            p.setChecked(false);
            p.setKey("routeW-212");
            p.setTitle("212");
            p.setSummary("Mandurah Line To Perth Underground Stn");
            workRoutesScreenPref.addPreference(p);
        } else if (key.equals("Home-Stop")) {
            homeStopNumberPref.setSummary(sharedPreferences.getString("Home-Stop", "6 digit stop number"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
