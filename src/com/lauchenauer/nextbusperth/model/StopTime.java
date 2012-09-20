package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;

public class StopTime {
    private String stopNumber;
    private String routeNumber;
    private String departureTime;

    public StopTime(String stopNumber, String routeNumber, String departureTime) {
        this.stopNumber = stopNumber;
        this.routeNumber = routeNumber;
        this.departureTime = departureTime;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put("stop_number", stopNumber);
        values.put("route_number", routeNumber);
        values.put("departure_time", departureTime);

        return values;
    }
}
