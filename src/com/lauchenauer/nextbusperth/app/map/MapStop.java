package com.lauchenauer.nextbusperth.app.map;

public class MapStop {
    private String stopNumber;
    private String stopName;
    private double latitude;
    private double longitude;

    public MapStop(String stopNumber, String stopName, double latitude, double longitude) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "MapStop: " + stopNumber + " - " + stopName + "   " + latitude + " / " + longitude;
    }
}
