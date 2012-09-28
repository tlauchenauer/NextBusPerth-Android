package com.lauchenauer.nextbusperth.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;
import com.lauchenauer.nextbusperth.model.Route;

import java.util.List;

public class AboutActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference workStopNumberPref;
    private EditTextPreference homeStopNumberPref;
    private PreferenceScreen workRoutesScreenPref;
    private PreferenceScreen homeRoutesScreenPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.about);
        addPreferencesFromResource(R.xml.preferences);

        workStopNumberPref = (EditTextPreference) getPreferenceScreen().findPreference(SettingsHelper.WORK_STOP_SETTING);
        homeStopNumberPref = (EditTextPreference) getPreferenceScreen().findPreference(SettingsHelper.HOME_STOP_SETTING);
        homeRoutesScreenPref = (PreferenceScreen) getPreferenceScreen().findPreference("routes-home");
        workRoutesScreenPref = (PreferenceScreen) getPreferenceScreen().findPreference("routes-work");
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("Preference changed", key);
        if (key.equals(SettingsHelper.WORK_STOP_SETTING)) {
            String stopNumber = sharedPreferences.getString(SettingsHelper.WORK_STOP_SETTING, "");
            workStopNumberPref.setSummary(stopNumber);

            new RoutesDownloadTask(this, true).execute(stopNumber);
        } else if (key.equals(SettingsHelper.HOME_STOP_SETTING)) {
            String stopNumber = sharedPreferences.getString(SettingsHelper.HOME_STOP_SETTING, "");
            homeStopNumberPref.setSummary(stopNumber);

            new RoutesDownloadTask(this, false).execute(stopNumber);
        }
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

    private void processRoutes(boolean workRoutes, List<Route> routes) {
        Log.d("ROUTES", "found " + routes.size());

        if (workRoutes) {
            createRoutePreferences(workRoutesScreenPref, "routeW", routes);
        } else {
            createRoutePreferences(homeRoutesScreenPref, "routeH", routes);
        }
    }

    private void createRoutePreferences(PreferenceScreen screen, String key, List<Route> routes) {
        screen.removeAll();

        for (Route r : routes) {
            CheckBoxPreference p = createCheckBoxPreference(key + "-" + r.getRouteNumber() + "-" + r.getStopNumber(), r);
            screen.addPreference(p);
        }
    }

    private CheckBoxPreference createCheckBoxPreference(String key, Route route) {
        CheckBoxPreference p = new CheckBoxPreference(this);
        p.setChecked(false);
        p.setKey(key);
        p.setTitle(route.getRouteNumber());
        p.setSummary(route.getRouteName());

        return p;
    }

    private class RoutesDownloadTask extends AsyncTask<String, Void, List<Route>> { // AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue>
        private ProgressDialog progressDialog;
        private Context context;
        private boolean workRoutes;

        public RoutesDownloadTask(Context context, boolean workRoutes) {
            this.context = context;
            this.workRoutes = workRoutes;
        }

        @Override
        protected List<Route> doInBackground(String... stopNumbers) {
            return new RoutesHelper(getApplicationContext()).retrieveRoutes(stopNumbers[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<Route> routes) {
            super.onPostExecute(routes);

            progressDialog.dismiss();
            processRoutes(workRoutes, routes);
        }
    }
}
