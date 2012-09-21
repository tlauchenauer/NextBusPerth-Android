package com.lauchenauer.nextbusperth;

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

public class UrlHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String BASE_TIMETABLE_URL = "http://perth-timetable.herokuapp.com/time_table/";

    public static String readTimeTable(String stopNumber, Date date) {
        Log.d("[UrlHelper.readTimeTable]", "Stop: " + stopNumber + "  for: " + DATE_FORMAT.format(date));

        return readTextFromUrl(BASE_TIMETABLE_URL +stopNumber + "/" + DATE_FORMAT.format(date));
    }
    
    private static String readTextFromUrl(String url) {
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
            Log.d("[UrlHelper.readTextFromUrl]", "Network exception", e);
        }

        return "";
    }
}
