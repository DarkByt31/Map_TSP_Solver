package com.darkbyt3.example.maptsp;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    public static GoogleMap mMap;
    public static Polyline line;                                                  //adds polyline to the map

    public static ProgressBar progressBar;
    public static TextView textView;


    private static List<LatLng> locations = new ArrayList<>();      //stores the locations marked by user
    private static int counti, countj, size, uniq;                  //counti/j iterates over adjMat,
                                                                    //uniq is the no. of unique distance in matrix

    private static int[][] adjMat = new int[10][10];                 //symmetric adjacency matrix with adjMat[i][i]=0
    private static int distance = 0;
    private static ArrayList<Integer> wayPoints = new ArrayList<>();

    // Static LatLng trial values
    LatLng startLatLng = new LatLng(12.8643255, 77.6656508);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);                 //To Hide ProgressBar

        textView = findViewById(R.id.textView);

        for (int i = 0; i < 10; i++)
            adjMat[i][i] = 0;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);


        // Move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your marker title")
                .snippet("Your marker snippet"));

        //to add each marked location in an array****
        locations.add(latLng);

    }

    public void calculate(View view) {

        size = locations.size() - 1;
        uniq = size * (size + 1) / 2;
        counti = 1;
        countj = 0;

        progressBar.setVisibility(View.VISIBLE);               // To Show ProgressBar
        textView.setText("Calculating distances...");

        new DistAsyncTask().execute();
    }

    private class DistAsyncTask extends AsyncTask<String, String, String> {
        int i, j;
        String url;

        @Override
        protected void onPreExecute() {          //set the i, j var for adj matrix
            i = counti;
            j = countj;

            countj++;
            if (countj >= counti) {
                countj = 0;
                counti++;
            }

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            url = URLConstruct.makeDistURL(locations.get(i), locations.get(j));      //construct url to measure distance

            JSONDownload jDownloader = new JSONDownload();
            return jDownloader.getJSONFromUrl(url);                         //download the json data
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // parse json and add dist to adj matrix
            String s = JSONParser.jsonDistParser(result);
            distance = Integer.parseInt(s);
            adjMat[i][j] = adjMat[j][i] = distance;

            // execute again for the remaining places
            if ((i + 1) * (j + 2) < (size + 1) * (size + 1)) {
                new DistAsyncTask().execute("");
            }

            //this condition is true when the first instance of asynctask is destroyed
            if (--uniq == 0) {
                progressBar.setVisibility(View.INVISIBLE);              // To hide ProgressBar

                TSPEngine tsp = new TSPEngine();
                wayPoints = tsp.computeTSP(adjMat, size + 1);

                String url = URLConstruct.makePolyURL(locations, wayPoints);
                new PolyAsyncTask(url, locations, wayPoints, mMap).execute();
            }
        }
    }

    public void printLog(View view) {

        int i, j;
        for (i = 0; i <= size; i++)
            for (j = 0; j <= size; j++)
                Log.d("#adjMat", String.valueOf(adjMat[i][j]));

        for (i = 0; i < locations.size(); i++)
            Log.d("#dist", String.valueOf(locations.get(i)));
    }

    public void clearMap(View view) {

        mMap.clear();
        locations.clear();
        textView.setText("");

    }
}