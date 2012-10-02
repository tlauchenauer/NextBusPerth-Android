package com.lauchenauer.nextbusperth.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;
import com.lauchenauer.nextbusperth.helper.NewRoutesHelper;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;
import com.lauchenauer.nextbusperth.model.JourneyRoute;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.RouteJourneyPreference;

import java.util.List;
import java.util.Map;

public class AboutActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    private static final String SIX_DIGIT_STOP_NUMBER = "6 digit stop number";
    private static final String ROUTE_PREFIX = "route";
    private static final String WORK_ROUTE_PREFIX = ROUTE_PREFIX + "W";
    private static final String HOME_ROUTE_PREFIX = ROUTE_PREFIX + "H";

    private DatabaseHelper dbHelper;
    private EditTextPreference workStopNumberPref;
    private EditTextPreference homeStopNumberPref;
    private PreferenceScreen workRoutesScreenPref;
    private PreferenceScreen homeRoutesScreenPref;
    private String oldWorkStopNumber;
    private String oldHomeStopNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.about);

        workStopNumberPref = (EditTextPreference) getPreferenceScreen().findPreference(SettingsHelper.WORK_STOP_SETTING);
        homeStopNumberPref = (EditTextPreference) getPreferenceScreen().findPreference(SettingsHelper.HOME_STOP_SETTING);
        homeRoutesScreenPref = (PreferenceScreen) getPreferenceScreen().findPreference("routes-home");
        workRoutesScreenPref = (PreferenceScreen) getPreferenceScreen().findPreference("routes-work");

        clearRouteSelectionPreferences();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        oldHomeStopNumber = prefs.getString(SettingsHelper.HOME_STOP_SETTING, "");
        oldWorkStopNumber = prefs.getString(SettingsHelper.WORK_STOP_SETTING, "");

        String stopNumber = prefs.getString(SettingsHelper.WORK_STOP_SETTING, SIX_DIGIT_STOP_NUMBER);
        workStopNumberPref.setSummary(stopNumber);
        stopNumber = prefs.getString(SettingsHelper.HOME_STOP_SETTING, SIX_DIGIT_STOP_NUMBER);
        homeStopNumberPref.setSummary(stopNumber);

        dbHelper = new DatabaseHelper(getApplicationContext());
        processRoutes(true, dbHelper.getJourneyRoutes(JourneyRoute.WORK_JOURNEY));
        processRoutes(false, dbHelper.getJourneyRoutes(JourneyRoute.HOME_JOURNEY));

        Button download = (Button) findViewById(R.id.download_button);
        download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                downloadTimetables();
            }
        });

        NewRoutesHelper h = new NewRoutesHelper();
        h.retrieveRoutes("64");
        h.printData();
    }

    private void clearRouteSelectionPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Map<String, ?> settings = prefs.getAll();
        for (String key : settings.keySet()) {
            if (key.startsWith(ROUTE_PREFIX)) {
                editor.remove(key);
            }
        }
        editor.commit();
    }

    private void downloadTimetables() {
        Log.d(" < DOWNLOAD TIMETABLES", "-------------------------------------------------------------------------");

        dbHelper.getSelectedStopNumbersAndRoutes();

        Log.d(" > DOWNLOAD TIMETABLES", "-------------------------------------------------------------------------");
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("Preference changed", key);
        if (key.equals(SettingsHelper.WORK_STOP_SETTING)) {
            String stopNumber = sharedPreferences.getString(SettingsHelper.WORK_STOP_SETTING, "");
            if (stopNumber.trim().length() < 1) {
                stopNumber = SIX_DIGIT_STOP_NUMBER;
            }
            workStopNumberPref.setSummary(stopNumber);

            if (!stopNumber.equals(oldWorkStopNumber)) {
                new RoutesDownloadTask(this, true).execute(stopNumber);
                oldWorkStopNumber = stopNumber;
            }
        } else if (key.equals(SettingsHelper.HOME_STOP_SETTING)) {
            String stopNumber = sharedPreferences.getString(SettingsHelper.HOME_STOP_SETTING, "");
            if (stopNumber.trim().length() < 1) {
                stopNumber = SIX_DIGIT_STOP_NUMBER;
            }
            homeStopNumberPref.setSummary(stopNumber);

            new RoutesDownloadTask(this, false).execute(stopNumber);
            if (!stopNumber.equals(oldHomeStopNumber)) {
                new RoutesDownloadTask(this, false).execute(stopNumber);
                oldHomeStopNumber = stopNumber;
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getClass() != JourneyCheckBoxPreference.class) return true;

        JourneyCheckBoxPreference pref = (JourneyCheckBoxPreference)preference;
        RouteJourneyPreference p = pref.getRouteJourneyPreference();
        Log.d("PREFERENCE CHANGED", p.getRouteNumber() + " - " + p.getHeadsign() + " - " + pref.isChecked() + " - " + o.toString());

        return true;
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

    private void processRoutes(boolean workRoutes, List<? extends RouteJourneyPreference> routes) {
        if (workRoutes) {
            createRoutePreferences(workRoutesScreenPref, WORK_ROUTE_PREFIX, routes);
        } else {
            createRoutePreferences(homeRoutesScreenPref, HOME_ROUTE_PREFIX, routes);
        }
    }

    private void createRoutePreferences(PreferenceScreen screen, String key, List<? extends RouteJourneyPreference> routes) {
        screen.removeAll();

        for (RouteJourneyPreference r : routes) {
            CheckBoxPreference p = createCheckBoxPreference(key + "-" + r.getRouteNumber() + "-" + r.getStopNumber(), r);
            screen.addPreference(p);
        }
    }

    private CheckBoxPreference createCheckBoxPreference(String key, RouteJourneyPreference r) {
        CheckBoxPreference p = new JourneyCheckBoxPreference(this, r);
        p.setChecked(r.isSelected());
        p.setKey(key);
        p.setPersistent(false);
        p.setTitle(r.getRouteNumber());
        p.setSummary(r.getHeadsign());
        p.setOnPreferenceChangeListener(this);

        return p;
    }

    private class JourneyCheckBoxPreference extends CheckBoxPreference {
        private RouteJourneyPreference preference;

        public JourneyCheckBoxPreference(Context context, RouteJourneyPreference preference) {
            super(context);
            this.preference = preference;
        }

        public RouteJourneyPreference getRouteJourneyPreference() {
            return preference;
        }
    }

    private class RoutesDownloadTask extends AsyncTask<String, Void, List<? extends Route>> {
        private ProgressDialog progressDialog;
        private Context context;
        private boolean workRoutes;

        public RoutesDownloadTask(Context context, boolean workRoutes) {
            this.context = context;
            this.workRoutes = workRoutes;
        }

        @Override
        protected List<? extends Route> doInBackground(String... stopNumbers) {
            RoutesHelper helper = new RoutesHelper(getApplicationContext());

            helper.clearJourneyRoutesFromDatabase(workRoutes);
            List<Route> routes = helper.retrieveRoutes(stopNumbers[0]);
            helper.writeRoutesToDatabase(routes);
            helper.writeJourneyRoutesToDatabase(workRoutes, routes);

            return routes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<? extends Route> routes) {
            super.onPostExecute(routes);

            progressDialog.dismiss();
            processRoutes(workRoutes, routes);
        }
    }
}
