package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.Stop;
import com.lauchenauer.nextbusperth.model.StopTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimetableHelper implements JSONConstants {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TIMETABLE_URL = "time_table/";

    private Context context;

    public TimetableHelper(Context context) {
        this.context = context;
    }

    public void downloadTimeTable() {
        SettingsHelper prefs = new SettingsHelper(context);

        downloadTimeTable(prefs.getHomeStopNumber());
        downloadTimeTable(prefs.getWorkStopNumber());
    }

    private void downloadTimeTable(String stopNumber) {
        String timetableJSON = readTimeTable(stopNumber, new Date());

        writeTimeTable(timetableJSON);
    }

    public void writeTimeTable(String timeTableJSON) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            JSONObject json = new JSONObject(timeTableJSON);
            JSONArray stops = json.getJSONArray(TIMETABLE);
            for (int i = 0; i < stops.length(); i++) {
                JSONObject stopJSON = stops.getJSONObject(i);
                JSONArray routes = stopJSON.getJSONArray(ROUTES);
                Stop stop = new Stop(stopJSON.getString(STOP_NUMBER), stopJSON.getString(STOP_NAME));
                dbHelper.writeModelToDB(stop, database);

                for (int j = 0; j < routes.length(); j++) {
                    JSONObject routeJSON = routes.getJSONObject(j);
                    JSONArray departureTimes = routeJSON.getJSONArray(DEPARTURE_TIMES);
                    Route route = new Route(stop.getStopNumber(), routeJSON.getString(ROUTE_NUMBER), routeJSON.getString(ROUTE_NAME), routeJSON.getString(HEADSIGN));
                    dbHelper.writeModelToDB(route, database);

                    for (int k = 0; k < departureTimes.length(); k++) {
                        String departureTime = (String) departureTimes.get(k);
                        dbHelper.writeModelToDB(new StopTime(stop.getStopNumber(), route.getRouteNumber(), departureTime), database);
                    }
                }
            }

            Log.d("Stops COUNT", "rows: " + dbHelper.fetchRowCount(Stop.class, database));
            Log.d("Routes COUNT", "rows: " + dbHelper.fetchRowCount(Route.class, database));
            Log.d("StopTimes COUNT", "rows: " + dbHelper.fetchRowCount(StopTime.class, database));
        } catch (JSONException e) {
            Log.e("[TimetableHelper.writeTimeTable]", e.getMessage(), e);
        } finally {
            database.close();
        }
    }

    private String readTimeTable(String stopNumber, Date date) {
        Log.d("[TimetableHelper.readTimeTable]", "Stop: " + stopNumber + "  for: " + DATE_FORMAT.format(date));

        return UrlHelper.readTextFromUrl(TIMETABLE_URL + stopNumber + "/" + DATE_FORMAT.format(date));
    }
}
