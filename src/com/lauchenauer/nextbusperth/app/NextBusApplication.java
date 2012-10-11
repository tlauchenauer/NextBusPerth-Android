package com.lauchenauer.nextbusperth.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.lauchenauer.nextbusperth.dao.DaoMaster;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDefaultFor;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;

public class NextBusApplication extends Application {
    private static final String TIMETABLE_DB = "nextbus-perth-db";
    private static NextBusApplication application;

    private DaoSession daoSession;
    private boolean firstRun = false;

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
        if (DatabaseHelper.getJourneysCount() == 0) {
            firstRun = true;
        }

        if (firstRun) {
            DatabaseHelper.getOrInsertJourney("Work", "", "", 0, 0, JourneyDefaultFor.am);
            DatabaseHelper.getOrInsertJourney("Home", "", "", 0, 0, JourneyDefaultFor.pm);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
