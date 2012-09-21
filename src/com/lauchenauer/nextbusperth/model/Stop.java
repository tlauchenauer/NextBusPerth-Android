package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class Stop extends BaseModel {
    static final String STOP_NAME = "stop_name";
    private String stopNumber;
    private String stopName;

    public Stop(JSONObject json) throws JSONException {
        this(json.getString(STOP_NUMBER), json.getString(STOP_NAME));
    }

    public Stop(String stopNumber, String stopName) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        
        Log.d("[Stop] - created", stopNumber + " : " + stopName);
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(STOP_NUMBER, stopNumber);
        values.put(STOP_NAME, stopName);

        return values;
    }
}
