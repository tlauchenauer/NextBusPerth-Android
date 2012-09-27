package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.Service;
import com.lauchenauer.nextbusperth.model.Stop;
import com.lauchenauer.nextbusperth.model.StopTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper {
    private static final SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String TIMETABLE_DB = "nextbus-perth-db";
    private static final String TBL_STOPS = "tbl_stops";
    private static final String TBL_ROUTES = "tbl_routes";
    private static final String TBL_STOP_TIMES = "tbl_stop_times";
    private static final String[] TABLES = {TBL_STOPS, TBL_ROUTES, TBL_STOP_TIMES};
    private static final long DEPARTURE_DELTA = 5 * 60 * 1000l;

    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;

        initDB();
    }

    private SQLiteDatabase getDatabase() {
        return context.openOrCreateDatabase(TIMETABLE_DB, SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }

    public void clearDB() {
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
        database.replace(TBL_STOPS, null, stop.getContentValues());
    }

    private void writeRoute(Route route, SQLiteDatabase database) {
        database.replace(TBL_ROUTES, null, route.getContentValues());
    }

    private void writeStopTimes(StopTime stopTime, SQLiteDatabase database) {
        database.replace(TBL_STOP_TIMES, null, stopTime.getContentValues());
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

    public List<Service> getNextBuses(String stopNumber, int maxResults) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = null;
        ArrayList<Service> services = new ArrayList<Service>();
        try {
            String queryString = "SELECT s.stop_number, s.stop_name, r.route_number, r.route_name, r.headsign, st.departure_time";
            queryString += " FROM " + TBL_STOPS + " s";
            queryString += " JOIN " + TBL_ROUTES + " r ON s.stop_number = r.stop_number";
            queryString += " JOIN " + TBL_STOP_TIMES + " st ON s.stop_number = st.stop_number AND r.route_number = st.route_number";
            queryString += " WHERE s.stop_number = ? AND st.departure_time >= ?";
            queryString += " ORDER BY st.departure_time";
            queryString += " LIMIT " + maxResults;

            Date startDate = new Date(new Date().getTime() - DEPARTURE_DELTA);
            cursor = database.rawQuery(queryString, new String[]{stopNumber, ISO8601FORMAT.format(startDate)});
            while (cursor.moveToNext()) {
                services.add(retrieveService(cursor));
            }
        } catch (ParseException e) {
            Log.e("[DatabaseHelper.getNextBuses]", e.getMessage(), e);
        } catch (SQLiteException e) {
            Log.e("[DatabaseHelper.getNextBuses]", e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            database.close();
        }

        return services;
    }

    private Service retrieveService(Cursor cursor) throws ParseException {
        return new Service(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), ISO8601FORMAT.parse(cursor.getString(5)));
    }

    private void outputCursor(Cursor cursor) {
        Log.d("[CURSOR]", "---------------------------------------");

        String columns = "";
        for (String col : cursor.getColumnNames()) {
            columns += col + "  |";
        }
        Log.d("[CURSOR]", columns);

        while (cursor.moveToNext()) {
            String values = "";
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                values += cursor.getString(i) + "  |";
            }

            Log.d("[CURSOR]", values);
        }

        Log.d("[CURSOR]", "---------------------------------------");
    }
}
