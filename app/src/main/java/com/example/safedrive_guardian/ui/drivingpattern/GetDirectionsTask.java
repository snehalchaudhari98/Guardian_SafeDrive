package com.example.safedrive_guardian.ui.drivingpattern;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.example.safedrive_guardian.ui.crimehotspot.CrimeHostspotFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetDirectionsTask extends AsyncTask<LatLng, Void, String>{
    public String apiResponse = "{\n" +
            "    \"total_incidents\": 72,\n" +
            "    \"total_pages\": 1,\n" +
            "    \"incidents\": [\n" +
            "        {\n" +
            "            \"city_key\": \"STZ\",\n" +
            "            \"incident_code\": \"2324370\",\n" +
            "            \"incident_date\": \"2023-11-25T22:50:00Z\",\n" +
            "            \"incident_offense\": \"Disorderly Conduct\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Scottsdale Police Department\",\n" +
            "            \"incident_latitude\": 33.494575,\n" +
            "            \"incident_longitude\": -111.929905,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"city_key\": \"STZ\",\n" +
            "            \"incident_code\": \"2324371\",\n" +
            "            \"incident_date\": \"2023-11-25T22:24:00Z\",\n" +
            "            \"incident_offense\": \"Driving Under the Influence\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Scottsdale Police Department\",\n" +
            "            \"incident_latitude\": 33.493981,\n" +
            "            \"incident_longitude\": -111.907492,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"city_key\": \"STZ\",\n" +
            "            \"incident_code\": \"2324369\",\n" +
            "            \"incident_date\": \"2023-11-25T21:55:00Z\",\n" +
            "            \"incident_offense\": \"Larceny/Theft Offenses\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Scottsdale Police Department\",\n" +
            "            \"incident_latitude\": 33.50032,\n" +
            "            \"incident_longitude\": -111.919883,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"city_key\": \"STZ\",\n" +
            "            \"incident_code\": \"2324368\",\n" +
            "            \"incident_date\": \"2023-11-25T21:27:00Z\",\n" +
            "            \"incident_offense\": \"Disorderly Conduct\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Scottsdale Police Department\",\n" +
            "            \"incident_latitude\": 33.505362,\n" +
            "            \"incident_longitude\": -111.928455,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"city_key\": \"CPHX\",\n" +
            "            \"incident_code\": \"IR23030200\",\n" +
            "            \"incident_date\": \"2023-11-25T21:13:00Z\",\n" +
            "            \"incident_offense\": \"Assault Offenses\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Maricopa County Sheriff's Office\",\n" +
            "            \"incident_latitude\": 33.4148015,\n" +
            "            \"incident_longitude\": -111.6296949,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"city_key\": \"STZ\",\n" +
            "            \"incident_code\": \"2324359\",\n" +
            "            \"incident_date\": \"2023-11-25T20:21:00Z\",\n" +
            "            \"incident_offense\": \"Disorderly Conduct\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Scottsdale Police Department\",\n" +
            "            \"incident_latitude\": 33.499347,\n" +
            "            \"incident_longitude\": -111.924112,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"city_key\": \"CPHX\",\n" +
            "            \"incident_code\": \"IR23030190\",\n" +
            "            \"incident_date\": \"2023-11-25T20:20:00Z\",\n" +
            "            \"incident_offense\": \"Larceny/Theft Offenses\",\n" +
            "            \"incident_offense_code\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_detail_description\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_crime_against\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_offense_action\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_original_type\": \"Not available with evaluation api-key.\",\n" +
            "            \"incident_source_name\": \"Maricopa County Sheriff's Office\",\n" +
            "            \"incident_latitude\": 33.4651482,\n" +
            "            \"incident_longitude\": -112.2717149,\n" +
            "            \"incident_address\": \"Not available with evaluation api-key.\"\n" +
            "        }\n" +
            "\t]\n" +
            "}";

    public GoogleMap googleMap;
    public List<LatLng> hotspots;
    public LatLng startLatLng;
    public LatLng endLatLng;
    public String responseBody;
    public String startAddress;
    public String endAddress;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Clear the map
//        googleMap.clear();
    }
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
    //    @Override
