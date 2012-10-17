package com.lauchenauer.nextbusperth.app.prefs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import java.util.List;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.NextBusApplication;
import com.lauchenauer.nextbusperth.app.SettingsActivity;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDefaultFor;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;

import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.am;
import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.none;
import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.pm;

public class JourneyPreference implements Preference.OnPreferenceChangeListener {
    private static final String ROUTE_PREFIX = "route";

    private Journey journey;
    private SettingsActivity parent;
    private PreferenceCategory journeysList;
    private PreferenceScreen journeyPreferenceScreen;
    private PreferenceScreen routesPreferenceScreen;
    private EditTextPreference journeyName;
    private ClickPreference stopSelection;
    private ClickPreference deleteJourneyBtn;
    private ListPreference defaultForPreference;

    public JourneyPreference(Journey journey, SettingsActivity parentActivity, PreferenceCategory journeysList) {
        this.journey = journey;
        this.parent = parentActivity;
        this.journeysList = journeysList;

        journeyPreferenceScreen = parent.getPreferenceManager().createPreferenceScreen(parent);
        journeyPreferenceScreen.setTitle(journey.getName());
        journeyPreferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                deleteJourneyBtn.setEnabled(DatabaseHelper.getJourneysCount() > 1);

                return true;
            }
        });

        setupJourneyPreferenceScreen();

        journeysList.addPreference(journeyPreferenceScreen);
    }

    private void setupJourneyPreferenceScreen() {
        createJourneyNameEdit();
        createStopEditor();
        createRoutesSelection();
        createDefaultSelection();
        createDeleteJourneyButton();

        processRoutes(journey);
    }

    private void createJourneyNameEdit() {
        journeyName = new EditTextPreference(parent);
        journeyName.setTitle(journey.getName());
        journeyName.setSummary("Change the journey name");
        journeyName.setText(journey.getName());
        journeyName.setPersistent(false);
        journeyPreferenceScreen.addPreference(journeyName);

        journeyName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                journey.setName(o.toString());
                DatabaseHelper.updateJourney(journey);
                journeyName.setTitle(o.toString());
                routesPreferenceScreen.setTitle(journey.getName() + " Routes");
                journeyPreferenceScreen.setTitle(journey.getName());
                return true;
            }
        });
    }

    private void createStopEditor() {
        stopSelection = new ClickPreference(parent, null);
        stopSelection.setTitle("Stop");
        stopSelection.setSummary(journey.getStop_name());
        journeyPreferenceScreen.addPreference(stopSelection);

        stopSelection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(parent, StopSelectorActivity.class);
                i.putExtra("lat", -31957406);
                i.putExtra("lon", 115851122);
                i.putExtra("zoom", 12);

                if (journey.getStop_lat() != 0) {
                    i.putExtra("lat", journey.getStop_lat());
                    i.putExtra("lon", journey.getStop_lon());
                    i.putExtra("zoom", 19);
                }
                parent.startActivityForResult(i, journey.getId().intValue());
                return true;
            }
        });
    }

    private void createRoutesSelection() {
        routesPreferenceScreen = parent.getPreferenceManager().createPreferenceScreen(parent);
        routesPreferenceScreen.setTitle(journey.getName() + " Routes");
        routesPreferenceScreen.setSummary("Select all routes that should have their departure times listed.");
        journeyPreferenceScreen.addPreference(routesPreferenceScreen);
    }

    private void createDefaultSelection() {
        defaultForPreference = new ListPreference(parent);
        defaultForPreference.setPersistent(false);
        defaultForPreference.setTitle("Default for");
        defaultForPreference.setSummary("changing this will override all other default settings");
        defaultForPreference.setEntries(new String[]{"AM", "PM", "none"});
        defaultForPreference.setEntryValues(new String[]{"" + am.getId(), "" + pm.getId(), "" + none.getId()});
        defaultForPreference.setValue(journey.getDefault_for().toString());
        journeyPreferenceScreen.addPreference(defaultForPreference);

        defaultForPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                int defaultFor = Integer.parseInt(o.toString());
                journey.setDefault_for(defaultFor);
                DatabaseHelper.updateJourney(journey);

                if (defaultFor == none.getId()) return true;

                for (Journey j : DatabaseHelper.getAllJourneys()) {
                    if (j.getDefault_for() == defaultFor && (!j.equals(journey))) {
                        j.setDefault_for(none.getId());
                        DatabaseHelper.updateJourney(j);
                    }
                }
                parent.updateDefaultFor();

                return true;
            }
        });
    }

    public void updateDefaultFor() {
        journey.refresh();
        if (journey.getDefault_for() == none.getId()) {
            defaultForPreference.setValue(journey.getDefault_for().toString());
        }
    }

    private void createDeleteJourneyButton() {
        deleteJourneyBtn = new ClickPreference(parent, null);
        deleteJourneyBtn.setTitle("Delete Journey");
        deleteJourneyBtn.setSummary("Permanently delete this journey");
        deleteJourneyBtn.setWidgetLayoutResource(R.layout.remove_image);
        journeyPreferenceScreen.addPreference(deleteJourneyBtn);

        deleteJourneyBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(parent)
                        .setTitle("Delete Journey?")
                        .setMessage("Do you really want to permanently delete this journey??")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteJourney(journey);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });
    }

    private void deleteJourney(Journey journey) {
        DatabaseHelper.deleteJourneyAndJourneyRoutes(journey);

        journeyPreferenceScreen.getDialog().dismiss();
        journeysList.removePreference(journeyPreferenceScreen);
        parent.removeJourneyPreference(journey);
    }

    void processRoutes(Journey journey) {
        RoutesHelper helper = new RoutesHelper();
        List<JourneyRoute> journeyRoutes = helper.getJourneyRoutes(journey);

        createRoutePreferences(journeyRoutes);
    }

    private void createRoutePreferences(List<JourneyRoute> routes) {
        routesPreferenceScreen.removeAll();

        SelectAllListPreference selectAll = new SelectAllListPreference(parent);
        routesPreferenceScreen.addPreference(selectAll);

        for (JourneyRoute jr : routes) {
            CheckBoxPreference p = createCheckBoxPreference(ROUTE_PREFIX + "-" + jr.getId(), jr);
            selectAll.addTrackedPreference(p);
            routesPreferenceScreen.addPreference(p);
        }
    }

    private CheckBoxPreference createCheckBoxPreference(String key, JourneyRoute jr) {
        CheckBoxPreference p = new JourneyRouteCheckBoxPreference(parent, jr);
        p.setChecked(jr.getSelected());
        p.setKey(key);
        p.setPersistent(false);
        p.setTitle(jr.getRoute().getNumber());
        p.setSummary(jr.getRoute().getHeadsign());
        p.setOnPreferenceChangeListener(this);

        return p;
    }

    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getClass() != JourneyRouteCheckBoxPreference.class) return true;

        JourneyRouteCheckBoxPreference pref = (JourneyRouteCheckBoxPreference) preference;
        JourneyRoute jr = pref.getJourneyRoute();
        jr.setSelected((Boolean) o);

        JourneyRouteDao journeyRouteDao = NextBusApplication.getApp().getDaoSession().getJourneyRouteDao();
        journeyRouteDao.update(jr);

        return true;
    }

    public void stopUpdated() {
        stopSelection.setSummary(journey.getStop_name());

        new RoutesDownloadTask(parent, journey, this).execute(journey.getStop_number());
    }
}
