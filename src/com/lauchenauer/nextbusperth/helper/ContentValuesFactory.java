package com.lauchenauer.nextbusperth.helper;

import android.content.ContentValues;
import com.lauchenauer.nextbusperth.model.JourneyRoute;
import com.lauchenauer.nextbusperth.model.Route;
import com.lauchenauer.nextbusperth.model.Stop;
import com.lauchenauer.nextbusperth.model.StopTime;

public class ContentValuesFactory implements DBConstants {
    public static ContentValues getContentValues(Object o) {
        ContentValues values = new ContentValues();

        if (o.getClass() == Route.class) {
            addValuesForRoute(values, (Route) o);
        } else if (o.getClass() == Stop.class) {
            addValuesForStop(values, (Stop) o);
        } else if (o.getClass() == StopTime.class) {
            addValuesForStopTime(values, (StopTime) o);
        } else if (o.getClass() == JourneyRoute.class) {
            addValuesForJourneyRoute(values, (JourneyRoute) o);
        }

        return values;
    }

    private static void addValuesForRoute(ContentValues values, Route r) {
        values.put(STOP_NUMBER, r.getStopNumber());
        values.put(ROUTE_NAME, r.getRouteName());
        values.put(ROUTE_NUMBER, r.getRouteNumber());
        values.put(HEADSIGN, r.getHeadsign());
    }

    private static void addValuesForStop(ContentValues values, Stop s) {
        values.put(STOP_NUMBER, s.getStopNumber());
        values.put(STOP_NAME, s.getStopName());
    }

    private static void addValuesForStopTime(ContentValues values, StopTime s) {
        values.put(STOP_NUMBER, s.getStopNumber());
        values.put(ROUTE_NUMBER, s.getRouteNumber());
        values.put(DEPARTURE_TIME, s.getDepartureTime());
    }

    private static void addValuesForJourneyRoute(ContentValues values, JourneyRoute j) {
        values.put(STOP_NUMBER, j.getStopNumber());
        values.put(ROUTE_NUMBER, j.getRouteNumber());
        values.put(JOURNEY_NAME, j.getJourneyName());
        values.put(SELECTED, j.isSelected());
    }
}
