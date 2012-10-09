package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.prefs.StopSelectorPreference;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;
import com.lauchenauer.nextbusperth.helper.TimetableHelper;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String ROUTE_PREFIX = "route";

    private Map<Journey, StopSelectorPreference> stopNumberPreferences;
    private Map<Journey, PreferenceScreen> routesScreenPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.settings);

        final Journey workJourney = DatabaseHelper.getJourneyByName(NextBusApplication.WORK_JOURNEY_NAME);
        final Journey homeJourney = DatabaseHelper.getJourneyByName(NextBusApplication.HOME_JOURNEY_NAME);

        stopNumberPreferences = new HashMap<Journey, StopSelectorPreference>();
        stopNumberPreferences.put(workJourney, (StopSelectorPreference) getPreferenceScreen().findPreference("Work-Stop"));
        stopNumberPreferences.put(homeJourney, (StopSelectorPreference) getPreferenceScreen().findPreference("Home-Stop"));

        routesScreenPreferences = new HashMap<Journey, PreferenceScreen>();
        routesScreenPreferences.put(workJourney, (PreferenceScreen) getPreferenceScreen().findPreference("routes-work"));
        routesScreenPreferences.put(homeJourney, (PreferenceScreen) getPreferenceScreen().findPreference("routes-home"));

        stopNumberPreferences.get(workJourney).setSummary(workJourney.getStop_name());
        stopNumberPreferences.get(homeJourney).setSummary(homeJourney.getStop_name());

        processRoutes(workJourney);
        processRoutes(homeJourney);

        Button download = (Button) findViewById(R.id.download_button);
        download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new TimetableDownloadTask(SettingsActivity.this).execute();
            }
        });

        stopNumberPreferences.get(homeJourney).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivityForResult(i, homeJourney.getId().intValue());
                return true;
            }
        });

        stopNumberPreferences.get(workJourney).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivityForResult(i, workJourney.getId().intValue());
                return true;
            }
        });
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

            if (!stopNumber.equals(journey.getStop_number())) {
                journey.setStop_number(stopNumber);
                journey.setStop_name(stopName);
                journey.setStop_lat(lat);
                journey.setStop_lon(lon);
                DatabaseHelper.updateJourney(journey);
                stopNumberPreferences.get(journey).setSummary(stopName);

                new RoutesDownloadTask(this, journey).execute(stopNumber);
            }
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

    private void processRoutes(Journey journey) {
        RoutesHelper helper = new RoutesHelper();
        List<JourneyRoute> journeyRoutes = helper.getJourneyRoutes(journey);

        createRoutePreferences(routesScreenPreferences.get(journey), journeyRoutes);
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

        public RoutesDownloadTask(Context context, Journey journey) {
            this.context = context;
            this.journey = journey;
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

            processRoutes(journey);
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
