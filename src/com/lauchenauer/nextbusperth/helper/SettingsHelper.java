package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsHelper {
    private static final String FIRST_RUN = "firstRun";
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
