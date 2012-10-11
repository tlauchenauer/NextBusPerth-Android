package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.prefs.ClickPreference;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;
import com.lauchenauer.nextbusperth.helper.TimetableHelper;

import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.am;
import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.none;
import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.pm;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String ROUTE_PREFIX = "route";

    private Map<Journey, ClickPreference> stopNumberPreferences;
    private Map<Journey, PreferenceScreen> routesScreenPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        routesScreenPreferences = new HashMap<Journey, PreferenceScreen>();
        stopNumberPreferences = new HashMap<Journey, ClickPreference>();

        PreferenceCategory journeysList = (PreferenceCategory) getPreferenceScreen().findPreference("journeys_list");
        for (Journey j : DatabaseHelper.getAllJourneys()) {
            PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(this);
            ps.setTitle(j.getName());
            journeysList.addPreference(ps);
            setupJourneyPreferenceScreen(j, ps);
        }

        ClickPreference download = (ClickPreference) getPreferenceScreen().findPreference("download_times");
        download.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new TimetableDownloadTask(SettingsActivity.this).execute();
                return true;
            }
        });
    }

    private void setupJourneyPreferenceScreen(final Journey journey, PreferenceScreen preferenceScreen) {
        final EditTextPreference journeyName = new EditTextPreference(this);
        journeyName.setTitle(journey.getName());
        journeyName.setSummary("Change the journey name");
        journeyName.setText(journey.getName());
        journeyName.setPersistent(false);
        preferenceScreen.addPreference(journeyName);

        journeyName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                journey.setName(o.toString());
                DatabaseHelper.updateJourney(journey);
                journeyName.setTitle(o.toString());
                return true;
            }
        });

        ClickPreference stopSelection = new ClickPreference(this, null);
        stopSelection.setTitle("Stop");
        stopSelection.setSummary(journey.getStop_name());
        stopNumberPreferences.put(journey, stopSelection);
        preferenceScreen.addPreference(stopSelection);

        stopSelection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivityForResult(i, journey.getId().intValue());
                return true;
            }
        });

        PreferenceScreen routesScreenPreference = getPreferenceManager().createPreferenceScreen(this);
        routesScreenPreference.setTitle(journey.getName() + " Routes");
        routesScreenPreference.setSummary("Select all routes that should have their departure times listed.");
        routesScreenPreferences.put(journey, routesScreenPreference);
        preferenceScreen.addPreference(routesScreenPreference);

        processRoutes(journey, routesScreenPreference);

        ListPreference defaultFor = new ListPreference(this);
        defaultFor.setPersistent(false);
        defaultFor.setTitle("Default for");
        defaultFor.setSummary("changing this will override all other default settings");
        defaultFor.setEntries(new String[]{"AM", "PM", "none"});
        defaultFor.setEntryValues(new String[]{"" + am.getId(), "" + pm.getId(), "" + none.getId()});
        defaultFor.setValue(journey.getDefault_for().toString());
        preferenceScreen.addPreference(defaultFor);

        defaultFor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                journey.setDefault_for(new Integer(o.toString()));
                DatabaseHelper.updateJourney(journey);

                // todo adjust other am pm settings

                return true;
            }
        });


//        ClickPreference deleteJourney = new ClickPreference(this, null);
//        deleteJourney.setTitle("Delete Journey");
//        deleteJourney.setSummary("Permanently delete this journey");
//        deleteJourney.setWidgetLayoutResource(R.layout.remove_image);
//        preferenceScreen.addPreference(deleteJourney);
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
            stopNumberPreferences.get(journey).setSummary(stopName);

            new RoutesDownloadTask(this, journey, routesScreenPreferences.get(journey)).execute(stopNumber);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getClass() != JourneyCheckBoxPreference.class) return true;

        JourneyCheckBoxPreference pref = (JourneyCheckBoxPreference) preference;
        JourneyRoute jr = pref.getJourneyRoute();
        jr.setSelected((Boolean) o);

        JourneyRouteDao journeyRouteDao = NextBusApplication.getApp().getDaoSession().getJourneyRouteDao();
        journeyRouteDao.update(jr);

        return true;
    }

    private void processRoutes(Journey journey, PreferenceScreen preferenceScreen) {
        RoutesHelper helper = new RoutesHelper();
        List<JourneyRoute> journeyRoutes = helper.getJourneyRoutes(journey);

        createRoutePreferences(preferenceScreen, journeyRoutes);
    }

    private void createRoutePreferences(PreferenceScreen screen, List<JourneyRoute> routes) {
        screen.removeAll();

        SelectAllListPreference selectAll = new SelectAllListPreference(this);
        screen.addPreference(selectAll);

        for (JourneyRoute jr : routes) {
            CheckBoxPreference p = createCheckBoxPreference(ROUTE_PREFIX + "-" + jr.getId(), jr);
            selectAll.addTrackedPreference(p);
            screen.addPreference(p);
        }
    }

    private CheckBoxPreference createCheckBoxPreference(String key, JourneyRoute jr) {
        CheckBoxPreference p = new JourneyCheckBoxPreference(this, jr);
        p.setChecked(jr.getSelected());
        p.setKey(key);
        p.setPersistent(false);
        p.setTitle(jr.getRoute().getNumber());
        p.setSummary(jr.getRoute().getHeadsign());
        p.setOnPreferenceChangeListener(this);

        return p;
    }

    private class SelectAllListPreference extends ListPreference implements Preference.OnPreferenceChangeListener {
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

    private class JourneyCheckBoxPreference extends CheckBoxPreference {
        private JourneyRoute journeyRoute;

        public JourneyCheckBoxPreference(Context context, JourneyRoute journeyRoute) {
            super(context);
            this.journeyRoute = journeyRoute;
        }

        public JourneyRoute getJourneyRoute() {
            return journeyRoute;
        }
    }

    private class RoutesDownloadTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog;
        private Context context;
        private Journey journey;
        private PreferenceScreen preferenceScreen;

        public RoutesDownloadTask(Context context, Journey journey, PreferenceScreen preferenceScreen) {
            this.context = context;
            this.journey = journey;
            this.preferenceScreen = preferenceScreen;
        }

        @Override
        protected Boolean doInBackground(String... stopNumbers) {
            RoutesHelper helper = new RoutesHelper();

            helper.clearJourneyRoutesFromDatabase(journey);
            List<Route> routes = helper.retrieveRoutes(stopNumbers[0]);
            helper.writeJourneyRoutesToDatabase(journey, routes);

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("downloading routes...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            processRoutes(journey, preferenceScreen);
            progressDialog.dismiss();
        }
    }

    private class TimetableDownloadTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog progressDialog;
        private Context context;

        public TimetableDownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            new TimetableHelper().downloadTimeTable();

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("downloading departure times ...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            progressDialog.dismiss();
        }
    }
}
