package com.example.safedrive_guardian.ui.drivingpattern;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {
    private HashMap<String,String> getDur(JSONArray googleDirectionsJson)
    {
        HashMap<String,String> googleDirMap = new HashMap<>();
        String dura = "";
        String dist ="";


        try {

            dura = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            dist = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirMap.put("duration" , dura);
            googleDirMap.put("distance", dist);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return googleDirMap;
    }


    private HashMap<String, String> getPl(JSONObject googlePlaceJson)
    {
        HashMap<String, String> glgPlaMap = new HashMap<>();
        String plceNa = "-NA-";
        String vici = "-NA-";
        String lat = "";
        String lng = "";
        String ref = "";
        Log.d("getPlace", "Entered");


        try {
            if(!googlePlaceJson.isNull("name"))
            {

                plceNa = googlePlaceJson.getString("name");

            }
            if( !googlePlaceJson.isNull("vicinity"))
            {
                vici = googlePlaceJson.getString("vicinity");

            }
            lat = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            lng = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            ref = googlePlaceJson.getString("reference");

            glgPlaMap.put("place_name" , plceNa);
            glgPlaMap.put("vicinity" , vici);
            glgPlaMap.put("lat" , lat);
            glgPlaMap.put("lng" , lng);
            glgPlaMap.put("reference" , ref);


            Log.d("getPlace", "Putting Places");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return glgPlaMap;
    }



    private List<HashMap<String,String>> getPla(JSONArray jsonArray)
    {
        int cnt = jsonArray.length();
        List<HashMap<String,String>> plaList = new ArrayList<>();
        HashMap<String,String> plaMap = null;
        Log.d("Places", "getPlaces");

        for(int i = 0; i< cnt; i++)
        {
            try {
                plaMap = getPl((JSONObject) jsonArray.get(i));
                plaList.add(plaMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return plaList;

    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");

            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPla(jsonArray);
    }

    public String[] parseDir(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRoutes(jsonArray);
    }

    public HashMap<String, String> DistDir(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getDur(jsonArray);
    }

    public String[] getRoutes(JSONArray googleStepsJson )
    {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i = 0;i<count;i++)
        {
            try {
                polylines[i] = getRoute(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }

    public String getRoute(JSONObject googlePathJson)
    {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }

}