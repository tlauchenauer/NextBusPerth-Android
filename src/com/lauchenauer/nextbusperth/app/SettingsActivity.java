package com.lauchenauer.nextbusperth.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.prefs.StopSelectorPreference;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.helper.TimetableHelper;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;

import static com.lauchenauer.nextbusperth.app.NextBusApplication.JourneyType;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String SIX_DIGIT_STOP_NUMBER = "6 digit stop number";
    private static final String ROUTE_PREFIX = "route";
    private static final String WORK_ROUTE_PREFIX = ROUTE_PREFIX + "W";
    private static final String HOME_ROUTE_PREFIX = ROUTE_PREFIX + "H";
    private static final int HOME_STOP = 1;
    private static final int WORK_STOP = 2;

    private StopSelectorPreference workStopNumberPref;
    private StopSelectorPreference homeStopNumberPref;
    private PreferenceScreen workRoutesScreenPref;
    private PreferenceScreen homeRoutesScreenPref;
    private String oldWorkStopNumber;
    private String oldHomeStopNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.settings);

        workStopNumberPref = (StopSelectorPreference) getPreferenceScreen().findPreference(SettingsHelper.WORK_STOP_SETTING);
        homeStopNumberPref = (StopSelectorPreference) getPreferenceScreen().findPreference(SettingsHelper.HOME_STOP_SETTING);
        homeRoutesScreenPref = (PreferenceScreen) getPreferenceScreen().findPreference("routes-home");
        workRoutesScreenPref = (PreferenceScreen) getPreferenceScreen().findPreference("routes-work");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        oldHomeStopNumber = prefs.getString(SettingsHelper.HOME_STOP_SETTING, "");
        oldWorkStopNumber = prefs.getString(SettingsHelper.WORK_STOP_SETTING, "");

        String stopNumber = prefs.getString(SettingsHelper.WORK_STOP_SETTING, SIX_DIGIT_STOP_NUMBER);
        workStopNumberPref.setSummary(stopNumber);
        stopNumber = prefs.getString(SettingsHelper.HOME_STOP_SETTING, SIX_DIGIT_STOP_NUMBER);
        homeStopNumberPref.setSummary(stopNumber);

        processRoutes(JourneyType.work);
        processRoutes(JourneyType.home);

        Button download = (Button) findViewById(R.id.download_button);
        download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new TimetableDownloadTask(SettingsActivity.this).execute();
            }
        });

        homeStopNumberPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivityForResult(i, HOME_STOP);
                return true;
            }
        });

        workStopNumberPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, StopSelectorActivity.class);
                startActivityForResult(i, WORK_STOP);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String stopNumber = data.getStringExtra("stop_number");
            SettingsHelper helper = new SettingsHelper(this);

            switch (requestCode) {
                case HOME_STOP:
                    homeStopNumberPref.setSummary(stopNumber);
                    helper.setJourneyStopNumber(JourneyType.home, stopNumber);
                    if (!stopNumber.equals(oldHomeStopNumber)) {
                        new RoutesDownloadTask(this, JourneyType.home).execute(stopNumber);
                        oldHomeStopNumber = stopNumber;
                    }
                    break;
                case WORK_STOP:
                    workStopNumberPref.setSummary(stopNumber);
                    helper.setJourneyStopNumber(JourneyType.work, stopNumber);
                    if (!stopNumber.equals(oldWorkStopNumber)) {
                        new RoutesDownloadTask(this, JourneyType.work).execute(stopNumber);
                        oldWorkStopNumber = stopNumber;
                    }
                    break;
            }
        }
    }

//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Log.d("Preference changed", key);
//        if (key.equals(SettingsHelper.WORK_STOP_SETTING)) {
//            String stopNumber = sharedPreferences.getString(SettingsHelper.WORK_STOP_SETTING, "");
//            if (stopNumber.trim().length() < 1) {
//                stopNumber = SIX_DIGIT_STOP_NUMBER;
//            }
//            workStopNumberPref.setSummary(stopNumber);
//
//            if (!stopNumber.equals(oldWorkStopNumber)) {
//                new RoutesDownloadTask(this, JourneyType.work).execute(stopNumber);
//                oldWorkStopNumber = stopNumber;
//            }
//        } else if (key.equals(SettingsHelper.HOME_STOP_SETTING)) {
//            String stopNumber = sharedPreferences.getString(SettingsHelper.HOME_STOP_SETTING, "");
//            if (stopNumber.trim().length() < 1) {
//                stopNumber = SIX_DIGIT_STOP_NUMBER;
//            }
//            homeStopNumberPref.setSummary(stopNumber);
//
//            if (!stopNumber.equals(oldHomeStopNumber)) {
//                new RoutesDownloadTask(this, JourneyType.home).execute(stopNumber);
//                oldHomeStopNumber = stopNumber;
//            }
//        }
//    }

    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getClass() != JourneyCheckBoxPreference.class) return true;

        JourneyCheckBoxPreference pref = (JourneyCheckBoxPreference) preference;
        JourneyRoute jr = pref.getJourneyRoute();
        jr.setSelected((Boolean) o);

        JourneyRouteDao journeyRouteDao = NextBusApplication.getApp().getDaoSession().getJourneyRouteDao();
        journeyRouteDao.update(jr);

        return true;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
//    }

    private void processRoutes(JourneyType journeyType) {
        RoutesHelper helper = new RoutesHelper();
        List<JourneyRoute> journeyRoutes = helper.getJourneyRoutes(journeyType);
        switch (journeyType) {
            case work:
                createRoutePreferences(workRoutesScreenPref, WORK_ROUTE_PREFIX, journeyRoutes);
                break;
            case home:
                createRoutePreferences(homeRoutesScreenPref, HOME_ROUTE_PREFIX, journeyRoutes);
                break;
        }
    }

    private void createRoutePreferences(PreferenceScreen screen, String key, List<JourneyRoute> routes) {
        screen.removeAll();

        for (JourneyRoute jr : routes) {
            CheckBoxPreference p = createCheckBoxPreference(key + "-" + jr.getId(), jr);
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
        private JourneyType journeyType;

        public RoutesDownloadTask(Context context, JourneyType journeyType) {
            this.context = context;
            this.journeyType = journeyType;
        }

        @Override
        protected Boolean doInBackground(String... stopNumbers) {
            RoutesHelper helper = new RoutesHelper();

            helper.clearJourneyRoutesFromDatabase(journeyType);
            List<Route> routes = helper.retrieveRoutes(stopNumbers[0]);
            helper.writeJourneyRoutesToDatabase(journeyType, routes);

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

            processRoutes(journeyType);
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
            TimetableHelper helper = new TimetableHelper(context);
            helper.downloadTimeTable();

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
