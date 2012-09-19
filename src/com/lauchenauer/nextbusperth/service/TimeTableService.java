package com.lauchenauer.nextbusperth.service;

import android.content.Intent;
import android.util.Log;

public class TimeTableService extends WakefulIntentService {
    public TimeTableService() {
        super("TimeTableService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        // do long winded process here
        Log.d("[TimetableService]", "TimeTableService taking orders ...... right you are sir!");
    }
}