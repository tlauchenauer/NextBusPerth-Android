package com.lauchenauer.nextbusperth.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.google.android.maps.MapView;

public class NBMapView extends MapView {
    private int oldZoomLevel = -1;

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
            Log.d("[NBMapView]", "zoom level changed to " + getZoomLevel());
            oldZoomLevel = getZoomLevel();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            Log.d("[NBMapView]", "touch event happened " + getProjection().fromPixels(0, 0).toString());
        }
        return super.onTouchEvent(ev);
    }
}
