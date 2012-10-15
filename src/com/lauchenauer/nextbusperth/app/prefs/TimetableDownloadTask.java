package com.lauchenauer.nextbusperth.app.prefs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.lauchenauer.nextbusperth.helper.TimetableHelper;

public class TimetableDownloadTask extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog progressDialog;
    private Context context;

    public TimetableDownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        new TimetableHelper().downloadTimeTable();

        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("downloading departure times ...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        progressDialog.dismiss();
    }
}