//    public void onMapReady(GoogleMap map) {
//        googleMap = map;
//    }
    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(LatLng... params) {
        LatLng startAddress = params[0];
        LatLng endAddress = params[1];
        try {
            // Construct the URL for the Directions API
            String apiUrl = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=" + startAddress.latitude + "," + startAddress.longitude +
                    "&destination=" + endAddress.latitude + "," + endAddress.longitude +
                    "&key="+ADD_KEY;

            // Make the API request and get the JSON response
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            StringBuilder result = new StringBuilder();
            int data;
            while ((data = inputStream.read()) != -1) {
                result.append((char) data);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String datetime_end = dateFormat.format(cal.getTime()); // Today's date
            cal.add(Calendar.DATE, -6); // Subtract 6 days
            String datetime_ini = dateFormat.format(cal.getTime());

            synchronized(this) {
                startLatLng = startAddress;
                endLatLng = endAddress;
            }


            if(startLatLng == null) {
                startLatLng = new LatLng(33.4704757, -111.927377);
            }
            if(endLatLng == null) {
                endLatLng = new LatLng(33.4704757, -111.927377);
            }
            // Parse the API response to get hotspots
            hotspots = parseHotspotsFromApiResponse(datetime_ini, datetime_end, String.valueOf(startLatLng.latitude), String.valueOf(startLatLng.longitude));
            Log.d("parseHotspotsFromApiResponse", "hotspots = "+hotspots);

            Log.d("parseHotspotsFromApiResponse", "startLatLng = "+startLatLng);
            Log.d("parseHotspotsFromApiResponse", "endLatLng = "+endLatLng);

            return result.toString();

        } catch (IOException e) {
            Log.d("GetDirectionsTask", "Exception message: " + e.getMessage());
            Log.e("GetDirectionsTask", "Error fetching data from Directions API", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("result", "onPostExecute = "+result);
        if (result != null) {
            // Parse the JSON response
            try {
//                googleMap.clear();
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routes = jsonObject.getJSONArray("routes");
                drawHotspots();
                // Extract polyline points from the first route
                if (routes.length() > 0) {
                    googleMap.addMarker(new MarkerOptions().position(startLatLng).title("Start"));
                    googleMap.addMarker(new MarkerOptions().position(endLatLng).title("End"));
                }

            } catch (JSONException e) {
                Log.e("GetDirectionsTask", "Error parsing JSON response", e);
            }
        }
    }

    @SuppressLint("LongLogTag")
    private synchronized LatLng getLatLngFromAddress(String address) {
        try {
            // Construct the URL for the Geocoding API
            String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json" +
                    "?address=" + address +
                    "&key="+ADD_KEY;
            Log.d("parseHotspotsFromApiResponse", "geocodingUrl = "+geocodingUrl);

            // Make the Geocoding API request and get the JSON response
            URL url = new URL(geocodingUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            StringBuilder result = new StringBuilder();
            int data;
            while ((data = inputStream.read()) != -1) {
                result.append((char) data);
            }

            // Parse the API response to get the coordinates
            JSONObject jsonObject = new JSONObject(result.toString());
            JSONArray results = jsonObject.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
                return new LatLng(latitude, longitude);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    private List<LatLng> parseHotspotsFromApiResponse(String datetime_ini, String datetime_end, String lat, String lon) {
        String apiUrl = "https://api.crimeometer.com/v1/incidents/raw-data" +
                "?lat=" + lat + "&lon=" + lon +
                "&datetime_ini=" + datetime_ini +
                "&datetime_end=" + datetime_end +
                "&distance=20mi&page=1";
        String response = "";
        List<LatLng> crimeData = new ArrayList<>();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-api-key", "ADD KEY HERE");

            int status = connection.getResponseCode();
            InputStream inputStream;
            if (status != HttpURLConnection.HTTP_OK) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();
            Log.d("parseHotspotsFromApiResponse", "parseHotspotsFromApiResponse response = "+response);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("message")) {
                try {
                    jsonObject = new JSONObject(apiResponse);
                    JSONArray incidents = jsonObject.getJSONArray("incidents");
                    Log.d("parseHotspotsFromApiResponse", "parseHotspotsFromApiResponse");
                    for (int i = 0; i < incidents.length(); i++) {
                        JSONObject incident = incidents.getJSONObject(i);
                        double latitude = incident.getDouble("incident_latitude");
                        double longitude = incident.getDouble("incident_longitude");
                        crimeData.add(new LatLng(latitude, longitude));
                    }
                } catch (JSONException e) {
                    Log.e("GetDirectionsTask", "Error parsing hotspots from API response", e);
                }
            } else {
                JSONArray incidents = jsonObject.getJSONArray("incidents");
                Log.d("parseHotspotsFromApiResponse", "parseHotspotsFromApiResponse");
                int loop = 0;
                if(incidents.length() > 7) {
                    loop = 7;
                }
                else {
                    loop = incidents.length();
                }
                for (int i = 0; i < loop; i++) {
                    JSONObject incident = incidents.getJSONObject(i);
                    double latitude = incident.getDouble("incident_latitude");
                    double longitude = incident.getDouble("incident_longitude");
                    crimeData.add(new LatLng(latitude, longitude));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if(crimeData.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponse);
                JSONArray incidents = jsonObject.getJSONArray("incidents");
                Log.d("parseHotspotsFromApiResponse", "parseHotspotsFromApiResponse");
                for (int i = 0; i < incidents.length(); i++) {
                    JSONObject incident = incidents.getJSONObject(i);
                    double latitude = incident.getDouble("incident_latitude");
                    double longitude = incident.getDouble("incident_longitude");
                    crimeData.add(new LatLng(latitude, longitude));
                }
            } catch (JSONException e) {
                Log.e("GetDirectionsTask", "Error parsing hotspots from API response", e);
            }
        }
        return crimeData;
    }


//        private List<LatLng> parseHotspotsFromApiResponseString(String apiResponse) {
//            // Parse the API response and create a list of LatLng representing hotspots
//            List<LatLng> hotspots = new ArrayList<>();
//            try {
//                JSONObject jsonObject = new JSONObject(apiResponse);
//                JSONArray incidents = jsonObject.getJSONArray("incidents");
//                Log.d("parseHotspotsFromApiResponse", "parseHotspotsFromApiResponse");
//                for (int i = 0; i < incidents.length(); i++) {
//                    JSONObject incident = incidents.getJSONObject(i);
//                    double latitude = incident.getDouble("incident_latitude");
//                    double longitude = incident.getDouble("incident_longitude");
//                    hotspots.add(new LatLng(latitude, longitude));
//                }
//            } catch (JSONException e) {
//                Log.e("GetDirectionsTask", "Error parsing hotspots from API response", e);
//            }
//
//            return hotspots;
//        }

    public class SendPostRequestTask extends AsyncTask<String, Void, String> {
        public String responseBody;

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String jsonInputString = params[1];
            try {
                Log.d("sendPostRequest", "API url: " + url);
                // Create URL object
                URL apiUrl = new URL(url);

                // Open a connection
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

                // Set the request method to POST
                connection.setRequestMethod("POST");

                // Set the necessary headers
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("accept", "*/*");

                // Enable input/output streams
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Write the JSON payload to the request body
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response from the server
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response body using an InputStream
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        responseBody = response.toString();
                    }
                } else {
                    // Read the error response if there's an issue
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        responseBody = "Error: " + responseCode + ", " + errorResponse.toString();
                    }
                }

                // Print the response
                Log.d("sendPostRequest", "Response Code: " + responseCode);
                Log.d("sendPostRequest", "Response Body: " + responseBody);

                // Close the connection
                connection.disconnect();
                return responseBody;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String responseBody) {
            super.onPostExecute(responseBody);
            try {
                // Parse the response body to get the points of the new route
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject routeObject = routesArray.getJSONObject(0);
                JSONArray legsArray = routeObject.getJSONArray("legs");
                JSONObject legObject = legsArray.getJSONObject(0);
                JSONArray pointsArray = legObject.getJSONArray("points");
                JSONObject pointObject = pointsArray.getJSONObject(0);
                List<LatLng> newRoutePoints = new ArrayList<>();
                for (int i = 0; i < pointsArray.length(); i++) {
                    JSONObject point = pointsArray.getJSONObject(i);
                    double latitude = point.getDouble("latitude");
                    double longitude = point.getDouble("longitude");
                    newRoutePoints.add(new LatLng(latitude, longitude));
                }

                // Draw the new route on the map
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(newRoutePoints)
                        .color(Color.GREEN)
                        .width(5);
                googleMap.addPolyline(polylineOptions);
            } catch (JSONException e) {
                Log.e("sendPostRequest", "Invalid JSON string: " + responseBody, e);
                // Handle the exception...
            }
        }
    }

    @SuppressLint("LongLogTag")
    public void drawHotspots() {
        Log.d("parseHotspotsFromApiResponse", "hotspots squares inside");
        if (hotspots != null && hotspots.size() > 0) {
            List<JSONObject> avoidAreas = new ArrayList<>();

            for (LatLng hotspot : hotspots) {
                // Create a circle options object
                CircleOptions circleOptions = new CircleOptions()
                        .center(hotspot)  // Set the center of the circle to the hotspot
                        .radius(804.672)  // 0.5 miles in meters (1 mile = 1609.34 meters)
                        .strokeColor(Color.RED)  // Outline color
                        .fillColor(Color.parseColor("#80FF0000"));  // Fill color with some transparency

                // Add the circle to the map
                googleMap.addCircle(circleOptions);

                // Create an imaginary square around the hotspot
                LatLngBounds squareBounds = getSquareBounds(hotspot, 804.672);
                JSONObject squareJson = new JSONObject();
                try {
                    JSONObject southWest = new JSONObject();
                    southWest.put("latitude", squareBounds.southwest.latitude);
                    southWest.put("longitude", squareBounds.southwest.longitude);

                    JSONObject northEast = new JSONObject();
                    northEast.put("latitude", squareBounds.northeast.latitude);
                    northEast.put("longitude", squareBounds.northeast.longitude);

                    squareJson.put("southWestCorner", southWest);
                    squareJson.put("northEastCorner", northEast);

                    avoidAreas.add(squareJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject avoidAreasJson = new JSONObject();
            try {
                JSONArray rectanglesArray = new JSONArray(avoidAreas);
                avoidAreasJson.put("rectangles", rectanglesArray);
            } catch (JSONException e) {
                Log.e("RerouteTask", "Error formatting avoidAreas for API request", e);
            }


            // Create the parent JSON object
            JSONObject parentJsonObject = new JSONObject();
            try {
                parentJsonObject.put("avoidAreas", avoidAreasJson);
            } catch (JSONException e) {
                Log.e("RerouteTask", "Error creating parent JSON object", e);
            }
            Log.d("parseHotspotsFromApiResponse", "hotspots squares = "+parentJsonObject);

            String apiUrl = "https://api.tomtom.com/routing/1/calculateRoute/"
                    + startLatLng.latitude + "%2C" + startLatLng.longitude
                    + "%3A" + endLatLng.latitude + "%2C" + endLatLng.longitude
                    + "/json?key="+ADD_KEY;

            new SendPostRequestTask().execute(apiUrl, parentJsonObject.toString());
        }
    }

    public LatLngBounds getSquareBounds(LatLng center, double radius) {
        // Calculate the distance between the center and each side of the square
        double distanceFromCenterToSide = radius * Math.sqrt(2);

        // Calculate the coordinates of the square corners
        LatLng southWest = SphericalUtil.computeOffset(center, distanceFromCenterToSide, 225);
        LatLng northEast = SphericalUtil.computeOffset(center, distanceFromCenterToSide, 45);

        // Create and return a LatLngBounds object for the square
        return new LatLngBounds(southWest, northEast);
    }
}
