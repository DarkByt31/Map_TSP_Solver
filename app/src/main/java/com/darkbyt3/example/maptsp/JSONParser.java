package com.darkbyt3.example.maptsp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class parses the JSON data.
 * jsonPolyParser parses the polyline JSON data
 * jsonDistanceParser parses distance JSON data
 */

public class JSONParser {

    // Parse the polyline JSON
    public static String jsonPolyParser(String result) {

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");

            return encodedString;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Parse the distance JSON and send the distance as String
    public static String jsonDistParser(String result) {

        try {
            final JSONObject json = new JSONObject(result);

            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            JSONArray newTempARr = routes.getJSONArray("legs");
            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
            JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

            return distOb.getString("value");

        } catch (Exception e) {
            System.out.println("#code exception");
            Log.d("#result", result);
        }
        return null;
    }
}
