package com.lauchenauer.nextbusperth.app.prefs;

import android.content.Context;
import android.preference.CheckBoxPreference;

import com.lauchenauer.nextbusperth.dao.JourneyRoute;

public class JourneyRouteCheckBoxPreference extends CheckBoxPreference {
    private JourneyRoute journeyRoute;

    public JourneyRouteCheckBoxPreference(Context context, JourneyRoute journeyRoute) {
        super(context);
        this.journeyRoute = journeyRoute;
    }

    public JourneyRoute getJourneyRoute() {
        return journeyRoute;
    }
}
