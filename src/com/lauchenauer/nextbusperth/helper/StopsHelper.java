package com.lauchenauer.nextbusperth.helper;

import android.util.Log;
import com.google.android.maps.GeoPoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.lauchenauer.nextbusperth.app.prefs.map.MapStop;

public class StopsHelper implements JSONConstants {
    private static final String STOPS_URL = "stops";

    public static List<MapStop> retrieveStops(GeoPoint topLeft, GeoPoint bottomLeft) {
        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair("start_lat", "" + topLeft.getLatitudeE6() / 1000000.0));
        params.add(new BasicNameValuePair("start_long", "" + topLeft.getLongitudeE6() / 1000000.0));
        params.add(new BasicNameValuePair("end_lat", "" + bottomLeft.getLatitudeE6() / 1000000.0));
        params.add(new BasicNameValuePair("end_long", "" + bottomLeft.getLongitudeE6() / 1000000.0));

        String jsonResult = UrlHelper.readTextFromUrlWithParams(STOPS_URL, params);

        return processJSON(jsonResult);
    }

    public static List<MapStop> processJSON(String jsonText) {
        List<MapStop> stops = new ArrayList<MapStop>();
        try {
            JSONObject json = new JSONObject(jsonText);
            JSONArray stopsArray = json.getJSONArray(STOPS);
            for (int i = 0; i < stopsArray.length(); i++) {
                JSONObject stopJSON = stopsArray.getJSONObject(i);

                stops.add(new MapStop(stopJSON.getString(STOP_NUMBER), stopJSON.getString(STOP_NAME), stopJSON.getDouble(LAT), stopJSON.getDouble(LONG)));
            }
        } catch (JSONException e) {
            Log.e("[StopsHelper.processJSON]", e.getMessage(), e);
        }

        return stops;
    }
}
