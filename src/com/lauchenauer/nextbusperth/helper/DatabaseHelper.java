package com.lauchenauer.nextbusperth.helper;

import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDao;
import com.lauchenauer.nextbusperth.dao.JourneyDefaultFor;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.RouteDao;
import com.lauchenauer.nextbusperth.dao.Service;
import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;
import com.lauchenauer.nextbusperth.dao.StopTime;
import com.lauchenauer.nextbusperth.dao.StopTimeDao;

import static com.lauchenauer.nextbusperth.app.NextBusApplication.getApp;
import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.am;
import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.pm;

public class DatabaseHelper {
    private static final long DEPARTURE_DELTA = 5 * 60 * 1000l;

    public static Journey addNewJourney(String journeyName) {
        Journey journey = new Journey(null, journeyName, "", "", 0, 0, JourneyDefaultFor.none, DatabaseHelper.getJourneysCount());

        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();
        journeyDao.insert(journey);

        return journey;
    }

    public static Journey getOrInsertJourney(String journeyName, String stop_number, String stop_name, Integer stop_lat, Integer stop_lon, JourneyDefaultFor default_for) {
        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();

        Journey journey = journeyDao.queryBuilder().where(JourneyDao.Properties.Name.eq(journeyName)).unique();
        if (journey == null) {
            journey = new Journey(null, journeyName, stop_number, stop_name, stop_lat, stop_lon, default_for, getJourneysCount());
            journeyDao.insert(journey);
        }

        return journey;
    }

    public static void updateJourney(Journey journey) {
        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();
        journeyDao.update(journey);
    }

    public static Journey getJourneyById(long id) {
        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();
        return journeyDao.queryBuilder().where(JourneyDao.Properties.Id.eq(id)).unique();
    }

    public static Journey findCurrentDefaultJourney() {
        Time dtNow = new Time();
        dtNow.setToNow();
        int hours = dtNow.hour;

        JourneyDefaultFor timePeriod = am;
        if (hours >= 12) {
            timePeriod = pm;
        }

        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();
        return journeyDao.queryBuilder().where(JourneyDao.Properties.Default_for.eq(timePeriod.getId())).unique();
    }

    public static List<Journey> getAllJourneys() {
        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();
        return journeyDao.queryBuilder().orderAsc(JourneyDao.Properties.Position).list();
    }

    public static int getJourneysCount() {
        JourneyDao journeyDao = getApp().getDaoSession().getJourneyDao();
        return (int) journeyDao.queryBuilder().count();
    }

    public static Stop getOrInsertStop(String stopNumber, String stopName) {
        StopDao stopDao = getApp().getDaoSession().getStopDao();
        Stop stop = stopDao.queryBuilder().where(StopDao.Properties.Number.eq(stopNumber)).unique();
        if (stop == null) {
            stop = new Stop(null, stopNumber, stopName);
            stopDao.insert(stop);
        }

        return stop;
    }

    public static Route getOrInsertRoute(Stop stop, String routeNumber, String routeName, String headsign) {
        RouteDao routeDao = getApp().getDaoSession().getRouteDao();
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

    public static StopTime getOrInsertStopTime(Route route, Date departureTime) {
        StopTimeDao stopTimeDao = getApp().getDaoSession().getStopTimeDao();
        StopTime stopTime = stopTimeDao.queryBuilder()
                .where(StopTimeDao.Properties.Route_id.eq(route.getId()),
                        StopTimeDao.Properties.Departure_time.eq(departureTime))
                .unique();
        if (stopTime == null) {
            stopTime = new StopTime(null, route.getId(), departureTime);
            stopTimeDao.insert(stopTime);
        }

        return stopTime;
    }

    public static List<Service> getNextBuses(Journey journey, int maxResults) {
        StopTimeDao stopTimeDao = getApp().getDaoSession().getStopTimeDao();
        String queryString = " JOIN " + RouteDao.TABLENAME + " r ON T." + StopTimeDao.Properties.Route_id.columnName + " = r." + RouteDao.Properties.Id.columnName;
        queryString += " JOIN " + JourneyRouteDao.TABLENAME + " jr ON r." + RouteDao.Properties.Id.columnName + " = jr." + JourneyRouteDao.Properties.Route_id.columnName;
        queryString += " JOIN " + JourneyDao.TABLENAME + " j ON jr." + JourneyRouteDao.Properties.Journey_id.columnName + " = j." + JourneyDao.Properties.Id.columnName;
        queryString += " WHERE j." + JourneyDao.Properties.Id.columnName + " = ?";
        queryString += " AND jr." + JourneyRouteDao.Properties.Selected.columnName + " = ?";
        queryString += " AND T." + StopTimeDao.Properties.Departure_time.columnName + " >= ?";
        queryString += " ORDER BY T." + StopTimeDao.Properties.Departure_time.columnName;
        queryString += " LIMIT " + maxResults;

        Date startDate = new Date(new Date().getTime() - DEPARTURE_DELTA);
        List<StopTime> stopTimes = stopTimeDao.queryRaw(queryString, journey.getId().toString(), "1", "" + startDate.getTime());


        ArrayList<Service> services = new ArrayList<Service>();
        for (StopTime st : stopTimes) {
            Route route = st.getRoute();
            Stop stop = route.getStop();
            services.add(new Service(stop.getNumber(), stop.getName(), route.getNumber(), route.getName(), route.getHeadsign(), st.getDeparture_time()));
        }

        return services;
    }

    public static void printData() {
        DaoSession daoSession = getApp().getDaoSession();

        JourneyDao journeyDao = daoSession.getJourneyDao();
        List<Journey> journeys = journeyDao.queryBuilder().list();

        for (Journey j : journeys) {
            Log.d("JOURNEY", j.getId() + " - " + j.getName());

            for (JourneyRoute jr : j.getJourneyRouteList()) {
                Log.d("JOURNEYROUTE", jr.getRoute().getNumber() + " - " + jr.getRoute().getHeadsign() + " - " + jr.getSelected());
            }
        }

        StopDao stopDao = daoSession.getStopDao();
        List<Stop> stops = stopDao.queryBuilder().list();

        for (Stop s : stops) {
            Log.d("STOP", s.getId() + ":  " + s.getNumber() + " - " + s.getName());

            for (Route r : s.getRouteList()) {
                Log.d("ROUTE", r.getId() + ":  " + r.getNumber() + " - " + r.getName() + " - " + r.getHeadsign());

                for (StopTime st : r.getStopTimeList()) {
                    Log.d("STOPTIME", st.getId() + " - " + st.getDeparture_time());
                }
            }
        }
    }

    public static void deleteJourneyAndJourneyRoutes(Journey journey) {
        DaoSession daoSession = getApp().getDaoSession();
        JourneyDao journeyDao = daoSession.getJourneyDao();
        JourneyRouteDao journeyRouteDao = daoSession.getJourneyRouteDao();

        journey.resetJourneyRouteList();
        for (JourneyRoute jr : journey.getJourneyRouteList()) {
            journeyRouteDao.delete(jr);
        }
        journeyDao.delete(journey);
    }
}
