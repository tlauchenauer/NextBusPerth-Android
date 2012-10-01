package com.lauchenauer.nextbusperth.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.lauchenauer.nextbusperth.dao.DaoMaster;
import com.lauchenauer.nextbusperth.dao.DaoSession;

public class NextBusApplication extends Application {
    private static final String TIMETABLE_DB = "nextbus-perth-db";
    private static NextBusApplication application;

    private DaoSession daoSession;

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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
