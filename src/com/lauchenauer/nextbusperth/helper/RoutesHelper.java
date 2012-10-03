package com.lauchenauer.nextbusperth.helper;

import android.util.Log;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.lauchenauer.nextbusperth.app.NextBusApplication.JourneyType;
import static com.lauchenauer.nextbusperth.app.NextBusApplication.getApp;
import static com.lauchenauer.nextbusperth.helper.NewDatabaseHelper.getOrInsertRoute;
import static com.lauchenauer.nextbusperth.helper.NewDatabaseHelper.getOrInsertStop;

public class RoutesHelper implements JSONConstants {
    private static final String STOPS_URL = "routes/";

    private DaoSession daoSession;

    public RoutesHelper() {
        daoSession = getApp().getDaoSession();
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

    public void clearJourneyRoutesFromDatabase(JourneyType journeyType) {
        JourneyRouteDao journeyRouteDao = daoSession.getJourneyRouteDao();
        journeyRouteDao.queryBuilder().where(JourneyRouteDao.Properties.Journey_id.eq(getJourney(journeyType).getId())).buildDelete().executeDeleteWithoutDetachingEntities();
        getJourney(journeyType).resetJourneyRouteList();
    }

    public void writeJourneyRoutesToDatabase(JourneyType journeyType, List<Route> routes) {
        Journey journey = getJourney(journeyType);
        JourneyRouteDao journeyRouteDao = daoSession.getJourneyRouteDao();

        for (Route r : routes) {
            JourneyRoute jr = new JourneyRoute(null, journey.getId(), r.getId(), true);
            journeyRouteDao.insert(jr);
        }
    }

    public List<JourneyRoute> getJourneyRoutes(JourneyType journeyType) {
        JourneyRouteDao journeyRouteDao = daoSession.getJourneyRouteDao();
        return journeyRouteDao.queryBuilder().where(JourneyRouteDao.Properties.Journey_id.eq(getJourney(journeyType).getId())).list();
    }

    public List<Route> getSelectedRoutes(JourneyType journeyType) {
        List<JourneyRoute> journeyRoutes = getJourneyRoutes(journeyType);

        List<Route> routes = new ArrayList<Route>();
        for (JourneyRoute jr : journeyRoutes) {
            if (jr.getSelected()) {
                routes.add(jr.getRoute());
            }
        }

        return routes;
    }

    private Journey getJourney(JourneyType journeyType) {
        return getApp().getJourney(journeyType);
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
