package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingsHelper {
    private static final String PREFERENCE_FILENAME = "NextBusPerth_Preferences";
    private static final String FIRST_RUN = "firstRun";
    public static final String WORK_STOP_SETTING = "Work-Stop";
    public static final String HOME_STOP_SETTING = "Home-Stop";
    public static final String SPLIT_TIME_SETTING = "SplitTime";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SettingsHelper(Context context) {
        prefs = context.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setSplitTime(int splitTime) {
        putInt(SPLIT_TIME_SETTING, splitTime);
    }

    public int getSplitTime() {
        return prefs.getInt(SPLIT_TIME_SETTING, 0);
    }

    private void putString(String name, String value) {
        Log.d("[SettingsHelper] - putString", name + " = " + value);

        editor.putString(name, value);
        editor.commit();
    }

    private void putInt(String name, int value) {
        Log.d("[SettingsHelper] - putInt", name + " = " + value);

        editor.putInt(name, value);
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
