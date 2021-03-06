package com.lauchenauer.nextbusperth.dao;

import java.util.List;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table JOURNEY.
 */
public class Journey {

    private Long id;
    private String name;
    private String stop_number;
    private String stop_name;
    private Integer stop_lat;
    private Integer stop_lon;
    private Integer default_for;
    private Integer position;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient JourneyDao myDao;

    private List<JourneyRoute> journeyRouteList;

    // KEEP FIELDS - put your custom fields here
    public Journey(Long id, String name, String stop_number, String stop_name, int stop_lat, int stop_lon, JourneyDefaultFor default_for, int position) {
        this(id, name, stop_number, stop_name, stop_lat, stop_lon, default_for.getId(), position);
    }
    // KEEP FIELDS END

    public Journey() {
    }

    public Journey(Long id) {
        this.id = id;
    }

    public Journey(Long id, String name, String stop_number, String stop_name, Integer stop_lat, Integer stop_lon, Integer default_for, Integer position) {
        this.id = id;
        this.name = name;
        this.stop_number = stop_number;
        this.stop_name = stop_name;
        this.stop_lat = stop_lat;
        this.stop_lon = stop_lon;
        this.default_for = default_for;
        this.position = position;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getJourneyDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStop_number() {
        return stop_number;
    }

    public void setStop_number(String stop_number) {
        this.stop_number = stop_number;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public Integer getStop_lat() {
        return stop_lat;
    }

    public void setStop_lat(Integer stop_lat) {
        this.stop_lat = stop_lat;
    }

    public Integer getStop_lon() {
        return stop_lon;
    }

    public void setStop_lon(Integer stop_lon) {
        this.stop_lon = stop_lon;
    }

    public Integer getDefault_for() {
        return default_for;
    }

    public void setDefault_for(Integer default_for) {
        this.default_for = default_for;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<JourneyRoute> getJourneyRouteList() {
        if (journeyRouteList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            JourneyRouteDao targetDao = daoSession.getJourneyRouteDao();
            journeyRouteList = targetDao._queryJourney_JourneyRouteList(id);
        }
        return journeyRouteList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetJourneyRouteList() {
        journeyRouteList = null;
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

    @Override
    public int hashCode() {
        return getId().intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Journey.class) {
            return ((Journey)o).getId() == getId();
        }

        return false;
    }

    // KEEP METHODS END

}
