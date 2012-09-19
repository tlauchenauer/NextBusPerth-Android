package com.lauchenauer.nextbusperth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingsHandler {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SettingsHandler(Context context) {
        prefs = context.getSharedPreferences(Constants.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void putString(String name, String value) {
        Log.d("[SettingsHandler] - putString", name + " = " + value);

        editor.putString(name, value);
        editor.commit();
    }

    public void putInt(String name, int value) {
        Log.d("[SettingsHandler] - putInt", name + " = " + value);

        editor.putInt(name, value);
        editor.commit();
    }

    public void putBoolean(String name, boolean value) {
        Log.d("[SettingsHandler] - putBoolean", name + " = " + value);

        editor.putBoolean(name, value);
        editor.commit();
    }

    public String getString(String name) {
        return prefs.getString(name, "");
    }

    public int getInt(String name) {
        return prefs.getInt(name, 0);
    }

    public boolean isFirstRun() {
        boolean firstRun = prefs.getBoolean("firstRun", true);
        putBoolean("firstRun", false);

        return firstRun;
    }
}
