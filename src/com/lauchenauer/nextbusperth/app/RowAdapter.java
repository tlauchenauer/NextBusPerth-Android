package com.lauchenauer.nextbusperth.app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.dao.Service;

public class RowAdapter extends ArrayAdapter<Service> {
    private Context context;
    private List<Service> services;

    public RowAdapter(Context context, List<Service> services) {
        super(context, R.layout.row_adapter, services);
        this.context = context;
        this.services = services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
        clear();
        for (Service s : services) {
            add(s);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_adapter, parent, false);

        TextView routeNumber = (TextView) rowView.findViewById(R.id.route_number);
        TextView headsign = (TextView) rowView.findViewById(R.id.headsign);
        TextView timeDelta = (TextView) rowView.findViewById(R.id.time_delta);
        TextView stopName = (TextView) rowView.findViewById(R.id.stop_name);
        TextView departureTime = (TextView) rowView.findViewById(R.id.departure_time);

        Service s = services.get(position);
        routeNumber.setText(s.getRouteNumber());
        headsign.setText(s.getHeadsign());
        timeDelta.setText(s.getTimeDelta());
        stopName.setText(s.getStopName());
        if (s.getDepartureTime() == null) {
            departureTime.setText("");
        } else {
            departureTime.setText(NextBusFragment.TIME_FORMAT.format(s.getDepartureTime()));
        }

        int pos = position;
        while (pos > 4) {
            pos -= 5;
        }

        if (!services.get(0).hasLeft()) pos++;

        switch (pos) {
            case 0:
                timeDelta.setBackgroundResource(R.drawable.row_gradient1);
                break;
            case 1:
                timeDelta.setBackgroundResource(R.drawable.row_gradient2);
                break;
            case 2:
                timeDelta.setBackgroundResource(R.drawable.row_gradient3);
                break;
            case 3:
                timeDelta.setBackgroundResource(R.drawable.row_gradient4);
                break;
            case 4:
                timeDelta.setBackgroundResource(R.drawable.row_gradient5);
                break;
            case 5:
                timeDelta.setBackgroundResource(R.drawable.row_gradient6);
                break;
        }


        return rowView;
    }
}
