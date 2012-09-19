package com.lauchenauer.nextbusperth;

import android.content.ContentValues;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class StopTime {
    private String stopId;
    private String name;
    private String shortName;
    private String longName;
    private String headsign;
    private String departure;

    public StopTime(String stopId, String name, String shortName, String longName, String headsign, String departure) {
        this.stopId = stopId;
        this.name = name;
        this.shortName = shortName;
        this.longName = longName;
        this.headsign = headsign;
        this.departure = departure;

        Log.d("[Created StopTime]", toString());
    }

    public StopTime(JSONObject jsonObject) throws JSONException{
        this(jsonObject.getString("stop_id"), jsonObject.getString("name"), jsonObject.getString("short_name"), jsonObject.getString("long_name"),
                jsonObject.getString("headsign"), jsonObject.getString("departure"));
    }

    public String getStopId() {
        return stopId;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getHeadsign() {
        return headsign;
    }

    public String getDeparture() {
        return departure;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put("stop_id", stopId);
        values.put("name", name);
        values.put("short_name", shortName);
        values.put("long_name", longName);
        values.put("headsign", headsign);
        values.put("departure", departure);

        return values;
    }

    @Override
    public String toString() {
        return "stop_id: '" + stopId + "', name: '" + name + "', short_name: '" + shortName + "', long_name: '" + longName + "', headsign: '" + headsign + "', departure: '" + departure + "'";
    }
}
