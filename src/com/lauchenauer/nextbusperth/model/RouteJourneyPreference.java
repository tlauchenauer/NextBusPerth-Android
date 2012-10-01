package com.lauchenauer.nextbusperth.model;

public interface RouteJourneyPreference {
    public String getStopNumber();
    public String getRouteNumber();
    public String getHeadsign();
    public boolean isSelected();
}
