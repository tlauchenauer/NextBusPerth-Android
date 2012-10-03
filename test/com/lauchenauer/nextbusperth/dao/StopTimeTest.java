package com.lauchenauer.nextbusperth.dao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import com.lauchenauer.nextbusperth.dao.StopTime;
import com.lauchenauer.nextbusperth.dao.StopTimeDao;

public class StopTimeTest extends AbstractDaoTestLongPk<StopTimeDao, StopTime> {

    public StopTimeTest() {
        super(StopTimeDao.class);
    }

    @Override
    protected StopTime createEntity(Long key) {
        StopTime entity = new StopTime();
        return entity;
    }

}
