package com.example.efa_demo_app;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.efa_demo_app.Helpers.Journey;
import com.example.efa_demo_app.Helpers.JourneyAdapter;
import com.example.efa_demo_app.Helpers.Trip;
import com.example.efa_demo_app.Helpers.TripAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //StopFinder Request URI- Example http://smartmmi.demo.mentz.net/smartmmi/XML_STOPFINDER_REQUEST?outputFormat=rapidJson&type_sf=any&name_sf=mto
    public String stopFinderURI = "http://smartmmi.demo.mentz.net/smartmmi/XML_STOPFINDER_REQUEST?outputFormat=rapidJson&type_sf=any&name_sf=";
    public String stopFinderURIfinal;

    //Trip Request URI Example: http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&name_origin=de:08212:89&type_destination=stop&name_destination=de:08212:5203
    public String tripRequestURIbegin = "http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&name_origin=";
    public String tripRequestURIend = "&type_destination=stop&name_destination=";
    public String tripRequestURIfinal;

    //Origin
    public LinearLayout linLayStartHS;
    public TextView editTextOrigin;
    public TextView requestEFAOrigin;
    public TextView resultEFAOrigin;
    public Button buttonSearchOrigin;
    public String selectedOriginItem;
    public String selectedOriginStationID;

    //General Parameters
    private RequestQueue mQeue;
    public HashMap<String, String> data;
    public PopupMenu popupMenu;

    //Destination
    public LinearLayout linLayZielHS;
    public TextView editTextDestination;
    public TextView requestEFADestination;
    public TextView resultEFADestination;
    public Button buttonSearchDestination;
    public String selectedDestinationItem;
    public String selectedDestinationStationID;

    //Trip Request
    public LinearLayout linLayTripRequest;
    public Button buttonTripRequest;
    //public ArrayAdapter myAdapter;

    //Trips list
    private List<Journey> journeyList = new ArrayList<>();
    private List<Trip> allTrips;
    private ListView tripsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tripsListView = findViewById(R.id.listv);

        //Origin
        linLayStartHS = findViewById(R.id.linLayStartHS);
        editTextOrigin = findViewById(R.id.editTextOrigin);
        buttonSearchOrigin = findViewById(R.id.buttonSearchOrigin);
        requestEFAOrigin = findViewById(R.id.requestOriginTextView);
        resultEFAOrigin = findViewById(R.id.responseOriginTextView);
        resultEFAOrigin.setMovementMethod(new ScrollingMovementMethod());

        //General Parameters
        mQeue = Volley.newRequestQueue(this);
        data = new HashMap<>();

        //Destination
        linLayZielHS = findViewById(R.id.linLayZielHS);
        editTextDestination = findViewById(R.id.editTextDestination);
        buttonSearchDestination = findViewById(R.id.buttonSearchDestination);
        requestEFADestination = findViewById(R.id.requestDestinationTextView);
        resultEFADestination = findViewById(R.id.responseDestinationTextView);
        resultEFADestination.setMovementMethod(new ScrollingMovementMethod());

        //Trip Request
        linLayTripRequest = findViewById(R.id.linLayTripRequest);
        buttonTripRequest = findViewById(R.id.buttonTripRequest);

        //Origin
        buttonSearchOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultEFAOrigin.setText("");
                popupMenu = new PopupMenu(MainActivity.this, buttonSearchOrigin);
                String origin = String.valueOf(editTextOrigin.getText());
                stopFinderURIfinal = stopFinderURI + origin;
                requestEFAOrigin.setText(stopFinderURIfinal);

                // Tutorial https://www.youtube.com/watch?v=y2xtLqP8dSQ
                jsonParseOrigin();
            }
        });

        //Destination
        buttonSearchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultEFADestination.setText("");
                popupMenu = new PopupMenu(MainActivity.this, buttonSearchDestination);
                String origin = String.valueOf(editTextDestination.getText());
                stopFinderURIfinal = stopFinderURI + origin;
                requestEFADestination.setText(stopFinderURIfinal);

                // Tutorial https://www.youtube.com/watch?v=y2xtLqP8dSQ
                jsonParseDestination();
            }
        });

        //TripRequest
        buttonTripRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tripRequestURIfinal = tripRequestURIbegin + selectedOriginStationID + tripRequestURIend + selectedDestinationStationID;
                Log.d("TripRequest: ", tripRequestURIfinal);

                linLayStartHS.setVisibility(View.GONE);
                linLayZielHS.setVisibility(View.GONE);
                linLayTripRequest.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Triggers Trip Request: " + selectedOriginStationID + " to " + selectedDestinationStationID, Toast.LENGTH_LONG).show();
                tripRequest();
            }
        });
        //myAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
    }

    //Origin
    private void jsonParseOrigin() {

        String url = stopFinderURIfinal;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("locations");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject location = jsonArray.getJSONObject(i);

                                String stationID = location.getString("id");
                                String stationName = location.getString("name");
                                int stationMatchQuality = location.getInt("matchQuality");

                                data.put(stationName, stationID);

                                popupMenu.getMenu().add(stationName);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        selectedOriginItem = item.getTitle().toString();
                                        editTextOrigin.setText(selectedOriginItem);
                                        Log.d("selectedOriginTitle ", selectedOriginItem);
                                        //DEBUG
                                        // Toast.makeText(MainActivity.this, selectedOriginItem, Toast.LENGTH_SHORT).show();

                                        selectedOriginStationID = data.get(selectedOriginItem);
                                        if (selectedOriginStationID != null) {
                                            Log.d("selectedOriginID ", selectedOriginStationID);
                                        } else {
                                            Toast.makeText(MainActivity.this, "please be more specific", Toast.LENGTH_SHORT).show();
                                        }
                                        return false;
                                    }
                                });

                                //DEBUG
                                resultEFAOrigin.append(stationName + "; " + stationID + "; " + (stationMatchQuality) + "\n\n");
                            }
                            //Log.d("HashMapAll: ", String.valueOf(data));
                            popupMenu.show();

                            // set Linear Layout visible for user to select destination station
                            linLayZielHS.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            Log.e("ERROR", e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQeue.add(request);
    }

    //Destination
    private void jsonParseDestination() {

        String url = stopFinderURIfinal;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("locations");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject location = jsonArray.getJSONObject(i);

                                String stationID = location.getString("id");
                                String stationName = location.getString("name");
                                int stationMatchQuality = location.getInt("matchQuality");

                                data.put(stationName, stationID);

                                popupMenu.getMenu().add(stationName);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        selectedDestinationItem = item.getTitle().toString();
                                        editTextDestination.setText(selectedDestinationItem);
                                        Log.d("selectedDestTitle ", selectedDestinationItem);
                                        //DEBUG
                                        // Toast.makeText(MainActivity.this, selectedOriginItem, Toast.LENGTH_SHORT).show();

                                        selectedDestinationStationID = data.get(selectedDestinationItem);
                                        if (selectedDestinationStationID != null) {
                                            Log.d("selectedDestID ", selectedDestinationStationID);
                                        } else {
                                            Toast.makeText(MainActivity.this, "please be more specific", Toast.LENGTH_SHORT).show();
                                        }
                                        return false;
                                    }
                                });

                                //DEBUG
                                resultEFADestination.append(stationName + "; " + stationID + "; " + (stationMatchQuality) + "\n\n");
                            }
                            //Log.d("HashMapAll: ", String.valueOf(data));
                            popupMenu.show();

                            // set Linear Layout visible for user to start TripRequest
                            linLayTripRequest.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            Log.e("ERROR", e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQeue.add(request);
    }

    public void tripRequest() {

        String url = tripRequestURIfinal;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArrayJourneys = response.getJSONArray("journeys");
                            for (int i = 0; i < jsonArrayJourneys.length(); i++) {
                                JSONObject journey = jsonArrayJourneys.getJSONObject(i);
                                Log.d("test", journey.toString());

                                allTrips = new ArrayList<>();

                                //legs node is JSON Array
                                JSONArray jsonArrayLegs = journey.getJSONArray("legs");
                                for (int j = 0; j < jsonArrayLegs.length(); j++) {
                                    JSONObject leg = jsonArrayLegs.getJSONObject(j);

                                    //int duration = leg.getInt("duration");
                                    //Log.d("TripRequest_LEGS ", String.valueOf(duration));

                                    //origin node is JSON Object
                                    JSONObject jsonObjectOrigin = leg.getJSONObject("origin");
                                    String originDepartureTime = jsonObjectOrigin.getString("departureTimePlanned");

                                    //Log.d("TripRequest_DepartTime ", String.valueOf(originDepartureTime));
                                    //convert String to date
                                    DateFormat formatDepartureTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
                                    Date originDepartureDate = formatDepartureTime.parse(originDepartureTime);
                                    originDepartureDate.getTime();
                                    Log.d("TripRequest_DepartTime ", String.valueOf(originDepartureDate));

                                    //destination node is JSON Object
                                    JSONObject jsonObjectDestination = leg.getJSONObject("destination");
                                    String originArrivalTime = jsonObjectDestination.getString("arrivalTimePlanned");
                                    //Log.d("TripRequest_ArriveTime ", String.valueOf(originArrivalTime));
                                    //convert String to date
                                    DateFormat formatArrivalTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
                                    Date originArrivalDate = formatArrivalTime.parse(originArrivalTime);
                                    Log.d("TripRequest_ArriveTime ", String.valueOf(originArrivalDate));

                                    //calculating trip duration "travelTime"
                                    long travelTime = originArrivalDate.getTime() - originDepartureDate.getTime();
                                    Log.d("TripRequest_TravelTime ", String.valueOf(originArrivalDate.getTime() - originDepartureDate.getTime()));
                                    long minutes = travelTime / 60000;
                                    Log.d("TripRequest_TravelTime ", minutes + " Minuten");

                                    //transportation node is JSON Object
                                    JSONObject jsonObjectTransportation = leg.getJSONObject("transportation");
                                    String transportationName = jsonObjectTransportation.getString("name");

                                    // if 0 dann fußweg dabei
                                    Log.d("TripRequest_TRANSPORT ", String.valueOf(transportationName));

                                    allTrips.add(new Trip(String.valueOf(originDepartureDate), String.valueOf(originArrivalDate), minutes,String.valueOf(transportationName)));
                                    Log.d("test", String.valueOf(allTrips.size()));
                                }

                                journeyList.add(new Journey(allTrips));

                                //end of a journey element
                                Log.d("TripRequest_Journey_# ", String.valueOf(i));


                                // Tutorial: https://stackoverflow.com/questions/47129961/how-to-parsing-multi-dimensional-json-data-array-in-android-studio
                            }


                        } catch (JSONException e) {
                            Log.e("ERROR", e.toString());
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

//                        Log.d("test", "-------------------------------------------------------------------");
//                        Log.d("test", String.valueOf(allTrips.size()));
//                        String[] transports = new String[allTrips.size()];
//                        int counter = 0;
//                        for (Trip t : allTrips) {
//                            Log.d("test", t.arrivalTime);
//                            Log.d("test", t.departureTime);
//                            Log.d("test", String.valueOf(t.travelTimeMinutes));
//
//
//                            transports[counter] = t.transport;
//                            counter++;
//                        }

                        JourneyAdapter journeyAdapter = new JourneyAdapter(getApplicationContext(), journeyList);
                        tripsListView.setAdapter(journeyAdapter);

                        //THIS IS WORKING !
//                        TripAdapter tripAdapter = new TripAdapter(getApplicationContext(), allTrips);
//                        tripsListView.setAdapter(tripAdapter);


//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
//                                android.R.layout.simple_list_item_1, android.R.id.text1, transports);
//                        tripsListView.setAdapter(adapter);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQeue.add(request);
    }
}
