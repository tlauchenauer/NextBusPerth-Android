package com.lauchenauer.nextbusperth;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimetableHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TIMETABLE_URL = "time_table/";
    private static final String BASE_URL = "http://perth-timetable.herokuapp.com/";

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

        return readTextFromUrl(BASE_URL + TIMETABLE_URL + stopNumber + "/" + DATE_FORMAT.format(date));
    }

    private String readTextFromUrl(String url) {
        InputStream content;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            content = response.getEntity().getContent();

            BufferedInputStream bis = new BufferedInputStream(content);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);

            int current;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return new String(baf.toByteArray());
        } catch (Exception e) {
            Log.d("[TimetableHelper.readTextFromUrl]", "Network exception", e);
        }

        return "";
    }
}
