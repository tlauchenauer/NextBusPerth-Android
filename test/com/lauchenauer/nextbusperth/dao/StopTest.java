package com.lauchenauer.nextbusperth.dao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;

public class StopTest extends AbstractDaoTestLongPk<StopDao, Stop> {

    public StopTest() {
        super(StopDao.class);
    }

    @Override
    protected Stop createEntity(Long key) {
        Stop entity = new Stop();
        entity.setId(key);
        return entity;
    }

}
