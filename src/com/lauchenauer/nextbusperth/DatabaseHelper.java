package com.lauchenauer.nextbusperth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.Stop;
import com.lauchenauer.nextbusperth.model.StopTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper {
    private static final String ROUTE_NUMBER = "route_number";
    private static final String ROUTE_NAME = "route_name";
    private static final String HEADSIGN = "headsign";
    private static final String TIMETABLE_DB = "nextbus-perth-db";
    private static final String TBL_STOPS = "tbl_stops";
    private static final String TBL_ROUTES = "tbl_routes";
    private static final String TBL_STOP_TIMES = "tbl_stop_times";
    private static final String[] TABLES = {TBL_STOPS, TBL_ROUTES, TBL_STOP_TIMES};

    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        database = context.openOrCreateDatabase(TIMETABLE_DB, SQLiteDatabase.CREATE_IF_NECESSARY, null);

        initDB();
    }

    private void initDB() {
        for (String table : TABLES) {
            database.execSQL("DROP TABLE IF EXISTS " + table);
        }

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_STOPS + " (stop_number STRING PRIMARY KEY, stop_name STRING)");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_ROUTES + " (stop_number STRING, route_number STRING, PRIMARY KEY(stop_number, route_number), route_name STRING, headsign STRING)");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_STOP_TIMES + " (stop_number STRING, route_number STRING, PRIMARY KEY(stop_number, route_number), departure_time DATETIME)");
    }

    private void writeStop(Stop stop) {
        database.insert(TBL_STOPS, null, stop.getContentValues());
    }

    private void writeRoute(Route route) {
        database.insert(TBL_ROUTES, null, route.getContentValues());
    }

    private void writeStopTimes(StopTime stopTime) {
        database.insert(TBL_STOP_TIMES, null, stopTime.getContentValues());
    }

    public void writeTimeTable(String timeTableJSON) throws JSONException {
        JSONObject json = new JSONObject(timeTableJSON);
        JSONArray stops = json.getJSONArray("timetable");
        for (int i = 0; i < stops.length(); i++) {
            JSONObject stop = stops.getJSONObject(i);

            writeStop(new Stop(stop));

            Log.d("stop_name", stop.getString("stop_name"));
            Log.d("stop_number", stop.getString("stop_number"));

            JSONArray routes = stop.getJSONArray("routes");
            for (int j = 0; j < routes.length(); j++) {
                JSONObject route = routes.getJSONObject(j);
                Log.d(ROUTE_NUMBER, route.getString(ROUTE_NUMBER));
                Log.d(ROUTE_NAME, route.getString(ROUTE_NAME));
                Log.d(HEADSIGN, route.getString(HEADSIGN));
            }
        }
    }
}
