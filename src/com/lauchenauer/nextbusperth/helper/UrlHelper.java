package com.lauchenauer.nextbusperth.helper;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UrlHelper {
    private static final String BASE_URL = "http://perth-timetable.herokuapp.com/";

    public static String readTextFromUrl(String url) {
        return readTextFromUrlWithParams(url, new ArrayList<NameValuePair>(0));
    }

    public static String readTextFromUrlWithParams(String url, List<NameValuePair> params) {
        InputStream content;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            Log.d("URL", addParamsToUrl(url, params));
            HttpGet getRequest = new HttpGet(BASE_URL + addParamsToUrl(url, params));

            HttpResponse response = httpclient.execute(getRequest);
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

    private static String addParamsToUrl(String url, List<NameValuePair> params) {
        if (params.size() == 0) return url;

        if (!url.endsWith("?"))
            url += "?";

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }
}
