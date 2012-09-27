package com.lauchenauer.nextbusperth.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.OverlayItem;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.map.MapStop;
import com.lauchenauer.nextbusperth.app.map.NBMapView;
import com.lauchenauer.nextbusperth.app.map.OnMapViewChangedListener;
import com.lauchenauer.nextbusperth.app.map.StopItemOverlay;
import com.lauchenauer.nextbusperth.helper.StopsHelper;

import java.util.ArrayList;
import java.util.List;

public class StopSelectorActivity extends MapActivity implements OnMapViewChangedListener {
    private StopItemOverlay overlay;
    private GeoPoint lastTopLeft = new GeoPoint(0, 0);
    private NBMapView mapView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stop_selector);

        mapView = (NBMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapView.getController().setCenter(new GeoPoint(-31957406, 115851122));
        mapView.getController().setZoom(12);

        mapView.setOnMapViewChangedListener(this);
        
        Drawable bus_stop = this.getResources().getDrawable(R.drawable.pin_green);
        overlay = new StopItemOverlay(bus_stop, this);

        overlay.addOverlay(new OverlayItem(new GeoPoint(-31950918, 115857486), "Wellington St Before William St / Red And Yellow C", "Wellington St Before William St / Red And Yellow C"));
        overlay.addOverlay(new OverlayItem(new GeoPoint(-31956167, 115861310), "St Georges Tce, Stands A, B And F", "St Georges Tce, Stands A, B And F"));
        overlay.addOverlay(new OverlayItem(new GeoPoint(-31956274, 115853855), "Esplanade Busport", "Esplanade Busport"));
        overlay.addOverlay(new OverlayItem(new GeoPoint(-31950233, 115856744), "Wellington St Bus Stn", "Wellington St Bus Stn"));

        mapView.getOverlays().add(overlay);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onMapViewChanged(int zoomlevel, GeoPoint topLeft, GeoPoint bottomRight) {
        Log.d("[StopSelectorActivity.onMapViewChanged]", "Zoom: " + zoomlevel + "   TL: " + topLeft.toString() + "   BR: " + bottomRight.toString());

        boolean samePosition = lastTopLeft.getLatitudeE6() == topLeft.getLatitudeE6() && lastTopLeft.getLongitudeE6() == topLeft.getLongitudeE6();
        if (samePosition) return;
        lastTopLeft = topLeft;

        if (zoomlevel >= 16) {
            List<MapStop> stops = StopsHelper.retrieveStops(topLeft, bottomRight);
            Log.d("MapStops count", "" + stops.size());
            
            List<OverlayItem> items = new ArrayList<OverlayItem>(stops.size());
            for (MapStop stop : stops) {
                Log.d("overlay", stop.getStopNumber() + "  " + stop.getLatitude() + " / " + stop.getLongitude());
                OverlayItem item = new OverlayItem(new GeoPoint((int)(stop.getLatitude() * 1000000), (int)(stop.getLongitude() * 1000000)), stop.getStopNumber(), stop.getStopName());
                Log.d("   ", item.getPoint().toString());
                items.add(item);
            }
            
            overlay.clearOverlays();
            overlay.addOverlays(items);

//            mapView.getOverlays().clear();
//            mapView.getOverlays().add(overlay);
//            mapView.invalidate();
        }
    }
}
