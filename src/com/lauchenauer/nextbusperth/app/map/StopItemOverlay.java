package com.lauchenauer.nextbusperth.app.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class StopItemOverlay extends ItemizedOverlay implements DialogInterface.OnClickListener {
    private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
    private OverlayItem item;
    private Activity parentActivity;

    public StopItemOverlay(Drawable defaultMarker, Activity parentActivity) {
        super(boundCenterBottom(defaultMarker));
        this.parentActivity = parentActivity;
        populate();  // workaround for the NPE when no items present
    }

    public void addOverlays(List<OverlayItem> overlays) {
        this.overlays.addAll(overlays);
        setLastFocusedIndex(-1);  // Workaround for the ArrayIndexOutOfBoundExcepttion
        populate();
    }

    public void addOverlay(OverlayItem overlay) {
        overlays.add(overlay);
        setLastFocusedIndex(-1);  // Workaround for the ArrayIndexOutOfBoundExcepttion
        populate();
    }

    public void clearOverlays() {
        overlays.clear();
        setLastFocusedIndex(-1);  // Workaround for the ArrayIndexOutOfBoundExcepttion
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(parentActivity);
        dialog.setTitle("Select this Stop?");
        dialog.setPositiveButton("Select", this);
        dialog.setNegativeButton("Cancel", null);
        dialog.setMessage("Stop Number: " + item.getTitle() + " - " + item.getSnippet());
        dialog.show();

        return true;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d("MapItem selected!!!!!", item.getSnippet());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("stop_number", item.getTitle());
        parentActivity.setResult(Activity.RESULT_OK, resultIntent);
        parentActivity.finish();
    }
}
