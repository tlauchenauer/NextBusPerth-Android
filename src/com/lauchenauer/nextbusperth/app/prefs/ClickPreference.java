package com.lauchenauer.nextbusperth.app.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ClickPreference extends DialogPreference {
    public ClickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
    }

    @Override
    protected void onClick() {
    }
}
