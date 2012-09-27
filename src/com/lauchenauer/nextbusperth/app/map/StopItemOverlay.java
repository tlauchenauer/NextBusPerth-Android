package com.lauchenauer.nextbusperth.app.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class StopItemOverlay extends ItemizedOverlay implements DialogInterface.OnClickListener {
    private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
    private Context context;
    private OverlayItem item;

    public StopItemOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        this.context = context;
    }
    
    public void addOverlays(List<OverlayItem> overlays) {
        this.overlays.addAll(overlays);
        populate();
    }

    public void addOverlay(OverlayItem overlay) {
        overlays.add(overlay);
        populate();
    }
    
    public void clearOverlays() {
        overlays.clear();
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return overlays.get(i);
    }

    @Override
    public int size() {
        return overlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        item = overlays.get(index);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Select this Stop?");
        dialog.setPositiveButton("Select", this);
        dialog.setNegativeButton("Cancel", null);
        dialog.setMessage(item.getSnippet());
        dialog.show();

        return true;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d("MapItem selected!!!!!", item.getSnippet());
    }
}
