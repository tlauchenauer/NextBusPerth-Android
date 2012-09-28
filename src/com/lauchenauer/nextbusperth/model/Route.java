package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import com.lauchenauer.nextbusperth.helper.JSONConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class Route {
    private String stopNumber;
    private String routeNumber;
    private String routeName;
    private String headsign;

    public Route(String stopNumber, String routeNumber, String routeName, String headsign) {
        this.stopNumber = stopNumber;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.headsign = headsign;
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
}
