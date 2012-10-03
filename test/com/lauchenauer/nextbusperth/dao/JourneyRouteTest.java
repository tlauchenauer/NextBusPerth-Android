package com.lauchenauer.nextbusperth.dao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import com.lauchenauer.nextbusperth.dao.JourneyRoute;
import com.lauchenauer.nextbusperth.dao.JourneyRouteDao;

public class JourneyRouteTest extends AbstractDaoTestLongPk<JourneyRouteDao, JourneyRoute> {

    public JourneyRouteTest() {
        super(JourneyRouteDao.class);
    }

    @Override
    protected JourneyRoute createEntity(Long key) {
        JourneyRoute entity = new JourneyRoute();
        return entity;
    }

}
