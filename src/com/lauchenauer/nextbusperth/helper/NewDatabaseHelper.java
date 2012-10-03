package com.lauchenauer.nextbusperth.helper;

import android.util.Log;

import com.lauchenauer.nextbusperth.app.NextBusApplication;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDao;
import com.lauchenauer.nextbusperth.dao.JourneyRoute;
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

    public static List<Service> getNextBuses(JourneyType journeyType, int maxResults) {
        Journey journey = getApp().getJourney(journeyType);
        StringBuilder stopNumbers = new StringBuilder("('");
        List<JourneyRoute> journeyRoutes = journey.getJourneyRouteList();
        for (JourneyRoute jr : journeyRoutes) {
            stopNumbers.append(jr.getRoute().getStop().getNumber());
            stopNumbers.append("','");
        }
        stopNumbers.deleteCharAt(stopNumbers.length() - 1);
        stopNumbers.deleteCharAt(stopNumbers.length() - 1);
        stopNumbers.append(")");

        StopTimeDao stopTimeDao = getApp().getDaoSession().getStopTimeDao();
        String  queryString = " JOIN " + RouteDao.TABLENAME + " r ON T." + StopTimeDao.Properties.Route_id.columnName + " = r." + RouteDao.Properties.Id.columnName;
                queryString += " JOIN " + StopDao.TABLENAME + " s ON r." + RouteDao.Properties.Stop_id.columnName + " = s." + StopDao.Properties.Id.columnName;
                queryString += " WHERE s." + StopDao.Properties.Number.columnName + " IN " + stopNumbers.toString() + " AND T." + StopTimeDao.Properties.Departure_time.columnName + " >= ?";
                queryString += " ORDER BY T." + StopTimeDao.Properties.Departure_time.columnName;
                queryString += " LIMIT " + maxResults;

        Date startDate = new Date(new Date().getTime() - DEPARTURE_DELTA);
        List<StopTime> stopTimes = stopTimeDao.queryRaw(queryString, ISO8601FORMAT.format(startDate));

        Log.d("BOOOOOOOOO", queryString);
        Log.d("BOOOOOOOOO", "" + stopTimes.size());
        for (StopTime st : stopTimes) {
            Log.d("TIME", st.getRoute().getStop().getNumber() + " - " + st.getRoute().getStop().getName() + " - " + st.getRoute().getNumber() + " - " + st.getRoute().getHeadsign() + " - " + st.getDeparture_time());
        }


//            SQLiteDatabase database = getDatabase();
//            Cursor cursor = null;
//            ArrayList<Service> services = new ArrayList<Service>();
//            try {
//                String queryString = "SELECT s.stop_number, s.stop_name, r.route_number, r.route_name, r.headsign, st.departure_time";
//                queryString += " FROM " + TBL_STOPS + " s";
//                queryString += " JOIN " + TBL_ROUTES + " r ON s.stop_number = r.stop_number";
//                queryString += " JOIN " + TBL_STOP_TIMES + " st ON s.stop_number = st.stop_number AND r.route_number = st.route_number";
//                queryString += " WHERE s.stop_number = ? AND st.departure_time >= ?";
//                queryString += " ORDER BY st.departure_time";
//                queryString += " LIMIT " + maxResults;
//
//                Date startDate = new Date(new Date().getTime() - DEPARTURE_DELTA);
//                cursor = database.rawQuery(queryString, new String[]{stopNumber, ISO8601FORMAT.format(startDate)});
//                while (cursor.moveToNext()) {
//                    Service s = new Service(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), ISO8601FORMAT.parse(cursor.getString(5)));
//                    services.add(s);
//                }
//            } catch (ParseException e) {
//                Log.e("[DatabaseHelper.getNextBuses]", e.getMessage(), e);
//            } catch (SQLiteException e) {
//                Log.e("[DatabaseHelper.getNextBuses]", e.getMessage(), e);
//            } finally {
//                if (cursor != null) cursor.close();
//                database.close();
//            }

            return null;
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
