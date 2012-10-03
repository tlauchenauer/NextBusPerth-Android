package com.lauchenauer.nextbusperth.helper;

import android.util.Log;

import com.lauchenauer.nextbusperth.app.NextBusApplication;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDao;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.RouteDao;
import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;
import com.lauchenauer.nextbusperth.dao.StopTime;
import com.lauchenauer.nextbusperth.dao.StopTimeDao;
import com.lauchenauer.nextbusperth.model.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lauchenauer.nextbusperth.app.NextBusApplication.*;

public class NewDatabaseHelper {
    private static final SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final long DEPARTURE_DELTA = 5 * 60 * 1000l;

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
            Log.d("TIME", stop.getNumber() + " - " + stop.getName() + " - " + route.getNumber() + " - " + route.getHeadsign() + " - " + st.getDeparture_time());
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
}
