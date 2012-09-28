package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            JSONObject json = new JSONObject(jsonText);
            JSONArray routesArray = json.getJSONArray(ROUTES);
            for (int i = 0; i < routesArray.length(); i++) {
                JSONObject stopJSON = routesArray.getJSONObject(i);

                Route route = new Route(stopJSON.getString(STOP_NUMBER), stopJSON.getString(ROUTE_NUMBER), stopJSON.getString(ROUTE_NAME), stopJSON.getString(HEADSIGN));
                dbHelper.writeModelToDB(route, database);
                routes.add(route);
            }
        } catch (JSONException e) {
            Log.e("[StopsHelper.processJSON]", e.getMessage(), e);
        } finally {
            database.close();
        }

        return routes;
    }
}
