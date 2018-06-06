package com.darkbyt3.example.maptsp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class downloads the polyline in the background.
 * Decodes it. And then add it to the polyline option
 * to display the line on map.
 */

public class PolyAsyncTask extends AsyncTask<Void, Void, String> {
    String url;

    GoogleMap mMap;

    private static List<LatLng> locations = new ArrayList<>();
    private static ArrayList<Integer> wayPoints = new ArrayList<>();

    PolyAsyncTask(String urlPass, List<LatLng> loc, ArrayList<Integer> wPoints, GoogleMap mm) {
        url = urlPass;

        locations = loc;
        wayPoints = wPoints;

        mMap = mm;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

        MapsActivity.progressBar.setVisibility(View.VISIBLE);               // To Show ProgressBar
    }

    @Override
    protected String doInBackground(Void... params) {

        // Download the JSON content
        JSONDownload jDownloader = new JSONDownload();
        return jDownloader.getJSONFromUrl(url);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        MapsActivity.progressBar.setVisibility(View.INVISIBLE);                 //To Hide ProgressBar

        if (result != null) {
            drawPath(result);
        }
    }

    // parses json data to add polyline on map
    public void drawPath(String result) {
        mMap.clear();

        String path = "";

        // add new markers with labels to identify the order
        for (int i = 0; i < locations.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(locations.get(i))
                    .title(String.valueOf(wayPoints.get(i)))).showInfoWindow();
            path = wayPoints.get(i).toString() + "->";
        }

        path = path + "1";

        try {
            // Parse JSON data
            String json = JSONParser.jsonPolyParser(result);

            // Decode the polyline and add it to the list
            List<LatLng> list = DecodePolyline.decodePoly(json);

            // Build new options for polyline.
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

            // Add polyline Polyline options
            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                options.add(src);
            }

            // Add polyline to the map
            MapsActivity.line = mMap.addPolyline(options);
            MapsActivity.textView.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

