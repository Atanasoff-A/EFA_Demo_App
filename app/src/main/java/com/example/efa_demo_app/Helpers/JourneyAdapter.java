package com.example.efa_demo_app.Helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.efa_demo_app.R;

import java.util.List;

import androidx.annotation.NonNull;

public class JourneyAdapter extends ArrayAdapter<Journey> {

    public JourneyAdapter(@NonNull Context context, List<Journey> journeyList) {
        super(context, 0, journeyList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Journey journey = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.journey_layout, parent, false);
        }

        // Lookup view for data population
        TextView tvDepartureTime = convertView.findViewById(R.id.departure_time);
        TextView tvArrivalTime = convertView.findViewById(R.id.arrival_time);
        TextView transportType = convertView.findViewById(R.id.transport_type);
        TextView tvTravelTime = convertView.findViewById(R.id.travel_time);

        List<Trip> allTrips = journey.allTrips;

        String transport = "";
        String departureTime = allTrips.get(0).departureTime;
        String arrivalTime = allTrips.get(allTrips.size() - 1).arrivalTime;
        long travelTimeMin = 0;

        Log.d("test", "***********************************");
        for (Trip t : allTrips) {
            Log.d("test23", t.transport);
            transport += t.transport + " -> ";
            travelTimeMin += t.travelTimeMinutes;
        }

        // Remove "->" at the end
        transport = transport.substring(0, transport.length() - 3);



        // Populate the data into the template view using the data object
        tvDepartureTime.setText("Departure time: " + departureTime);
        tvArrivalTime.setText("Arrival time: " + arrivalTime);
        transportType.setText("Transport type: " + transport);
        tvTravelTime.setText("Travel time: " + String.valueOf(travelTimeMin));

        return convertView;
    }
}
