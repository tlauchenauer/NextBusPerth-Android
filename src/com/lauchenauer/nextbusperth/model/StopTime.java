package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import android.util.Log;

public class StopTime extends BaseModel {
    private static final String DEPARTURE_TIME = "departure_time";

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

        values.put(STOP_NUMBER, stopNumber);
        values.put(ROUTE_NUMBER, routeNumber);
        values.put(DEPARTURE_TIME, departureTime);

        return values;
    }
}
