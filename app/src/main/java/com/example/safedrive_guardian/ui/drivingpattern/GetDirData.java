package com.example.safedrive_guardian.ui.drivingpattern;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirData;
    String dur, dist;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];


        DnlURL downUrl = new DnlURL();
        try {
            googleDirData = downUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirData;
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String, String> distDir = null;
        String[] direList;
        Parser parser = new Parser();
        direList = parser.parseDir(s);
        displayDirection(direList);
        distDir = parser.DistDir(s);
        dur = distDir.get("duration");
        dist = distDir.get("distance");

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Duration :" + dur);
        markerOptions.snippet("Distance " + dist);
        mMap.addMarker(markerOptions);

    }

    public void displayDirection(String[] directionsList) {
        for (String s : directionsList) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(s));

            mMap.addPolyline(options);
        }
    }
}
