package com.lauchenauer.nextbusperth.dao;

import java.util.List;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table ROUTE.
 */
public class Route {

    private Long id;
    private Long stop_id;
    private String number;
    private String name;
    private String headsign;
    private Long route_id;
    private java.util.Date departure_time;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient RouteDao myDao;

    private Stop stop;
    private Long stop__resolvedKey;

    private List<StopTime> stopTimeList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Route() {
    }

    public Route(Long id) {
        this.id = id;
    }

    public Route(Long id, Long stop_id, String number, String name, String headsign, Long route_id, java.util.Date departure_time) {
        this.id = id;
        this.stop_id = stop_id;
        this.number = number;
        this.name = name;
        this.headsign = headsign;
        this.route_id = route_id;
        this.departure_time = departure_time;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRouteDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStop_id() {
        return stop_id;
    }

    public void setStop_id(Long stop_id) {
        this.stop_id = stop_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public Long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Long route_id) {
        this.route_id = route_id;
    }

    public java.util.Date getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(java.util.Date departure_time) {
        this.departure_time = departure_time;
    }

    /** To-one relationship, resolved on first access. */
    public Stop getStop() {
        if (stop__resolvedKey == null || !stop__resolvedKey.equals(stop_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StopDao targetDao = daoSession.getStopDao();
            stop = targetDao.load(stop_id);
            stop__resolvedKey = stop_id;
        }
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
        stop_id = stop == null ? null : stop.getId();
        stop__resolvedKey = stop_id;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<StopTime> getStopTimeList() {
        if (stopTimeList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StopTimeDao targetDao = daoSession.getStopTimeDao();
            stopTimeList = targetDao._queryRoute_StopTimeList(id);
        }
        return stopTimeList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetStopTimeList() {
        stopTimeList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
