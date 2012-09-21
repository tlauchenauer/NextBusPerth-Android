package com.lauchenauer.nextbusperth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.Stop;
import com.lauchenauer.nextbusperth.model.StopTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper {
    private static final String TIMETABLE_DB = "nextbus-perth-db";
    private static final String TBL_STOPS = "tbl_stops";
    private static final String TBL_ROUTES = "tbl_routes";
    private static final String TBL_STOP_TIMES = "tbl_stop_times";
    private static final String[] TABLES = {TBL_STOPS, TBL_ROUTES, TBL_STOP_TIMES};

    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;

        initDB();
    }

    private SQLiteDatabase getDatabase() {
        return context.openOrCreateDatabase(TIMETABLE_DB, SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }

    private void clearDB() {
        SQLiteDatabase database = getDatabase();
        try {
            for (String table : TABLES) {
                database.execSQL("DROP TABLE IF EXISTS " + table);
            }
        } finally {
            database.close();
        }
    }

    private void initDB() {
        SQLiteDatabase database = getDatabase();
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_STOPS + " (stop_number STRING PRIMARY KEY, stop_name STRING)");
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_ROUTES + " (stop_number STRING, route_number STRING, route_name STRING, headsign STRING, PRIMARY KEY(stop_number, route_number))");
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_STOP_TIMES + " (stop_number STRING, route_number STRING, departure_time DATETIME, PRIMARY KEY(stop_number, route_number, departure_time))");
        } finally {
            database.close();
        }
    }

    private void writeStop(Stop stop, SQLiteDatabase database) {
        database.insert(TBL_STOPS, null, stop.getContentValues());
    }

    private void writeRoute(Route route, SQLiteDatabase database) {
        database.insert(TBL_ROUTES, null, route.getContentValues());
    }

    private void writeStopTimes(StopTime stopTime, SQLiteDatabase database) {
        database.insert(TBL_STOP_TIMES, null, stopTime.getContentValues());
    }

    private long fetchRowCount(String table, SQLiteDatabase database) {
        String sql = "SELECT COUNT(*) FROM " + table;
        SQLiteStatement statement = database.compileStatement(sql);
        return statement.simpleQueryForLong();
    }

    public void writeTimeTable(String timeTableJSON) {
        SQLiteDatabase database = getDatabase();
        try {
            JSONObject json = new JSONObject(timeTableJSON);
            JSONArray stops = json.getJSONArray("timetable");
            for (int i = 0; i < stops.length(); i++) {
                JSONObject stopJSON = stops.getJSONObject(i);
                JSONArray routes = stopJSON.getJSONArray("routes");
                Stop stop = new Stop(stopJSON);
                writeStop(stop, database);

                for (int j = 0; j < routes.length(); j++) {
                    JSONObject routeJSON = routes.getJSONObject(j);
                    JSONArray departureTimes = routeJSON.getJSONArray("departure_times");
                    Route route = new Route(stop.getStopNumber(), routeJSON);
                    writeRoute(route, database);

                    for (int k = 0; k < departureTimes.length(); k++) {
                        String departureTime = (String) departureTimes.get(k);
                        writeStopTimes(new StopTime(stop.getStopNumber(), route.getRouteNumber(), departureTime), database);
                    }
                }
            }

            Log.d("Stops COUNT", "rows: " + fetchRowCount(TBL_STOPS, database));
            Log.d("Routes COUNT", "rows: " + fetchRowCount(TBL_ROUTES, database));
            Log.d("StopTimes COUNT", "rows: " + fetchRowCount(TBL_STOP_TIMES, database));
        } catch (JSONException e) {
            Log.e("[DatabaseHelper.writeTimeTable]", e.getMessage(), e);
        } finally {
            database.close();
        }
    }
}
