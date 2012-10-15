package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import java.util.HashMap;
import java.util.Map;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.prefs.ClickPreference;
import com.lauchenauer.nextbusperth.app.prefs.JourneyPreference;
import com.lauchenauer.nextbusperth.app.prefs.TimetableDownloadTask;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;

public class SettingsActivity extends PreferenceActivity {
    private Map<Journey, JourneyPreference> journeyPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        journeyPreferences = new HashMap<Journey, JourneyPreference>();

        PreferenceCategory journeysList = (PreferenceCategory) getPreferenceScreen().findPreference("journeys_list");
        for (Journey j : DatabaseHelper.getAllJourneys()) {
            createJourneyPreference(j, journeysList);
        }

        ClickPreference download = (ClickPreference) getPreferenceScreen().findPreference("download_times");
        download.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new TimetableDownloadTask(SettingsActivity.this).execute();
                return true;
            }
        });

        ClickPreference addJourney = (ClickPreference) getPreferenceScreen().findPreference("add_journey");
        addJourney.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                addNewJourney();
                return true;
            }
        });
    }

    private void addNewJourney() {
        Journey journey = DatabaseHelper.addNewJourney("new journey");

        PreferenceCategory journeysList = (PreferenceCategory) getPreferenceScreen().findPreference("journeys_list");
        createJourneyPreference(journey, journeysList);
    }

    private void createJourneyPreference(Journey journey, PreferenceCategory journeysList) {
        JourneyPreference journeyPreference = new JourneyPreference(journey, this, journeysList);

        journeyPreferences.put(journey, journeyPreference);
    }

    public void removeJourneyPreference(Journey journey) {
        journeyPreferences.remove(journey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String stopNumber = data.getStringExtra("stop_number");
            String stopName = data.getStringExtra("stop_name");
            int lat = data.getIntExtra("lat", 0);
            int lon = data.getIntExtra("long", 0);

            Journey journey = DatabaseHelper.getJourneyById(requestCode);
            journey.setStop_number(stopNumber);
            journey.setStop_name(stopName);
            journey.setStop_lat(lat);
            journey.setStop_lon(lon);
            DatabaseHelper.updateJourney(journey);

            journeyPreferences.get(journey).stopUpdated();
        }
    }
}
