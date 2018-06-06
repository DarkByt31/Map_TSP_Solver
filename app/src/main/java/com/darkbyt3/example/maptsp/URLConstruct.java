package com.darkbyt3.example.maptsp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * This class constructs URLs for distance API and polyline API
 */

public class URLConstruct {

    private static String str_origin, str_destination, parameters, url;
    private static String sensor = "sensor=false", output = "json";

    //construct url for measuring distance
    public static String makeDistURL(LatLng origin, LatLng dest) {

        // Origin of route
        str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        str_destination = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        sensor = "sensor=false";
        // Building the parameters to the web service
        parameters = str_origin + "&" + str_destination + "&" + sensor;
        // Output format
        output = "json";
        // Building the url to the web service
        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    // Construct URL for polyline
    public static String makePolyURL(List<LatLng> locations, ArrayList<Integer> wayPoints) {

        MapsActivity.textView.setText("Drawing the path...");

        // Origin of route
        str_origin = "origin=" + locations.get(0).latitude + "," + locations.get(0).longitude;
        // Destination of route
        str_destination = "destination=" + locations.get(0).latitude + "," + locations.get(0).longitude;
        // Building the parameters to the web service
        parameters = str_origin + "&" + str_destination;
        // Output format
        output = "json";
        //waypoints format
        String wpts = locations.get(wayPoints.get(1)).latitude + "," + locations.get(wayPoints.get(1)).longitude;
        for (int i = 2; i < locations.size(); i++)
            wpts = wpts + "|" + locations.get(wayPoints.get(i)).latitude + "," + locations.get(wayPoints.get(i)).longitude;

        // Building the url to the web service
        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&waypoints=" + wpts;

        return url;
    }
}
