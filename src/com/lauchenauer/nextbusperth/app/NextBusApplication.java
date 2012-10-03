package com.lauchenauer.nextbusperth.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.lauchenauer.nextbusperth.dao.*;

public class NextBusApplication extends Application {
    private static final String TIMETABLE_DB = "nextbus-perth-db";
    private static final String WORK_JOURNEY_NAME = "work";
    private static final String HOME_JOURNEY_NAME = "home";
    private static NextBusApplication application;

    private DaoSession daoSession;
    private Journey workJourney;
    private Journey homeJourney;

    public static NextBusApplication getApp() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, TIMETABLE_DB, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        loadSeedData();
    }

    private void loadSeedData() {
        homeJourney = getOrInsertJourney(HOME_JOURNEY_NAME);
        workJourney = getOrInsertJourney(WORK_JOURNEY_NAME);
    }

    private Journey getOrInsertJourney(String journeyName) {
        JourneyDao journeyDao = daoSession.getJourneyDao();

        Journey journey = journeyDao.queryBuilder().where(JourneyDao.Properties.Name.eq(journeyName)).unique();
        if (journey == null) {
            journey = new Journey(null, journeyName);
            journeyDao.insert(journey);
        }

        return journey;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Journey getJourney(JourneyType jt) {
        switch (jt) {
            case work:
                return workJourney;
            case home:
                return homeJourney;
        }

        return null;
    }

    public enum JourneyType {work, home}
}
