package com.lauchenauer.nextbusperth.app.prefs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.helper.RoutesHelper;

public class RoutesDownloadTask extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog progressDialog;
    private Context context;
    private Journey journey;
    private JourneyPreference journeyPreference;

    public RoutesDownloadTask(Context context, Journey journey, JourneyPreference journeyPreference) {
        this.context = context;
        this.journey = journey;
        this.journeyPreference = journeyPreference;
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

        journeyPreference.processRoutes(journey);
        progressDialog.dismiss();
    }
}