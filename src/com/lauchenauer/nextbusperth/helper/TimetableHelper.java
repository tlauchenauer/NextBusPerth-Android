package com.lauchenauer.nextbusperth.helper;

import android.util.Log;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.Stop;

import static com.lauchenauer.nextbusperth.helper.DatabaseHelper.getOrInsertRoute;
import static com.lauchenauer.nextbusperth.helper.DatabaseHelper.getOrInsertStop;
import static com.lauchenauer.nextbusperth.helper.DatabaseHelper.getOrInsertStopTime;

public class TimetableHelper implements JSONConstants {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DEPARTURETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TIMETABLE_URL = "time_table/";

    public TimetableHelper() {
    }

    public void downloadTimeTable() {
        RoutesHelper helper = new RoutesHelper();
        for (Journey journey : DatabaseHelper.getAllJourneys()) {
            downloadTimeTable(journey.getStop_number(), helper.getSelectedRoutes(journey));
        }
    }

    private void downloadTimeTable(String stopNumber, List<Route> routes) {
        String timetableJSON = readTimeTable(stopNumber, new Date(), routes);

        writeTimeTable(timetableJSON);
    }

    public void writeTimeTable(String timeTableJSON) {
        try {
            JSONObject json = new JSONObject(timeTableJSON);
            JSONArray stops = json.getJSONArray(TIMETABLE);
            for (int i = 0; i < stops.length(); i++) {
                JSONObject stopJSON = stops.getJSONObject(i);
                JSONArray routes = stopJSON.getJSONArray(ROUTES);
                Stop stop = getOrInsertStop(stopJSON.getString(STOP_NUMBER), stopJSON.getString(STOP_NAME));

                for (int j = 0; j < routes.length(); j++) {
                    JSONObject routeJSON = routes.getJSONObject(j);
                    JSONArray departureTimes = routeJSON.getJSONArray(DEPARTURE_TIMES);
                    Route route = getOrInsertRoute(stop, routeJSON.getString(ROUTE_NUMBER), routeJSON.getString(ROUTE_NAME), routeJSON.getString(HEADSIGN));

                    for (int k = 0; k < departureTimes.length(); k++) {
                        String departureTime = (String) departureTimes.get(k);
                        try {
                            getOrInsertStopTime(route, DEPARTURETIME_FORMAT.parse(departureTime));
                        } catch (ParseException e) {
                            Log.e("[TimetableHelper.writeTimeTable]", e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("[TimetableHelper.writeTimeTable]", e.getMessage(), e);
        }
    }

    private String readTimeTable(String stopNumber, Date date, List<Route> routes) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Route r : routes) {
            params.add(new BasicNameValuePair("routes[][" + r.getNumber() + "]", r.getHeadsign()));
        }

        return UrlHelper.readTextFromUrlWithParams(TIMETABLE_URL + stopNumber + "/" + DATE_FORMAT.format(date), params);
    }
}
