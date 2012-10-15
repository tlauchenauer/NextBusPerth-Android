package com.lauchenauer.nextbusperth.app.prefs.map;

import com.google.android.maps.GeoPoint;

public interface OnMapViewChangedListener {
    public void onMapViewChanged(int zoomlevel, GeoPoint topLeft, GeoPoint bottomRight);
}
