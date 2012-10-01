package com.lauchenauer.nextbusperth.app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.DaoMaster;
import com.lauchenauer.nextbusperth.dao.DaoSession;
import com.lauchenauer.nextbusperth.dao.RouteDao;
import com.lauchenauer.nextbusperth.dao.Stop;
import com.lauchenauer.nextbusperth.dao.StopDao;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;
import de.greenrobot.dao.QueryBuilder;

import java.util.List;

public class NextBusActivity extends FragmentActivity {
    private ViewPager viewPager;
    private SettingsHelper settingsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "testing-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        StopDao stopDao = daoSession.getStopDao();
        RouteDao routeDao = daoSession.getRouteDao();

        List<Stop> stops = stopDao.queryBuilder().list();
        for (Stop s : stops) {
            Log.d("STOP", s.getId() + " - " + s.getNumber() + " - " + s.getName());
        }

//        stopDao.queryBuilder().where(StopDao.Properties.Number.eq("123456")).unique();
//        Stop stop = new Stop(null, "523456", "My new brilliant new stop");
//        stopDao.insert(stop);
//        Log.d("DAO Testing", "inserted stop " + stop.getId());

        settingsHelper = new SettingsHelper(getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new NextBusFragmentAdapter(getSupportFragmentManager()));

        long splitTime = settingsHelper.getSplitTime();
        Time t = new Time();
        t.setToNow();
        long currentTime = t.hour * 12 + t.minute / 5;
        if (currentTime > splitTime) {
            viewPager.setCurrentItem(1);
        }

//        if (settingsHelper.isFirstRun()) {
//            Log.d("[NextBusActivity]", "firstRun - starting Alarm");
//            OnBootReceiver.startTimeTableAlarm(getApplicationContext());
//        }
    }

    void setPage(int page) {
        viewPager.setCurrentItem(page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Settings").setIcon(android.R.drawable.ic_menu_preferences).setIntent(new Intent(this, SettingsActivity.class));
        menu.add("About").setIcon(android.R.drawable.ic_menu_info_details).setIntent(new Intent(this, AboutActivity.class));

        return true;
    }

    private static class NextBusFragmentAdapter extends FragmentPagerAdapter {
        public NextBusFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return NextBusFragment.newInstance("Work");
                default:
                    return NextBusFragment.newInstance("Home");
            }

        }
    }
}
