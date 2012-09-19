package com.lauchenauer.nextbusperth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class NextBusActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextbus);

        Log.d("[NextBus - MAIN]", "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("[NextBus - MAIN]", "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Settings").setIcon(android.R.drawable.ic_menu_preferences).setIntent(new Intent(this, SettingsActivity.class));
        menu.add("About").setIcon(android.R.drawable.ic_menu_info_details).setIntent(new Intent(this, AboutActivity.class));

        return true;
    }
}
