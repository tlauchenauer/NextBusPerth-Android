package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import com.lauchenauer.nextbusperth.helper.JSONConstants;

public class JourneyRoute {
    private String stopNumber;
    private String routeNumber;
    private String journeyName;
    private boolean selected;

    public JourneyRoute(String journeyName, String stopNumber, String routeNumber, boolean selected) {
        this.stopNumber = stopNumber;
        this.routeNumber = routeNumber;
        this.journeyName = journeyName;
        this.selected = selected;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getJourneyName() {
        return journeyName;
    }

    public boolean isSelected() {
        return selected;
    }
}
