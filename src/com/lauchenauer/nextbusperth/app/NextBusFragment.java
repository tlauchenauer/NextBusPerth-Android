package com.lauchenauer.nextbusperth.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.helper.NewDatabaseHelper;
import com.lauchenauer.nextbusperth.helper.SettingsHelper;
import com.lauchenauer.nextbusperth.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.lauchenauer.nextbusperth.app.NextBusApplication.*;

public class NextBusFragment extends ListFragment {
    private SettingsHelper settingsHelper;
    private RowAdapter adapter;
    private TextView journeyNameView;
    private TextView stopNameView;
    private String journeyName;
    private boolean active = true;

    public static NextBusFragment newInstance(String journey) {
        NextBusFragment f = new NextBusFragment();

        Bundle args = new Bundle();
        args.putString("journeyName", journey);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        journeyName = getArguments() != null ? getArguments().getString("journeyName") : "Work";
        settingsHelper = new SettingsHelper(getActivity().getApplicationContext());

        adapter = new RowAdapter(getActivity().getApplicationContext(), new ArrayList<Service>());
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nextbus, container, false);

        journeyNameView = (TextView) v.findViewById(R.id.journey_name);
        stopNameView = (TextView) v.findViewById(R.id.stop_name);
        new UpdateTimerTask(this, 30);

        updateData();

        Button btn;
        if (journeyName.equals("Home")) {
            btn = (Button) v.findViewById(R.id.prev_journey);
        } else {
            btn = (Button) v.findViewById(R.id.next_journey);
        }
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int page = journeyName.equals("Home") ? 0 : 1;
                ((NextBusActivity) getActivity()).setPage(page);
            }
        });

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
        journeyNameView.setText(journeyName);

        Journey journey = getApp().getJourney(JourneyType.work);
        if (journeyName.equals("Home")) {
            journey = getApp().getJourney(JourneyType.home);
        }

        List<Service> services = NewDatabaseHelper.getNextBuses(journey, 5);
        if (services.size() < 1) {
            services.add(new Service("", "", "NO DATA", "", "download timetable", null));
        }
        adapter.setServices(services);

        if (services.size() > 0) {
            Service s = services.get(0);
            stopNameView.setText(s.getStopName());
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