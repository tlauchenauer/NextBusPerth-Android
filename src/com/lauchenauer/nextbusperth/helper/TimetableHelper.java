package com.lauchenauer.nextbusperth.helper;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimetableHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TIMETABLE_URL = "time_table/";

    private Context context;

    public TimetableHelper(Context context) {
        this.context = context;
    }

    public void downloadTimeTable() {
        SettingsHandler prefs = new SettingsHandler(context);

        downloadTimeTable(prefs.getHomeStopNumber());
        downloadTimeTable(prefs.getWorkStopNumber());
    }

    private void downloadTimeTable(String stopNumber) {
        String timetableJSON = readTimeTable(stopNumber, new Date());

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.writeTimeTable(timetableJSON);
    }

    private String readTimeTable(String stopNumber, Date date) {
        Log.d("[TimetableHelper.readTimeTable]", "Stop: " + stopNumber + "  for: " + DATE_FORMAT.format(date));

        return UrlHelper.readTextFromUrl(TIMETABLE_URL + stopNumber + "/" + DATE_FORMAT.format(date));
    }

    public TimetableHelper() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
