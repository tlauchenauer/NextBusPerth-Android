package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import org.json.JSONObject;

public class Stop {
    private String stopNumber;
    private String stopName;

    public Stop(JSONObject json) {

    }

    public Stop(String stopNumber, String stopName) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put("stop_number", stopNumber);
        values.put("stop_name", stopName);

        return values;
    }
}
