package com.lauchenauer.nextbusperth.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import java.util.List;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;

public class NextBusActivity extends FragmentActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextbus);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new NextBusFragmentAdapter(getSupportFragmentManager()));

//        if (settingsHelper.isFirstRun()) {
//            Log.d("[NextBusActivity]", "firstRun - starting Alarm");
//            OnBootReceiver.startTimeTableAlarm(getApplicationContext());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("resuming", "will need to rebuild fragments if journeys were added");
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
    }
}
