package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;

public class Route {
    private String stopNumber;
    private String routeNumber;
    private String routeName;

    public Route(String stopNumber, String routeNumber, String routeName) {
        this.stopNumber = stopNumber;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
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

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put("stop_number", stopNumber);
        values.put("route_name", routeName);
        values.put("route_number", routeNumber);

        return values;
    }
}
