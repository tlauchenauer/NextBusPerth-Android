package com.lauchenauer.nextbusperth.helper;

import com.lauchenauer.nextbusperth.app.NextBusApplication;
import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.RouteDao;
import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;
import com.lauchenauer.nextbusperth.dao.StopTime;
import com.lauchenauer.nextbusperth.dao.StopTimeDao;

import java.util.Date;

import static com.lauchenauer.nextbusperth.app.NextBusApplication.*;

public class NewDatabaseHelper {
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
}
