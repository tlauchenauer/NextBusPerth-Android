package com.lauchenauer.nextbusperth.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;
import com.lauchenauer.nextbusperth.helper.SettingsHandler;
import com.lauchenauer.nextbusperth.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NextBusFragment extends ListFragment {
    private SettingsHandler settingsHandler;
    private DatabaseHelper dbHelper;
    private RowAdapter adapter;
    private TextView journeyName;
    private TextView stopName;
    private String journey;
    private boolean active = true;

    public static NextBusFragment newInstance(String journey) {
        NextBusFragment f = new NextBusFragment();

        Bundle args = new Bundle();
        args.putString("journey", journey);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        journey = getArguments() != null ? getArguments().getString("journey") : "Work";
        settingsHandler = new SettingsHandler(getActivity().getApplicationContext());
        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());

        adapter = new RowAdapter(getActivity().getApplicationContext(), new ArrayList<Service>());
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nextbus, container, false);

        journeyName = (TextView) v.findViewById(R.id.journey_name);
        stopName = (TextView) v.findViewById(R.id.stop_name);
        new UpdateTimerTask(this, 30);

        updateData();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        active = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        active = true;

        updateData();
    }

    private void updateData() {
        journeyName.setText(journey);

        String stopNumber = settingsHandler.getWorkStopNumber();
        if (journey.equals("Home")) {
            stopNumber = settingsHandler.getHomeStopNumber();
        }

        List<Service> services = dbHelper.getNextBuses(stopNumber, 5);
        adapter.setServices(services);

        if (services.size() > 0) {
            Service s = services.get(0);
            stopName.setText(s.getStopName());
        }
    }

    private static class UpdateTimerTask extends TimerTask {
        private NextBusFragment fragment;
        private Timer timer;

        public UpdateTimerTask(NextBusFragment fragment, int seconds) {
            this.fragment = fragment;

            timer = new Timer();
            timer.schedule(this, seconds * 1000, seconds * 1000);
        }

        @Override
        public void run() {
            if (fragment.getActivity() == null || fragment.getActivity().getApplicationContext() == null) {
                cancel();
                return;
            }

            if (!fragment.active) return;

            fragment.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    fragment.updateData();
                }
            });
        }
    }
}