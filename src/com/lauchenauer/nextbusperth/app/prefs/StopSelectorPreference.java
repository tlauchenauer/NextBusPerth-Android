package com.lauchenauer.nextbusperth.app.prefs;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class StopSelectorPreference extends Preference {
    public StopSelectorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
    }
}
