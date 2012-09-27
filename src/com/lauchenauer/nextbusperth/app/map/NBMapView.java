package com.lauchenauer.nextbusperth.app.map;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.google.android.maps.MapView;

public class NBMapView extends MapView {
    private int oldZoomLevel = -1;
    private OnMapViewChangedListener listener;

    public NBMapView(Context context, String s) {
        super(context, s);
    }

    public NBMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NBMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getZoomLevel() != oldZoomLevel) {
            triggerOnMapViewChangedListener();
            oldZoomLevel = getZoomLevel();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            triggerOnMapViewChangedListener();
        }

        return super.onTouchEvent(ev);
    }

    public void setOnMapViewChangedListener(OnMapViewChangedListener listener) {
        this.listener = listener;
    }

    private void triggerOnMapViewChangedListener() {
        if (listener == null) return;

        listener.onMapViewChanged(getZoomLevel(), getProjection().fromPixels(0, 0), getProjection().fromPixels(getWidth(), getHeight()));
    }
}
