package com.example.efa_demo_app.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.efa_demo_app.R;

import java.util.List;

import androidx.annotation.NonNull;

public class TripAdapter extends ArrayAdapter<Trip> {
    public TripAdapter(@NonNull Context context, List<Trip> trips) {
        super(context, 0, trips);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Trip trip = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trip_item, parent, false);
        }
        // Lookup view for data population
        TextView tvDepartureTime = convertView.findViewById(R.id.departure_time);
        TextView tvArrivalTime = convertView.findViewById(R.id.arrival_time);
        TextView transportType = convertView.findViewById(R.id.transport_type);
        TextView tvTravelTime = convertView.findViewById(R.id.travel_time);

        // Populate the data into the template view using the data object
        tvDepartureTime.setText("Departure time: " + trip.departureTime);
        tvArrivalTime.setText("Arrival time: " + trip.arrivalTime);
        transportType.setText("Transport type: " + trip.transport);
        tvTravelTime.setText("Travel time: " + String.valueOf(trip.travelTimeMinutes));

        return convertView;
    }
}
