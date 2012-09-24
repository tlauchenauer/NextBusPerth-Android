package com.lauchenauer.nextbusperth.app;

import android.os.Bundle;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.lauchenauer.nextbusperth.R;

public class StopSelectorActivity extends MapActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stop_selector);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
