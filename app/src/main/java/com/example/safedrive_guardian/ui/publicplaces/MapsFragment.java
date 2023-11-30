package com.example.safedrive_guardian.ui.publicplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safedrive_guardian.R;
import com.example.safedrive_guardian.databinding.FragmentHomeBinding;
import com.example.safedrive_guardian.databinding.FragmentMapsBinding;
import com.example.safedrive_guardian.ui.home.HomeViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MapsFragment extends Fragment implements GoogleMap.OnMapClickListener {

    private FragmentMapsBinding fragmentMapsBinding;
    private String TAG = "APP: MapsActivity";

    private GoogleMap mMap;

//    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_code = 101;
    protected double curr_lat, curr_log;
    private double prev_lat = Integer.MAX_VALUE, prev_log = Integer.MAX_VALUE;

//    private Button searchButton;
    private String location_types = "school|hospital|park";
    private String search_radius = "1000";// in meter
    private double updated_radius = (1000 / 1000) / 2.0;// in meter
    private double btest_buff = 0.05;
    private int scaler = 1;
    protected TextView countTextView;
    protected TextView notificationView;
    protected CardView topBarCard;


    // Radius of the Earth in kilometers
    private static final double EARTH_RADIUS = 6371;
    private Marker currentLocationMarker;
    private static final double LOCATION_UPDATE_FACTOR = 0.001; // Factor to update location
    private LocationCallback locationCallback;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            mMap = googleMap;
            getCurrentLocation();

//            mMap.setMyLocationEnabled(true); // Enable the blue dot indicating user's location on the map

//            mMap.setOnMyLocationButtonClickListener(() -> {
//                // Handle the My Location button click here
//                // You can use mMap.getMyLocation() to get the current location
//                Log.d(TAG, "setOnMyLocationButtonClickListener: You can use mMap.getMyLocation() to get the current location");
//                return false;
//            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    Log.d(TAG, "setOnMyLocationButtonClickListener: You can use mMap.getMyLocation() to get the current location");
                    Toast.makeText(getContext(), "XXClicked on map at: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
//                    return false;

//                    getCurrentLocation();

                    handleAPIRequest(latLng.latitude, latLng.longitude);
                }

            });


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_maps, container, false);

        Log.d(TAG, "onCreateView: MapsFragment");
        PublicPlacesViewModel publicPlacesViewModel =
                new ViewModelProvider(this).get(PublicPlacesViewModel.class);

        fragmentMapsBinding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = fragmentMapsBinding.getRoot();

//        final TextView textView = binding.textHome;
//        publicPlacesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


//        searchButton = fragmentMapsBinding.myButton;
        countTextView = fragmentMapsBinding.countTextView;
        notificationView = fragmentMapsBinding.notificationView;
        topBarCard = fragmentMapsBinding.topBarCard;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());




