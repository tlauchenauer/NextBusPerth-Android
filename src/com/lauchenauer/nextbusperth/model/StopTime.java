package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import com.lauchenauer.nextbusperth.helper.JSONConstants;

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
}
