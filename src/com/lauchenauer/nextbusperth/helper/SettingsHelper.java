package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.lauchenauer.nextbusperth.app.NextBusApplication;

public class SettingsHelper {
    private static final String FIRST_RUN = "firstRun";
    public static final String WORK_STOP_SETTING = "Work-Stop";
    public static final String HOME_STOP_SETTING = "Home-Stop";
    public static final String SPLIT_TIME_SETTING_HOUR = "SplitTime.hour";
    public static final String SPLIT_TIME_SETTING_MINUTE = "SplitTime.minute";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SettingsHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    public int getSplitTime() {
        int hour = prefs.getInt(SPLIT_TIME_SETTING_HOUR, 12);
        int minute = prefs.getInt(SPLIT_TIME_SETTING_MINUTE, 0);

        return hour * 60 + minute;
    }

    public void setJourneyStopNumber(NextBusApplication.JourneyType journeyType, String stopNumber) {
        switch (journeyType) {
            case work:
                putString(WORK_STOP_SETTING, stopNumber);
                break;
            case home:
                putString(HOME_STOP_SETTING, stopNumber);
                break;
        }
    }

    private void putString(String name, String value) {
        Log.d("[SettingsHelper] - putString", name + " = " + value);

        editor.putString(name, value);
        editor.commit();
    }

    private void putBoolean(String name, boolean value) {
        Log.d("[SettingsHelper] - putBoolean", name + " = " + value);

        editor.putBoolean(name, value);
        editor.commit();
    }

    public boolean isFirstRun() {
        boolean firstRun = prefs.getBoolean(FIRST_RUN, true);
        putBoolean(FIRST_RUN, false);

        return firstRun;
    }
}
