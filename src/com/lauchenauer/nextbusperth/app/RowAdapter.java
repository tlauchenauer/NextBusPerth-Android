package com.lauchenauer.nextbusperth.app;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.lauchenauer.nextbusperth.R;
import com.lauchenauer.nextbusperth.model.Service;

import java.util.List;

public class RowAdapter extends ArrayAdapter<Service> {
    private Context context;
    private List<Service> services;

    public RowAdapter(Context context, List<Service> services) {
        super(context, R.layout.app_view_row, services);
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
        View rowView = inflater.inflate(R.layout.app_view_row, parent, false);

        TextView routeNumber = (TextView) rowView.findViewById(R.id.route_number);
        TextView headsign = (TextView) rowView.findViewById(R.id.headsign);
        TextView timeDelta = (TextView) rowView.findViewById(R.id.time_delta);

        Service s = services.get(position);
        routeNumber.setText(s.getRouteNumber());
        headsign.setText(s.getHeadsign());
        timeDelta.setText(s.getTimeDelta());

        GradientDrawable background = (GradientDrawable) timeDelta.getBackground();
        background.setColor(Color.rgb(s.hasLeft() ? 200 + position * 10 : 0, 0, s.hasLeft() ? 0 : 250 - position * 20));

        return rowView;
    }
}
