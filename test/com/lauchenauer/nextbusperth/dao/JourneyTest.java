package com.lauchenauer.nextbusperth.dao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDao;

public class JourneyTest extends AbstractDaoTestLongPk<JourneyDao, Journey> {

    public JourneyTest() {
        super(JourneyDao.class);
    }

    @Override
    protected Journey createEntity(Long key) {
        Journey entity = new Journey();
        entity.setId(key);
        return entity;
    }

}
