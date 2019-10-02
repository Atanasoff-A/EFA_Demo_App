package com.example.efa_demo_app.Helpers;

public class Trip {

    public String departureTime;
    public String arrivalTime;
    public long travelTimeMinutes;
    public String transport;

    public Trip(String departureTime, String arrivalTime, long travelTimeMinutes, String transport) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.travelTimeMinutes = travelTimeMinutes;
        this.transport = transport;
    }
}
