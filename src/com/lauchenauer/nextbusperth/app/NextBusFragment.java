package com.lauchenauer.nextbusperth.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.Journey;
import com.lauchenauer.nextbusperth.dao.Service;
import com.lauchenauer.nextbusperth.helper.DatabaseHelper;

public class NextBusFragment extends ListFragment {
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private RowAdapter adapter;
    private TextView journeyNameView;
    private TextView stopNameView;
    private TextView lastUpdateView;
    private Journey journey;
    private boolean hasPrev;
    private boolean hasNext;
    private int pageNumber;
    private boolean active = true;

    public static NextBusFragment newInstance(long journeyId, boolean hasPrev, boolean hasNext, int page) {
        NextBusFragment f = new NextBusFragment();

        Bundle args = new Bundle();
        args.putLong("journeyId", journeyId);
        args.putBoolean("hasNext", hasNext);
        args.putBoolean("hasPrev", hasPrev);
        args.putInt("page", page);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        journey = DatabaseHelper.getJourneyById(getArguments().getLong("journeyId"));
        hasNext = getArguments().getBoolean("hasNext");
        hasPrev = getArguments().getBoolean("hasPrev");
        pageNumber = getArguments().getInt("page");

        adapter = new RowAdapter(getActivity().getApplicationContext(), new ArrayList<Service>());
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nextbus_fragment, container, false);

        journeyNameView = (TextView) v.findViewById(R.id.journey_name);
        stopNameView = (TextView) v.findViewById(R.id.stop_name);
        lastUpdateView = (TextView) v.findViewById(R.id.last_update);
        new UpdateTimerTask(this, 30);

        updateData();

        Button prevButton = (Button) v.findViewById(R.id.prev_journey);
        prevButton.setVisibility(hasPrev ? View.VISIBLE : View.INVISIBLE);
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((NextBusActivity) getActivity()).setPage(pageNumber - 1);
            }
        });

        Button nextButton = (Button) v.findViewById(R.id.next_journey);
        nextButton.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((NextBusActivity) getActivity()).setPage(pageNumber + 1);
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
        journeyNameView.setText(journey.getName());
        lastUpdateView.setText("updated at " + TIME_FORMAT.format(new Date()));

        List<Service> services = DatabaseHelper.getNextBuses(journey, 5);
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