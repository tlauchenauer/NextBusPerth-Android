package com.lauchenauer.nextbusperth.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.lauchenauer.nextbusperth.model.JourneyRoute;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.Service;
import com.lauchenauer.nextbusperth.model.Stop;
import com.lauchenauer.nextbusperth.model.StopTime;

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
    private static final String TBL_JOURNEY_ROUTES = "tbl_journey_routes";
    private static final String[] TABLES = {TBL_STOPS, TBL_ROUTES, TBL_STOP_TIMES, TBL_JOURNEY_ROUTES};
    private static final long DEPARTURE_DELTA = 5 * 60 * 1000l;

    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;

        initDB();
    }

    public SQLiteDatabase getDatabase() {
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
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_JOURNEY_ROUTES + " (journey_name STRING, stop_number STRING, route_number STRING, BOOLEAN selected, PRIMARY KEY(journey_name, stop_number, route_number))");
        } finally {
            database.close();
        }
    }

    public void writeModelToDB(Object o, SQLiteDatabase database) {
        ContentValues values = ContentValuesFactory.getContentValues(o);
        String table = getTableForModel(o);

        database.replace(table, null, values);
    }

    private String getTableForModel(Object o) {
        return getTableForClass(o.getClass());
    }

    private String getTableForClass(Class clazz) {
        String table = null;
        if (clazz == Route.class) {
            table = TBL_ROUTES;
        } else if (clazz == Stop.class) {
            table = TBL_STOPS;
        } else if (clazz == StopTime.class) {
            table = TBL_STOP_TIMES;
        } else if (clazz == JourneyRoute.class) {
            table = TBL_JOURNEY_ROUTES;
        }

        return table;
    }

    public long fetchRowCount(Class clazz, SQLiteDatabase database) {
        String sql = "SELECT COUNT(*) FROM " + getTableForClass(clazz);
        SQLiteStatement statement = database.compileStatement(sql);
        return statement.simpleQueryForLong();
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
