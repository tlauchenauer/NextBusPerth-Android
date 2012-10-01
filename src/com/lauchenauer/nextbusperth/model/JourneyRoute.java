package com.lauchenauer.nextbusperth.model;

public class JourneyRoute implements RouteJourneyPreference {
    public static final String WORK_JOURNEY = "work";
    public static final String HOME_JOURNEY = "home";

    private String stopNumber;
    private String routeNumber;
    private String journeyName;
    private String headsign;
    private boolean selected;

    public JourneyRoute(String journeyName, String stopNumber, String routeNumber, String headsign, boolean selected) {
        this.stopNumber = stopNumber;
        this.routeNumber = routeNumber;
        this.journeyName = journeyName;
        this.selected = selected;
        this.headsign = headsign;
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

    public String getHeadsign() {
        return headsign;
    }
}
