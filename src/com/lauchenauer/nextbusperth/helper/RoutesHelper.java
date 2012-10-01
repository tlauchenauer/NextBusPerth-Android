package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.lauchenauer.nextbusperth.model.JourneyRoute;
import com.lauchenauer.nextbusperth.model.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoutesHelper implements JSONConstants {
    private static final String STOPS_URL = "routes/";

    private Context context;

    public RoutesHelper(Context context) {
        this.context = context;
    }

    public List<Route> retrieveRoutes(String stopNumber) {
        String jsonResult = UrlHelper.readTextFromUrl(STOPS_URL + stopNumber);

        return processJSON(jsonResult);
    }

    private List<Route> processJSON(String jsonText) {
        List<Route> routes = new ArrayList<Route>();

        try {
            JSONObject json = new JSONObject(jsonText);
            JSONArray routesArray = json.getJSONArray(ROUTES);
            for (int i = 0; i < routesArray.length(); i++) {
                JSONObject stopJSON = routesArray.getJSONObject(i);

                Route route = new Route(stopJSON.getString(STOP_NUMBER), stopJSON.getString(ROUTE_NUMBER), stopJSON.getString(ROUTE_NAME), stopJSON.getString(HEADSIGN));
                routes.add(route);
            }
        } catch (JSONException e) {
            Log.e("[StopsHelper.processJSON]", e.getMessage(), e);
        }

        return routes;
    }

    public void writeRoutesToDatabase(List<Route> routes) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            for (Route route : routes) {
                dbHelper.writeModelToDB(route, database);
            }
        } finally {
            database.close();
        }
    }

    public void clearJourneyRoutesFromDatabase(boolean workJourney) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            dbHelper.deleteFromDB(JourneyRoute.class, "journey_name = ?", new String[]{getJourneyName(workJourney)}, database);
        } finally {
            database.close();
        }
    }

    public void writeJourneyRoutesToDatabase(boolean workJourney, List<Route> routes) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            for (Route r : routes) {
                JourneyRoute jr = new JourneyRoute(getJourneyName(workJourney), r.getStopNumber(), r.getRouteNumber(), r.getHeadsign(), true);
                dbHelper.writeModelToDB(jr, database);
            }
        } finally {
            database.close();
        }
    }

    private String getJourneyName(boolean workJourney) {
        return workJourney ? JourneyRoute.WORK_JOURNEY : JourneyRoute.HOME_JOURNEY;
    }
}