//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                handleAPIRequest();
//
//
//            }
//        });



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: MapsFragment");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMapsBinding = null;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (Request_code) {

            case Request_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
        }

    }



    @Override
    public void onMapClick(LatLng latLng) {
        // Handle map click events here
        // latLng contains the clicked coordinates (latitude and longitude)
        Toast.makeText(getContext(), "Clicked on map at: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }


    private  void handleAPIRequest() {
        handleAPIRequest(curr_lat, curr_log);
    }

    private  void handleAPIRequest(double xlat, double xlog) {
        Log.d(TAG, "handleAPIRequest: ihandleAPIRequest "  );
        // Remove existing markers before adding new ones
            mMap.clear();
        FetchData hospitalData = fetchAndDisplayPlaces("hospital", xlat, xlog);
        FetchData schoolData = fetchAndDisplayPlaces("school", xlat, xlog);
        FetchData parkData = fetchAndDisplayPlaces("park", xlat, xlog);
//                fetchAndDisplayPlaces("park");


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if (countTextView != null) {
                    String countText = String.format("In your vicinity, there are %d hospitals, %d schools, and %d parks.", hospitalData.getHospitalCount(), schoolData.getSchoolCount(), parkData.getParkCount());
                    countTextView.setText(countText);
                }


                // Your function to be executed after 5 seconds
                int intersectingCount = hospitalData.getIntersectingCount() + schoolData.getIntersectingCount() + parkData.getIntersectingCount();
                Log.d(TAG, "handleAPIRequest: intersectingCount: " + intersectingCount );


                plotOriginalLocation(curr_lat, curr_log);
                if (notificationView != null){
                    String message = "";
                    if(intersectingCount >= 10){
                        message += "Approaching highly crowded area, prepare to slow down. Exercise caution and follow safety guidelines.";
                        topBarCard.setCardBackgroundColor(Color.RED);
                    }else if( intersectingCount < 7 && intersectingCount > 2){
                        message += "Moderate density :Stay vigilant and be aware of your surroundings. ";
                        topBarCard.setCardBackgroundColor(getResources().getColor(R.color.colorMediumLevel));
                    }else{
                        message += " Low Density : Drive safely, exercise normal precautions and enjoy your surroundings.";
                        topBarCard.setCardBackgroundColor(getResources().getColor(R.color.colorLowLevel));
                    }

                    notificationView.setText( message);

                    plotOriginalLocation(xlat, xlog);



                    // Set background color based on the level

                }
            }
        }, 3000);
    }


    private FetchData  fetchAndDisplayPlaces(String placeType ) {
        return fetchAndDisplayPlaces(placeType, curr_lat, curr_log);
    }

    private FetchData  fetchAndDisplayPlaces(String placeType, double lat , double log ) {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=" + lat + "," + log);
//        stringBuilder.append("location=" + curr_lat + "," + curr_log);
        stringBuilder.append("&radius=" + search_radius);
        stringBuilder.append("&type=" + placeType);
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=" + getResources().getString(R.string.google_maps_key));

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];

        dataFetch[0] = mMap;
        dataFetch[1] = url;

        Log.d(TAG, "onClick: url " + url);

        FetchData fetchData = new FetchData(placeType);
        fetchData.execute(dataFetch);


        return fetchData;

    }


    private void getCurrentLocation() {

        // check permission first
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);

            return;
        }




        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(6000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setFastestInterval(5000);
        locationRequest.setFastestInterval(60000);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                Toast.makeText(getApplicationContext(), " Location result is = "+locationResult., Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    Toast.makeText(getContext(), " Current location is null", Toast.LENGTH_LONG).show();
                    return;
                }


                for (Location location : locationResult.getLocations()) {

                    if (location != null) {
                        Toast.makeText(getContext(), " Current location is " + location.getLatitude() + " - " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    }

//
//                    curr_lat += LOCATION_UPDATE_FACTOR;
//                    curr_log += LOCATION_UPDATE_FACTOR;


                    // Calculate distance
                    double distance = calculateDistance(prev_lat, prev_log, curr_lat, curr_log);

                    // Print the result
                    System.out.println("Distance: " + distance + " km");
                    Log.d(TAG, "onLocationResult: Distance: " + distance + " km" + "updated_radius " + updated_radius + " curr_lat : " + curr_lat + " curr_log " + curr_log);

                    if (distance >= updated_radius) {
                        prev_lat = curr_lat;
                        prev_log = curr_log;
                        Log.d(TAG, "onLocationResult: FETCH NEW PLACES : ");
//                        fetchAndDisplayPlaces("hospital");
//                        fetchAndDisplayPlaces("school");

                        handleAPIRequest();

                        if(currentLocationMarker != null){
                            currentLocationMarker.remove();
                        }
                    }

                    LatLng latLng = new LatLng(curr_lat, curr_log);

//                    if (currentLocationMarker == null) {
//                        // Marker doesn't exist yet, create a new marker
//
//                        Log.d(TAG, "onLocationResult: Marker doesn't exist yet, create a new marker");
//                        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//                    } else {
//                        // Marker already exists, update its position
//                        Log.d(TAG, "onLocationResult: Marker already exists, update its position");
//
//                        currentLocationMarker.setPosition(latLng);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    }



//                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


                }
            }
        };


        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                scaler += 1;

                if (location != null) {
                    curr_lat = location.getLatitude();
                    curr_log = location.getLongitude();
                    Log.d(TAG, "addOnSuccessListener: curr_lat: " + curr_lat + " curr_log");


                    if (prev_log == Integer.MAX_VALUE) {
                        prev_log = curr_log;
                    }

                    if (prev_lat == Integer.MAX_VALUE) {
                        prev_lat = curr_lat;
                    }


                    LatLng latLng = new LatLng(curr_lat, curr_log);

                    // Remove the previous marker
//                    if (currentLocationMarker != null) {
//                        currentLocationMarker.remove();
//                    }


//                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
////                    mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    handleAPIRequest();
                }

            }
        });


    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Calculate differences
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        return EARTH_RADIUS * c;
    }



    private void plotOriginalLocation(double org_lat, double org_log){

        LatLng latLng =  new LatLng(org_lat, org_log);

        mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


//        currentLocationMarker.setPosition(latLng);
    }


    class FetchData extends AsyncTask<Object, String, String> {
        String googleNearByPlacesData;

        GoogleMap googleMap;

        String url;


        private static final double EARTH_RADIUS = 6371;

        private String TAG = "APP: FetchDataOld";
        private int marker_radius = 350;

        private int hospitalCount = 0;
        private int schoolCount = 0;
        private int parkCount = 0;
        private  double org_lat = curr_lat;
        private  double org_log = curr_log;
        private int intersectingCount = 0;
        private String type = "";

        public int getHospitalCount() {
            return hospitalCount;
        }

        public int getSchoolCount() {
            return schoolCount;
        }


        public int getParkCount() {
            return parkCount;
        }

        public FetchData(String type) {
            this.type = type;
        }

//    private List<Marker> markers = new ArrayList<>(); // Keep track of markers


//    public FetchDataOld(TextView countTextView,TextView notificationView,  double org_lat, double org_log) {
//        this.countTextView = countTextView;
//        this.org_lat = org_lat;
//        this.org_log = org_log;
//        this.notificationView = notificationView;
//    }
//        org_lat = curr_lat;



        @Override
        protected String doInBackground(Object... objects) {


            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googleNearByPlacesData = downloadUrl.retriveUrl(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return googleNearByPlacesData;
        }





        @Override
        protected void onPostExecute(String s) {
//        super.onPostExecute(s);

            // Remove existing markers before adding new ones
//            googleMap.clear();
//        plotOriginalLocation();




            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");
                Log.d(TAG, "onPostExecute:  jsonArray : "+jsonArray);

                for(int i= 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
                    Log.d(TAG, "onPostExecute: getLocation :"+getLocation);

                    String lat = getLocation.getString("lat");
                    String lng = getLocation.getString("lng");

                    JSONObject getName = jsonArray.getJSONObject(i);
                    String name = getName.getString("name");



//                    String rating = jsonObject1.getString("rating");


                    LatLng latLng = new LatLng(Double.parseDouble(lat) , Double.parseDouble(lng));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(name);
                    markerOptions.position(latLng);
                    markerOptions.alpha(0.8F);
                    markerOptions.draggable(true);

                    if(jsonObject1.has("vicinity")){
                        String vicinity = jsonObject1.getString("vicinity");
                        markerOptions.snippet("vicinity : " + vicinity);
                    }

                    if(jsonObject1.has("rating")){
                        String rating = jsonObject1.getString("rating");
                        markerOptions.snippet("rating : " + rating);
                    }


                    markerOptions.infoWindowAnchor(0.5f, 0.5f);
                    markerOptions.zIndex(1);



                    // Set marker icon based on the type of place
                    JSONArray typesArray = jsonObject1.getJSONArray("types");
                    if (typesArray.length() > 0) {
                        String placeType = typesArray.getString(0); // Get the first type
                        setMarkerIcon(markerOptions, placeType,   jsonObject1.optString("icon", ""));

                        // Increment the count for each type
                        switch (placeType) {
                            case "hospital":
                                hospitalCount++;
                                break;
                            case "school":
                                schoolCount++;
                                break;
                            case "park":
                                parkCount++;
                                break;
                        }

                    }

                    Marker marker= googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    // Draw a circle around the place with a radius of 1 km
                    drawCircle(marker.getPosition());

                    // Display the counts
//                    displayPlaceCounts();



                }



            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }


        private void displayPlaceCounts() {
            if (countTextView != null) {
                String countText = "Hospitals: " + hospitalCount + " Schools: " + schoolCount + " Parks: " + parkCount;
                countTextView.setText(countText);
            }


        }

        public int getIntersectingCount(){
            return intersectingCount;
        }


        private void setMarkerIcon(MarkerOptions markerOptions, String placeType, String iconUrl) {
            Log.d(TAG, "setMarkerIcon:  placeType "+placeType);
//        if (iconUrl != null && !iconUrl.isEmpty()) {
//            new AsyncTask<String, Void, BitmapDescriptor>() {
//                @Override
//                protected BitmapDescriptor doInBackground(String... urls) {
//                    return getBitmapDescriptorFromUrl(urls[0]);
//                }
//
//                @Override
//                protected void onPostExecute(BitmapDescriptor result) {
//                    // Set the bitmap as a marker icon on the UI thread
//                    markerOptions.icon(result);
//                }
//            }.execute(iconUrl);
//        } else {
            switch (placeType) {
                case "hospital":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                    break;
                case "school":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    break;
                case "park":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    break;
                // Add more cases for other types as needed
                default:
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    break;
            }
//        }

        }



        private void drawCircle(LatLng currentPlace) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(currentPlace)
                    .radius(marker_radius) // Radius in meters (1 km)
                    .strokeWidth(2) // Width of the circle's outline
                    .strokeColor(Color.BLUE) // Color of the circle's outline
                    .fillColor(Color.parseColor("#300000FF")); // Color of the circle's fill (with transparency)

            googleMap.addCircle(circleOptions);


            double distance = calculateDistance(
                    currentPlace.latitude, currentPlace.longitude,
                    org_lat,org_log);

            if (distance <= marker_radius) {
                intersectingCount++;
            }

        }



        private BitmapDescriptor getBitmapDescriptorFromUrl(String url) {
            try {
                InputStream inputStream = new URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return BitmapDescriptorFactory.fromBitmap(bitmap);
            } catch (IOException e) {
                return BitmapDescriptorFactory.defaultMarker(); // Default marker if there's an error
            }
        }
    }
}