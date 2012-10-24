package com.lauchenauer.nextbusperth.app.prefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.app.prefs.map.MapStop;
import com.lauchenauer.nextbusperth.app.prefs.map.NBMapView;
import com.lauchenauer.nextbusperth.app.prefs.map.OnMapViewChangedListener;
import com.lauchenauer.nextbusperth.app.prefs.map.StopItemOverlay;
import com.lauchenauer.nextbusperth.helper.StopsHelper;

public class StopSelectorActivity extends MapActivity implements OnMapViewChangedListener {
    private static final int ZOOM_LEVEL = 16;

    private StopItemOverlay overlay;
    private Rect lastLoadedStopsArea = new Rect(0, 0, 0, 0);
    private NBMapView mapView;
    private TextView zoomText;
    private ProgressBar progressBar;
    private MapStopsDownloadTask currentTask;
    private MapStopsDownloadTask nextTask;
    private ImageButton searchButton;
    private EditText searchText;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stop_selector);

        zoomText = (TextView) findViewById(R.id.zoom_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchButton = (ImageButton) findViewById(R.id.search_btn);
        searchText = (EditText) findViewById(R.id.search_text);

        Bundle extras = getIntent().getExtras();

        mapView = (NBMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setCenter(new GeoPoint(extras.getInt("lat"), extras.getInt("lon")));
        mapView.getController().setZoom(extras.getInt("zoom"));
        mapView.setOnMapViewChangedListener(this);

        Drawable bus_stop = this.getResources().getDrawable(R.drawable.green_pin);
        overlay = new StopItemOverlay(bus_stop, this);
        mapView.getOverlays().add(overlay);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // -31.379434,115.301102
                // -33.045508,116.682632
                Geocoder geocoder = new Geocoder(StopSelectorActivity.this);
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                    List<Address> addresses = geocoder.getFromLocationName(searchText.getText().toString(), 10, -33.045508, 115.301102, -31.379434, 116.682632);
                    final List<String> addressTexts = new ArrayList<String>(addresses.size());
                    for (Address a : addresses) {
                        Log.d("Address", a.toString());
                        addressTexts.add(a.getAddressLine(0) + ", " + a.getAddressLine(1));
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(StopSelectorActivity.this);
                    builder.setTitle("Select address");
                    builder.setItems(addressTexts.toArray(new String[1]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("SELECTED", addressTexts.get(i));
                        }
                    });
                    builder.create().show();
                } catch (IOException e) {
                    Log.d("EXCEPTION", e.getMessage(), e);
                }
            }
        });
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onMapViewChanged(int zoomlevel, GeoPoint topLeft, GeoPoint bottomRight) {
        Log.d("[StopSelectorActivity.onMapViewChanged]", "Zoom: " + zoomlevel + "   TL: " + topLeft.toString() + "   BR: " + bottomRight.toString());

        Rect newArea = createRect(topLeft, bottomRight);
        if (lastLoadedStopsArea.contains(newArea)) {
            return;
        }

        if (zoomlevel >= ZOOM_LEVEL) {
            zoomText.setVisibility(View.INVISIBLE);

            if (currentTask == null) {
                progressBar.setVisibility(View.VISIBLE);
                currentTask = new MapStopsDownloadTask(topLeft, bottomRight);
                currentTask.execute();
            } else {
                nextTask = new MapStopsDownloadTask(topLeft, bottomRight);
            }

            lastLoadedStopsArea = newArea;
        } else {
            zoomText.setVisibility(View.VISIBLE);
            updateOverlays(new ArrayList<MapStop>());
        }
    }

    private Rect createRect(GeoPoint topLeft, GeoPoint bottomRight) {
        int left = topLeft.getLatitudeE6();
        int right = bottomRight.getLatitudeE6();
        if (left > right) {
            left = bottomRight.getLatitudeE6();
            right = topLeft.getLatitudeE6();
        }

        int top = topLeft.getLongitudeE6();
        int bottom = bottomRight.getLongitudeE6();
        if (top > bottom) {
            top = bottomRight.getLongitudeE6();
            bottom = topLeft.getLongitudeE6();
        }

        return new Rect(left, top, right, bottom);
    }

    private void updateOverlays(List<MapStop> stops) {
        List<OverlayItem> items = new ArrayList<OverlayItem>(stops.size());
        for (MapStop stop : stops) {
            OverlayItem item = new OverlayItem(new GeoPoint((int) (stop.getLatitude() * 1000000), (int) (stop.getLongitude() * 1000000)), stop.getStopNumber(), stop.getStopName());
            items.add(item);
        }

        overlay.clearOverlays();

        // only add items if zoom level is correct (in case it is called out of order by a task completeing after a zoom out event
        if (mapView.getZoomLevel() >= ZOOM_LEVEL) {
            overlay.addOverlays(items);
        }

        progressBar.setVisibility(View.INVISIBLE);
        mapView.invalidate();
    }

    private synchronized void taskDone() {
        currentTask = null;

        if (nextTask != null) {
            currentTask = nextTask;
            nextTask = null;

            progressBar.setVisibility(View.VISIBLE);
            currentTask.execute();
        }
    }

    private class MapStopsDownloadTask extends AsyncTask<Void, Void, List<MapStop>> {
        private GeoPoint topLeft;
        private GeoPoint bottomRight;

        private MapStopsDownloadTask(GeoPoint topLeft, GeoPoint bottomRight) {
            this.topLeft = topLeft;
            this.bottomRight = bottomRight;
        }

        @Override
        protected List<MapStop> doInBackground(Void... voids) {
            return StopsHelper.retrieveStops(topLeft, bottomRight);
        }

        @Override
        protected void onPostExecute(List<MapStop> mapStops) {
            StopSelectorActivity.this.updateOverlays(mapStops);
            StopSelectorActivity.this.taskDone();
        }
    }
}
