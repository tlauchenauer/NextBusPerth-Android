package com.lauchenauer.nextbusperth.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.Menu;

import java.util.List;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.JourneyDefaultFor;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;

import static com.lauchenauer.nextbusperth.dao.JourneyDefaultFor.*;

public class NextBusActivity extends FragmentActivity {
    private ViewPager viewPager;
    private NextBusFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextbus);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new NextBusFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

//        if (settingsHelper.isFirstRun()) {
//            Log.d("[NextBusActivity]", "firstRun - starting Alarm");
//            OnBootReceiver.startTimeTableAlarm(getApplicationContext());
//        }

        showPageByDefault();
    }

    private void showPageByDefault() {
        Time dtNow = new Time();
        dtNow.setToNow();
        int hours = dtNow.hour;

        JourneyDefaultFor timePeriod = am;
        if (hours >= 12) {
            timePeriod = pm;
        }

        Journey j = DatabaseHelper.findJourneyByDefaultFor(timePeriod);
        if (j == null) return;

        setPage(adapter.getPositionFor(j));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.refreshData();
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

    private static class NextBusFragmentAdapter extends FragmentStatePagerAdapter {
        private List<Journey> journeys;

        public NextBusFragmentAdapter(FragmentManager fm) {
            super(fm);
            journeys = DatabaseHelper.getAllJourneys();
        }

        @Override
        public int getCount() {
            return journeys.size();
        }

        @Override
        public Fragment getItem(int position) {
            boolean hasPrev = position != 0;
            boolean hasNext = position < journeys.size() - 1;

            return NextBusFragment.newInstance(journeys.get(position).getId(), hasPrev, hasNext, position);
        }

        public void refreshData() {
            journeys = DatabaseHelper.getAllJourneys();
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public int getPositionFor(Journey journey) {
            return journeys.indexOf(journey);
        }
    }
}
