package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.util.Log;
import com.lauchenauer.nextbusperth.app.NextBusApplication;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.RouteDao;
import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;
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

                Stop stop = getOrInsertStop(stopJSON.getString(STOP_NUMBER), "need to pass this along with the JSON");
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
            stop = new Stop(null, "523456", "My new brilliant new stop");
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

    public void writeRoutesToDatabase(List<Route> routes) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            for (Route route : routes) {
                dbHelper.writeModelToDB(route, database);
            }
        } finally {
            database.close();
        }
    }

    public void clearJourneyRoutesFromDatabase(boolean workJourney) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            dbHelper.deleteFromDB(JourneyRoute.class, "journey_name = ?", new String[]{getJourneyName(workJourney)}, database);
        } finally {
            database.close();
        }
    }

    public void writeJourneyRoutesToDatabase(boolean workJourney, List<Route> routes) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getDatabase();

        try {
            for (Route r : routes) {
                JourneyRoute jr = new JourneyRoute(getJourneyName(workJourney), r.getStopNumber(), r.getRouteNumber(), r.getHeadsign(), true);
                dbHelper.writeModelToDB(jr, database);
            }
        } finally {
            database.close();
        }
    }

    private String getJourneyName(boolean workJourney) {
        return workJourney ? JourneyRoute.WORK_JOURNEY : JourneyRoute.HOME_JOURNEY;
    }
}
