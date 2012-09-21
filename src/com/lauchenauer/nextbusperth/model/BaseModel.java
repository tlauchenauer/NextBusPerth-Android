package com.lauchenauer.nextbusperth.model;

import android.content.ContentValues;

public abstract class BaseModel {
    static final String STOP_NUMBER = "stop_number";
    static final String ROUTE_NUMBER = "route_number";

    public abstract ContentValues getContentValues();
}
