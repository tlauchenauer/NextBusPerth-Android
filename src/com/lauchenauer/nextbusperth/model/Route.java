package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class Route extends BaseModel {
    private static final String ROUTE_NAME = "route_name";
    private static final String HEADSIGN = "headsign";

    private String stopNumber;
    private String routeNumber;
    private String routeName;
    private String headsign;

    public Route(String stopNumber, JSONObject json) throws JSONException {
        this(stopNumber, json.getString(BaseModel.ROUTE_NUMBER), json.getString(ROUTE_NAME), json.getString(HEADSIGN));
    }

    public Route(String stopNumber, String routeNumber, String routeName, String headsign) {
        this.stopNumber = stopNumber;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.headsign = headsign;

        Log.d("[Route] - created", stopNumber + " : route : " + routeNumber + " : " + routeName + " : " + headsign);
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getHeadsign() {
        return headsign;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(STOP_NUMBER, stopNumber);
        values.put(ROUTE_NAME, routeName);
        values.put(BaseModel.ROUTE_NUMBER, routeNumber);
        values.put(HEADSIGN, headsign);

        return values;
    }
}
