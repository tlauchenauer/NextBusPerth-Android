package com.lauchenauer.nextbusperth.app.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.lauchenauer.nextbusperth.R;

public class TimePreference extends DialogPreference {
    private TimePicker timePicker;
    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTE = 0;

    public TimePreference(Context context, AttributeSet attributes) {
        super(context, attributes);
        setPersistent(false);
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        timePicker = (TimePicker) view.findViewById(R.id.prefTimePicker);
        timePicker.setCurrentHour(getSharedPreferences().getInt(getKey() + ".hour", DEFAULT_HOUR));
        timePicker.setCurrentMinute(getSharedPreferences().getInt(getKey() + ".minute", DEFAULT_MINUTE));
        timePicker.setIs24HourView(true);
    }

    @Override
    protected void onDialogClosed(boolean okToSave) {
        super.onDialogClosed(okToSave);
        if (okToSave) {
            timePicker.clearFocus();
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(getKey() + ".hour", timePicker.getCurrentHour());
            editor.putInt(getKey() + ".minute", timePicker.getCurrentMinute());
            editor.commit();
        }
    }
}
