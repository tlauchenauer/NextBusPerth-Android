package com.lauchenauer.nextbusperth.app.prefs;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.ArrayList;
import java.util.List;

public class SelectAllListPreference extends ListPreference implements Preference.OnPreferenceChangeListener {
    private static final String ALL = "all";
    private static final String NONE = "none";
    private List<CheckBoxPreference> trackedPreferences;

    public SelectAllListPreference(Context context) {
        super(context);
        setTitle("Select All/None");
        setEntries(new String[]{"select all", "select none"});
        setEntryValues(new String[]{ALL, NONE});
        trackedPreferences = new ArrayList<CheckBoxPreference>();
        setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object o) {
        boolean setting = true;
        if (o.toString().equals(NONE)) {
            setting = false;
        } else {
            return true; // selection was cancelled
        }

        for (CheckBoxPreference cbp : trackedPreferences) {
            cbp.setChecked(setting);
            cbp.getOnPreferenceChangeListener().onPreferenceChange(cbp, setting);
        }

        return true;
    }

    public void addTrackedPreference(CheckBoxPreference preference) {
        trackedPreferences.add(preference);
    }
}
