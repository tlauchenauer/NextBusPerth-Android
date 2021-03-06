package com.lauchenauer.nextbusperth.dao;

import com.lauchenauer.nextbusperth.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table JOURNEY_ROUTE.
 */
public class JourneyRoute {

    private Long id;
    private Long journey_id;
    private Long route_id;
    private Boolean selected;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient JourneyRouteDao myDao;

    private Journey journey;
    private Long journey__resolvedKey;

    private Route route;
    private Long route__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public JourneyRoute() {
    }

    public JourneyRoute(Long id) {
        this.id = id;
    }

    public JourneyRoute(Long id, Long journey_id, Long route_id, Boolean selected) {
        this.id = id;
        this.journey_id = journey_id;
        this.route_id = route_id;
        this.selected = selected;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getJourneyRouteDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJourney_id() {
        return journey_id;
    }

    public void setJourney_id(Long journey_id) {
        this.journey_id = journey_id;
    }

    public Long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Long route_id) {
        this.route_id = route_id;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    /** To-one relationship, resolved on first access. */
    public Journey getJourney() {
        if (journey__resolvedKey == null || !journey__resolvedKey.equals(journey_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            JourneyDao targetDao = daoSession.getJourneyDao();
            journey = targetDao.load(journey_id);
            journey__resolvedKey = journey_id;
        }
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
        journey_id = journey == null ? null : journey.getId();
        journey__resolvedKey = journey_id;
    }

    /** To-one relationship, resolved on first access. */
    public Route getRoute() {
        if (route__resolvedKey == null || !route__resolvedKey.equals(route_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RouteDao targetDao = daoSession.getRouteDao();
            route = targetDao.load(route_id);
            route__resolvedKey = route_id;
        }
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
        route_id = route == null ? null : route.getId();
        route__resolvedKey = route_id;
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
