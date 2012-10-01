package com.lauchenauer.nextbusperth.dao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import com.lauchenauer.nextbusperth.dao.Route;
import com.lauchenauer.nextbusperth.dao.RouteDao;

public class RouteTest extends AbstractDaoTestLongPk<RouteDao, Route> {

    public RouteTest() {
        super(RouteDao.class);
    }

    @Override
    protected Route createEntity(Long key) {
        Route entity = new Route();
        entity.setId(key);
        return entity;
    }

}
