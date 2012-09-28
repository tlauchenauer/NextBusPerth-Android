package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import com.lauchenauer.nextbusperth.R;

public class AboutActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference workStopNumberPref;
    private EditTextPreference homeStopNumberPref;
    private DialogPreference splitTimePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.about);
        addPreferencesFromResource(R.xml.preferences);

        workStopNumberPref = (EditTextPreference)getPreferenceScreen().findPreference("Work-Stop");
        homeStopNumberPref = (EditTextPreference)getPreferenceScreen().findPreference("Home-Stop");
        splitTimePref = (DialogPreference)getPreferenceScreen().findPreference("SplitTime");
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateSummaries();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSummaries();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateSummaries() {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        workStopNumberPref.setSummary(prefs.getString("Work-Stop", "6 digit stop number"));
        homeStopNumberPref.setSummary(prefs.getString("Home-Stop", "6 digit stop number"));

        String splitTime = prefs.getInt("SplitTime.hour", 12) + ":" + String.format("%02d", prefs.getInt("SplitTime.minute", 0));
        splitTimePref.setSummary("Time when to switch from work to home journey (currently: " + splitTime + ")");
    }
}
