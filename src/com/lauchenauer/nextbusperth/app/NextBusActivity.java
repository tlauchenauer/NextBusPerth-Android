package com.lauchenauer.nextbusperth.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.Menu;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.helper.SettingsHandler;

public class NextBusActivity extends FragmentActivity {
    private ViewPager viewPager;
    private SettingsHandler settingsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        settingsHandler = new SettingsHandler(getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new NextBusFragmentAdapter(getSupportFragmentManager()));

        long splitTime = settingsHandler.getSplitTime();
        Time t = new Time();
        t.setToNow();
        long currentTime = t.hour * 12 + t.minute / 5;
        if (currentTime > splitTime) {
            viewPager.setCurrentItem(1);
        }

//        if (settingsHandler.isFirstRun()) {
//            Log.d("[NextBusActivity]", "firstRun - starting Alarm");
//            OnBootReceiver.startTimeTableAlarm(getApplicationContext());
//        }
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
