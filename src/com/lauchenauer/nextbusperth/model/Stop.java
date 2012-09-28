package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import com.lauchenauer.nextbusperth.helper.JSONConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class Stop {
    private String stopNumber;
    private String stopName;

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
}
