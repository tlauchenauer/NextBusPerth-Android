package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.util.Log;
import com.lauchenauer.nextbusperth.app.NextBusApplication;
import com.lauchenauer.nextbusperth.dao.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewRoutesHelper implements JSONConstants {
    private static final String STOPS_URL = "routes/";

    private DaoSession daoSession;

    public NewRoutesHelper() {
        daoSession = NextBusApplication.getApp().getDaoSession();
    }

    public List<Route> retrieveRoutes(String stopNumber) {
        String jsonResult = UrlHelper.readTextFromUrl(STOPS_URL + stopNumber);

        return processJSON(jsonResult);
    }

    private List<Route> processJSON(String jsonText) {
        List<Route> routes = new ArrayList<Route>();

        try {
            JSONObject json = new JSONObject(jsonText);
            JSONArray routesArray = json.getJSONArray(ROUTES);
            for (int i = 0; i < routesArray.length(); i++) {
                JSONObject stopJSON = routesArray.getJSONObject(i);

                Stop stop = getOrInsertStop(stopJSON.getString(STOP_NUMBER), stopJSON.getString(STOP_NAME));
                Route route = getOrInsertRoute(stop, stopJSON.getString(ROUTE_NUMBER), stopJSON.getString(ROUTE_NAME), stopJSON.getString(HEADSIGN));
                routes.add(route);
            }
        } catch (JSONException e) {
            Log.e("[StopsHelper.processJSON]", e.getMessage(), e);
        }

        return routes;
    }

    private Stop getOrInsertStop(String stopNumber, String stopName) {
        StopDao stopDao = daoSession.getStopDao();

        Stop stop = stopDao.queryBuilder().where(StopDao.Properties.Number.eq(stopNumber)).unique();
        if (stop == null) {
            stop = new Stop(null, stopNumber, stopName);
            stopDao.insert(stop);
        }

        return stop;
    }

    private Route getOrInsertRoute(Stop stop, String routeNumber, String routeName, String headsign) {
        RouteDao routeDao = daoSession.getRouteDao();

        Route route = routeDao.queryBuilder()
                .where(RouteDao.Properties.Stop_id.eq(stop.getId()),
                        RouteDao.Properties.Number.eq(routeNumber),
                        RouteDao.Properties.Name.eq(routeName),
                        RouteDao.Properties.Headsign.eq(headsign))
                .unique();

        if (route == null) {
            route = new Route(null, stop.getId(), routeNumber, routeName, headsign);
            routeDao.insert(route);
        }

        return route;
    }

    public void clearJourneyRoutesFromDatabase(boolean workJourney) {
        Journey journey = workJourney ? NextBusApplication.getApp().getWorkJourney() : NextBusApplication.getApp().getHomeJourney();
        JourneyRouteDao journeyRouteDao = daoSession.getJourneyRouteDao();
        journeyRouteDao.queryBuilder().where(JourneyRouteDao.Properties.Journey_id.eq(journey.getId())).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public void printData() {
        StopDao stopDao = daoSession.getStopDao();
        List<Stop> stops = stopDao.queryBuilder().list();

        for (Stop s : stops) {
            Log.d("STOP", s.getId() + ":  " + s.getNumber() + " - " + s.getName());

            for (Route r : s.getRouteList()) {
                Log.d("ROUTE", r.getId() + ":  " + r.getNumber() + " - " + r.getName() + " - " + r.getHeadsign());
            }
        }
    }
}
