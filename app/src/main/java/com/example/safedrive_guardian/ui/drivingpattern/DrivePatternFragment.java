package com.example.safedrive_guardian.ui.drivingpattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Priority;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.Console;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

import com.example.safedrive_guardian.R;
import com.google.android.material.snackbar.Snackbar;

public class DrivePatternFragment extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        SensorEventListener{
    private Button searchBtn, dirBtn, startBtn, backBtn;
    private EditText searchFld;
    private TextView txtSpeedLimit, txtCurrentSpeed;
    private int turns, changeAcc = 0;
    private final float totalScore = 10;
    private GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker, mUserLocationMarker;
    LocationRequest mLocationRequest;
    int PROXIMITY_RADIUS = 10000;
    double lat, lng;
    double endLat, endLng;
    int i = 0;
    long timeEnd, timeStart;
    String timeStr;
    private boolean isRunning;
    private boolean isPaused;
    private long start = 0;
    private long pauseStart = 0;
    private long end = 0;
    private List<String> details = new ArrayList<>(4);
    int countLimitExceed = 0;
    String countSlimitExceed;
    String timeLimitExceed;
    int maxSpeed = 0;
    String sMaxSpeed;
    String User_Name;
    private float currentSpeed = 0.0f;
    String speedLimit;
    int flag = 0;
    private boolean RainAndSnow;
    private int countSuddenBreaks = 0;
    long timeBreakStart, timeBreakEnd;
    float tmpSpeed = 0;

    // sensor variables
    // for gryo
    public static final float TOLERANCE = 0.000000001f;
    public static final int TIME_CONSTANT = 10;
    private static final float NS_TO_S = 1.0f / 1000000000.0f;
    int operationCount = 1;
    float pitchOutput, rollOutput, yawOutput;
    // counter for sensor fusion
    int sensorFusionYawCount = 0;
    int sensorFusionPitchCount = 0;
    //counter for quaternion
    int quaternionYawCount = 0;
    int quaternionPitchCount = 0;

    // final pitch and yaw values
    int accumulatedYaw = 0;
    int accumulatedPitch = 0;

    //counter for accelerometer reading
    int offsetX = 0;
    int offsetY = 0;
    float[] magneticFieldValues;
    float[] gravityValues;
    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
    Float pitch = 0f;
    Float roll = 0f;
    Float yaw = 0f;
    Float pitchQuaternion = 0f;
    Float rollQuaternion = 0f;
    Float yawQuaternion = 0f;
    Float newPitchSensorFusion = 0f;
    Float newRollSensorFusion = 0f;
    Float newYawSensorFusion = 0f;

    Float newPitchQuaternionSensorFusion = 0f;
    Float newRollQuaternionSensorFusion = 0f;
    Float newYawQuaternionSensorFusion = 0f;
    float mPitch, mRoll, mYaw;
    // for accelerometer
    float xAcc;
    float yAcc;
    float zAcc;
    float xPreviousAccel;
    float yPreviousAccel;
    float zPreviousAccel;
    float xCalibratedAccel = 0f;
    float yCalibratedAccel = 0f;
    float zCalibratedAccel = 0f;
    boolean isWriteEnabled = false;
    TextView yawTextView, pitchTextView, yawQTextView, pitchQTextView, xTextView, yTextView;
    private SensorManager sensorManager = null;
    // angular speeds from gyro
    private float[] gyroValues = new float[3];
    // rotation matrix from gyro data
    private float[] matrixGyro = new float[9];
    // orientation angles from gyro matrix
    private float[] orientationGyro = new float[3];
    // magnetic field vector
    private float[] magnetValues = new float[3];
    // accelerometer vector
    private float[] accelValues = new float[3];
    // orientation angles from accel and magnet
    private float[] orientationAccel = new float[3];
    // final orientation angles from sensor fusion
    private float[] orientationFused = new float[3];
    // accelerometer and magnetometer based rotation matrix
    private float[] matrixRotation = new float[9];
    private float timestamp;
    private boolean initState = true;
    private Timer fuseTimer = new Timer();
    private String SHARED_PREF_NAME = "drivingpatternapp";
    private boolean isInitialized = false;
    Boolean isYAccelerationChanged = false;
    Boolean isXAccelerationChanged = false;
    private String location;

    // for 30 sec sensor values reset
    int finalYaw = 0;
    int finalPitch = 0;
    int finalX = 0;
    int finalY = 0;
    Boolean areBrakesApplied = false;
    TextView drivingScore;
    int speedLimitInMph;
    double overallSafeScore = 0;
    double averageScore =0;
    double previousScore = 0;
    List<Double> scoreValues;
    ScoreArray scoreArrayList;
    RelativeLayout mainRelativeLayout;
    AutocompleteSupportFragment startAddressFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_drive_pattern);
        dirBtn = findViewById(R.id.Btnto);
        searchBtn = findViewById(R.id.Btnsearch);
        startBtn = findViewById(R.id.Btnstart);
        backBtn = findViewById(R.id.Btnback);
        txtSpeedLimit = findViewById(R.id.speedLimit);
        txtCurrentSpeed = findViewById(R.id.currentSpeed);
        drivingScore = findViewById(R.id.score);
        mainRelativeLayout = findViewById(R.id.main_layout);
        startAddressFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.startAddressAutoCompleteFragment);
        User_Name = "Darshan";

        scoreValues = new ArrayList<>();
        Places.initialize(getApplicationContext(), "AIzaSyBJ7iTZYsaNhmO9NvvCQvv6xcxyBLXd054");
        PlacesClient placesClient = Places.createClient(this);
        startAddressFragment.getView().setBackgroundColor(Color.WHITE);
        startAddressFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        startAddressFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                location = place.getName();
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle any errors
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                started(view);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // computing sensor values
        orientationGyro[0] = 0.0f;
        orientationGyro[1] = 0.0f;
        orientationGyro[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        matrixGyro[0] = 1.0f;
        matrixGyro[1] = 0.0f;
        matrixGyro[2] = 0.0f;
        matrixGyro[3] = 0.0f;
        matrixGyro[4] = 1.0f;
        matrixGyro[5] = 0.0f;
        matrixGyro[6] = 0.0f;
        matrixGyro[7] = 0.0f;
        matrixGyro[8] = 1.0f;

        // get sensorManager and initialise sensor listeners
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        initListeners();
        // wait for one second until gyroscope and magnetometer/accelerometer
        // data is initialised then schedule the complementary filter task
        fuseTimer.scheduleAtFixedRate(new deriveFusedOrientation(),
                2000, TIME_CONSTANT);
        // analysing behavior every 2 sec
        fuseTimer.scheduleAtFixedRate(new BehaviorAnalysis(), 1000, 2000);
        fuseTimer.scheduleAtFixedRate(new ResetSensorValues(), 1000, 30000);
    }

    // initializing the sensors
    public void initListeners() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void started(View view) {
        View fragmentView = startAddressFragment.getView();
        if (location.isEmpty()) {
            Toast.makeText(this, "\"Cannot be blank\"", Toast.LENGTH_SHORT).show();
        } else {
            if (i == 0) {
                startBtn.setText("END");
                timeStart = System.currentTimeMillis();
                timeBreakStart = System.currentTimeMillis();
                countSuddenBreaks = 0;
                changeAcc = 0;
                scoreValues.clear();
                i = 1;
                LatLng latLng = new LatLng(lat, lng);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                if (fragmentView != null) {
                    ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
                    layoutParams.height = 0;
                    fragmentView.setLayoutParams(layoutParams);
                }
                searchBtn.setVisibility(View.GONE);
                dirBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
                txtCurrentSpeed.setVisibility(View.VISIBLE);
                txtSpeedLimit.setVisibility(View.VISIBLE);
            } else {
                startBtn.setText("START");
                timeEnd = System.currentTimeMillis();
                long tDelta = timeEnd - timeStart;
                double elapsedSeconds = tDelta / 1000.0;
                int hours = (int) (elapsedSeconds / 3600);
                int minutes = (int) ((elapsedSeconds % 3600) / 60);
                int seconds = (int) (elapsedSeconds % 60);
                timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                long elapsed = stop();
                double tseconds = ((double) elapsed / 1000000000.0);
                int shours = (int) (tseconds / 3600);
                int sminutes = (int) ((tseconds % 3600) / 60);
                int sseconds = (int) (tseconds % 60);
                timeLimitExceed = String.format("%02d:%02d:%02d", shours, sminutes, sseconds);
                countSlimitExceed = Integer.toString(countLimitExceed);
                sMaxSpeed = Integer.toString(maxSpeed);
                i = 0;
                details.clear();
                scoreArrayList = new ScoreArray(scoreValues);
                averageScore = scoreArrayList.getAverage();
                double result = Math.round(averageScore *100)/100.0;
                if (result >= 5) {
                    Toast.makeText(getApplicationContext(), "Good Driving, Trip Score: " + result, Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Poor Driving, Trip Score: " + result, Toast.LENGTH_LONG).show();
                }
                onadd();
            }

        }

    }

    public long elapsed() {
        if (isRunning()) {
            if (isPaused())
                return (pauseStart - start);
            return (System.nanoTime() - start);
        } else
            return (end - start);
    }

    public String toStringText() {
        long enlapsed = elapsed();
        return ((double) enlapsed / 1000000000.0) + " Seconds";
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void start() {
        start = System.nanoTime();
        isRunning = true;
        isPaused = false;
        pauseStart = -1;
    }

    public long stop() {
        if (!isRunning()) {
            return -1;
        } else if (isPaused()) {
            isRunning = false;
            isPaused = false;

            return pauseStart - start;
        } else {
            end = System.nanoTime();
            isRunning = false;
            return end - start;
        }
    }

    public long pause() {
        if (!isRunning()) {
            return -1;
        } else if (isPaused()) {
            return (pauseStart - start);
        } else {
            pauseStart = System.nanoTime();
            isPaused = true;
            return (pauseStart - start);
        }
    }

    public void resume() {
        if (isPaused() && isRunning()) {
            start = System.nanoTime() - (pauseStart - start);
            isPaused = false;
        }
    }

    public void onadd() {
        // all the attributes are added to list
        details.add("Total Time: " + timeStr);
        details.add("Max Speed: " + sMaxSpeed);
        details.add("LimitExceedTime: " + timeLimitExceed);
        details.add("LimitExceedCount: " + countSlimitExceed);
        details.add("suddenBreaksCount: " + countSuddenBreaks);
        details.add("suddenAcceleration: " + changeAcc);
        String sScore = Double.toString(Math.round(averageScore * 100) / 100.0);
        details.add("Score :" + sScore);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy' 'HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));
        details.add("DataAndTime :" + sdf.format(new Date()));
        //Add all the data in Firebase

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                gMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }

        gMap.setOnMarkerDragListener(this);
        gMap.setOnMarkerClickListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onClick(View v) {
        Object[] dataTransfer = new Object[2];

        switch (v.getId()) {
            case R.id.Btnsearch: {
                gMap.clear();
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                Log.d("location = ", location);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {
                        Address myAddress = addressList.get(0);
                        LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                        markerOptions.position(latLng);
                        gMap.addMarker(markerOptions);
                        gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        endLat = myAddress.getLatitude();
                        endLng = myAddress.getLongitude();
                    }

                }
            }
            break;

            case R.id.Btnto:
                dataTransfer = new Object[3];
                String url = getDirectionsUrl();
                GetDirData getDirectionsData = new GetDirData();
                dataTransfer[0] = gMap;
                dataTransfer[1] = url;
                GetDirectionsTask getDirectionsTask = new GetDirectionsTask();
                getDirectionsTask.setGoogleMap(gMap);
                LatLng startAddress = new LatLng(lat, lng);
                LatLng endAddress = new LatLng(endLat, endLng);
                getDirectionsTask.execute(startAddress, endAddress);
                dataTransfer[2] = new LatLng(endLat, endLng);
                getDirectionsData.execute(dataTransfer);
                break;
        }
    }

    private String getDirectionsUrl() {

        return "https://maps.googleapis.com/maps/api/directions/json?" + "origin=" + lat + "," + lng +
                "&destination=" + endLat + "," + endLng +
                "&key=" + "AIzaSyBJ7iTZYsaNhmO9NvvCQvv6xcxyBLXd054";
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=").append(nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyBJ7iTZYsaNhmO9NvvCQvv6xcxyBLXd054>");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.####");
        return Double.parseDouble(twoDForm.format(d));
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (mUserLocationMarker != null) {
            mUserLocationMarker.remove();
        }

        lat = location.getLatitude();
        lng = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = gMap.addMarker(markerOptions);

        //move map camera
        if (i == 1) {
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
        speedLimit = String.valueOf(40);
        double kph = (Double.parseDouble(speedLimit)) * 0.621;
        speedLimitInMph = (int) Math.round(kph);
        txtSpeedLimit.setText("Limit:" + "" + speedLimitInMph);
        pause();
        currentSpeed = location.getSpeed() * 2.23f;
        CharSequence text = "Speed Limit Exceeded!";
        timeBreakEnd = System.currentTimeMillis();
        long breakElapsed = timeBreakStart - timeBreakEnd;
        double breakElapsedSeconds = breakElapsed / 1000.0;
        int breakSeconds = (int) (breakElapsedSeconds % 60);
        if (breakSeconds % 5 == 0) {
            tmpSpeed = currentSpeed;
        }
        if (breakSeconds % 2 == 0 && tmpSpeed >= 35 && (tmpSpeed - currentSpeed >= 20)) {

            countSuddenBreaks++;
            areBrakesApplied = true;
        } else {
            areBrakesApplied = false;
        }
        if (breakSeconds % 2 == 0 && currentSpeed - tmpSpeed >= 20) {
            changeAcc++;
        }
        if (currentSpeed > speedLimitInMph) {
            if (!isRunning()) {
                start();
            } else {
                resume();
            }
            if (flag == 0) {
                countLimitExceed++;
                flag = 1;
            }
        }
        if (currentSpeed < speedLimitInMph) {
            flag = 0;
        }
        if (maxSpeed < currentSpeed) {
            maxSpeed = (int) currentSpeed;
        }
//                    requestQueue.add(request);
        txtCurrentSpeed.setText("Speed: " + new DecimalFormat("##").format(currentSpeed));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void back(View view) {
        searchBtn.setVisibility(View.VISIBLE);
        dirBtn.setVisibility(View.VISIBLE);
        View fragmentView = startAddressFragment.getView();
        if (fragmentView != null) {
            ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            fragmentView.setLayoutParams(layoutParams);
        }
        LinearLayout one = findViewById(R.id.linearLayout);
        one.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);
        txtCurrentSpeed.setVisibility(View.INVISIBLE);
        txtSpeedLimit.setVisibility(View.INVISIBLE);
        gMap.stopAnimation();
        gMap.clear();
        startAddressFragment.setText("");
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        gMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        endLat = marker.getPosition().latitude;
        endLng = marker.getPosition().longitude;

//        Log.d("end_lat", "" + endLat);
//        Log.d("end_lng", "" + endLng);
        return false;
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        endLat = marker.getPosition().latitude;
        endLng = marker.getPosition().longitude;

        Log.d("end_lat", "" + endLat);
        Log.d("end_lng", "" + endLng);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // for sensor
    // Sensor Fusion involving Accelerometer, Gyroscope, and Magnetometer
    // Quaternion
    // Accelerometer
    @Override
    public void onSensorChanged(SensorEvent event) {
        refreshSensorData();
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravityValues = event.values;
                xAcc = event.values[0];
                yAcc = event.values[1];
                zAcc = event.values[2];
                configureAccelerometer();
                // copy new accelerometer data into accel array
                // then calculate new orientation
                System.arraycopy(event.values, 0, accelValues, 0, 3);
                calculateOrientationFromAccMag();
                break;

            case Sensor.TYPE_GYROSCOPE:
                // process gyro data
                processGyroscopeData(event);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // copy new magnetometer data into magnet array
                magneticFieldValues = event.values;
                System.arraycopy(event.values, 0, magnetValues, 0, 3);
                break;
        }
        formulateQuaternion();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void configureAccelerometer() {
        if (!isInitialized) {
            xPreviousAccel = xAcc;
            yPreviousAccel = yAcc;
            zPreviousAccel = zAcc;
            isInitialized = true;
        } else {
            xCalibratedAccel = (xPreviousAccel - xAcc);
            yCalibratedAccel = (yPreviousAccel - yAcc);
            zCalibratedAccel = (zPreviousAccel - zAcc);
            xPreviousAccel = xAcc;
            yPreviousAccel = yAcc;
            zPreviousAccel = zAcc;
        }
    }

    private void formulateQuaternion() {
        float[] R = new float[9];
        float[] I = new float[9];
        if (magneticFieldValues != null && gravityValues != null) {
            boolean success = SensorManager.getRotationMatrix(R, I, gravityValues, magneticFieldValues);
            if (success) {
                float[] mOrientation = new float[3];
                float[] mQuaternion = new float[4];
                SensorManager.getOrientation(R, mOrientation);

                SensorManager.getQuaternionFromVector(mQuaternion, mOrientation);

                mYaw = mQuaternion[1]; // orientation contains: azimuth(yaw), pitch and Roll
                mPitch = mQuaternion[2];
                mRoll = mQuaternion[3];

                newPitchQuaternionSensorFusion = pitchQuaternion - mPitch;
                newRollQuaternionSensorFusion = rollQuaternion - mRoll;
                newYawQuaternionSensorFusion = yawQuaternion - mYaw;

                pitchQuaternion = mPitch;
                rollQuaternion = mRoll;
                yawQuaternion = mYaw;
            }
        }
    }

    private void refreshSensorData() {
        if (newPitchSensorFusion != 0 && newPitchQuaternionSensorFusion != 0 && newYawSensorFusion != 0 && newYawQuaternionSensorFusion != 0 && xCalibratedAccel != 0 && yCalibratedAccel != 0) {
            isWriteEnabled = false;
            isXAccelerationChanged = false;
            isYAccelerationChanged = false;
            operationCount = operationCount + 1;
            if (operationCount == 2250) {
                operationCount = 1;
            }

            if (newYawSensorFusion > .30 || newYawSensorFusion < -.30) {
                sensorFusionYawCount = sensorFusionYawCount + 1;
                isWriteEnabled = true;
            }

            if (newPitchSensorFusion > .12 || newPitchSensorFusion < -.12) {
                sensorFusionPitchCount = sensorFusionPitchCount + 1;
                isWriteEnabled = true;
            }

            if (newYawQuaternionSensorFusion > .30 || newYawQuaternionSensorFusion < -.30) {
                quaternionYawCount = quaternionYawCount + 1;
                isWriteEnabled = true;
            }

            if (newPitchQuaternionSensorFusion > .12 || newPitchQuaternionSensorFusion < -.12) {
                quaternionPitchCount = quaternionPitchCount + 1;
                isWriteEnabled = true;
            }

            if (xCalibratedAccel > 3 || xCalibratedAccel < -3) {
                offsetX = offsetX + 1;
                isWriteEnabled = true;
                isXAccelerationChanged = true;
            }

            if (yCalibratedAccel > 2.5 || yCalibratedAccel < -2.5) {
                offsetY = offsetY + 1;
                isWriteEnabled = true;
                isYAccelerationChanged = true;
            }

            // computing final values for pitch and yaw counters
            if (sensorFusionPitchCount != 0 || quaternionPitchCount != 0) {
                accumulatedPitch = (int) (sensorFusionPitchCount + 0.3 * quaternionPitchCount);
            }

            if (sensorFusionYawCount != 0 || quaternionYawCount != 0) {
                accumulatedYaw = (int) (sensorFusionYawCount + 0.4 * quaternionYawCount);
            }

            /*

            Here, one counter on any sensor doesn't reflect the crossing of threshold for 1 time,
            it just gives the total number of times the data was recorded during "1 crossing"
            For one time the user makes a rash turn, counter was reach upto 10 for that one single incident

            */

            // only saving if there is change in the counters
            if (isWriteEnabled) {

                //Creating a shared preference
                SharedPreferences sharedPreferences = DrivePatternFragment.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                //Creating editor to store values to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //Adding values to editor
                editor.putInt("overPitch", accumulatedPitch);
                editor.putInt("overYaw", accumulatedYaw);
                editor.putInt("overX", offsetX);
                editor.putInt("overY", offsetY);

                //Saving values to editor
                editor.apply();
                Log.i("MapsActivity", "finalOverPitch : " + accumulatedPitch);
            }
        }
    }

    public void calculateOrientationFromAccMag() {
        if (SensorManager.getRotationMatrix(matrixRotation, null, accelValues, magnetValues)) {
            SensorManager.getOrientation(matrixRotation, orientationAccel);
        }
    }

    public void processGyroscopeData(SensorEvent event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (orientationAccel == null)
            return;

        // initialisation of the gyroscope based rotation matrix
        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = calculateRotationMatrixFromOrientation(orientationAccel);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            matrixGyro = calculateMatrixProduct(matrixGyro, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS_TO_S;
            System.arraycopy(event.values, 0, gyroValues, 0, 3);
            calculateRotationVectorFromGyro(gyroValues, deltaVector, dT / 2.0f);
        }

        // measurement done, save current time for next interval
        timestamp = event.timestamp;

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        // apply the new rotation interval on the gyroscope based rotation matrix
        matrixGyro = calculateMatrixProduct(matrixGyro, deltaMatrix);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(matrixGyro, orientationGyro);
    }

    private float[] calculateMatrixProduct(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    private void calculateRotationVectorFromGyro(float[] gyroValues,
                                                 float[] deltaRotationVector,
                                                 float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float) Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > TOLERANCE) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    private float[] calculateRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (displayPitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (displayRoll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (displayRoll, displayPitch, azimuth)
        float[] resultMatrix = calculateMatrixProduct(xM, yM);
        resultMatrix = calculateMatrixProduct(zM, resultMatrix);
        return resultMatrix;
    }

    private class deriveFusedOrientation extends TimerTask {
        float filter_coefficient = 0.85f;
        float oneMinusCoeff = 1.0f - filter_coefficient;

        public void run() {
            // Azimuth
            if (orientationGyro[0] < -0.5 * Math.PI && orientationAccel[0] > 0.0) {
                orientationFused[0] = (float) (filter_coefficient * (orientationGyro[0] + 2.0 * Math.PI) + oneMinusCoeff * orientationAccel[0]);
                orientationFused[0] -= (orientationFused[0] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (orientationAccel[0] < -0.5 * Math.PI && orientationGyro[0] > 0.0) {
                orientationFused[0] = (float) (filter_coefficient * orientationGyro[0] + oneMinusCoeff * (orientationAccel[0] + 2.0 * Math.PI));
                orientationFused[0] -= (orientationFused[0] > Math.PI) ? 2.0 * Math.PI : 0;
            } else
                orientationFused[0] = filter_coefficient * orientationGyro[0] + oneMinusCoeff * orientationAccel[0];

            // Pitch
            if (orientationGyro[1] < -0.5 * Math.PI && orientationAccel[1] > 0.0) {
                orientationFused[1] = (float) (filter_coefficient * (orientationGyro[1] + 2.0 * Math.PI) + oneMinusCoeff * orientationAccel[1]);
                orientationFused[1] -= (orientationFused[1] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (orientationAccel[1] < -0.5 * Math.PI && orientationGyro[1] > 0.0) {
                orientationFused[1] = (float) (filter_coefficient * orientationGyro[1] + oneMinusCoeff * (orientationAccel[1] + 2.0 * Math.PI));
                orientationFused[1] -= (orientationFused[1] > Math.PI) ? 2.0 * Math.PI : 0;
            } else
                orientationFused[1] = filter_coefficient * orientationGyro[1] + oneMinusCoeff * orientationAccel[1];

            // Roll
            if (orientationGyro[2] < -0.5 * Math.PI && orientationAccel[2] > 0.0) {
                orientationFused[2] = (float) (filter_coefficient * (orientationGyro[2] + 2.0 * Math.PI) + oneMinusCoeff * orientationAccel[2]);
                orientationFused[2] -= (orientationFused[2] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (orientationAccel[2] < -0.5 * Math.PI && orientationGyro[2] > 0.0) {
                orientationFused[2] = (float) (filter_coefficient * orientationGyro[2] + oneMinusCoeff * (orientationAccel[2] + 2.0 * Math.PI));
                orientationFused[2] -= (orientationFused[2] > Math.PI) ? 2.0 * Math.PI : 0;
            } else
                orientationFused[2] = filter_coefficient * orientationGyro[2] + oneMinusCoeff * orientationAccel[2];

            // Overwrite gyro matrix and orientation with fused orientation to comensate gyro drift
            matrixGyro = calculateRotationMatrixFromOrientation(orientationFused);
            System.arraycopy(orientationFused, 0, orientationGyro, 0, 3);

            pitchOutput = orientationFused[1];
            rollOutput = orientationFused[2];
            yawOutput = orientationFused[0];

            // present instance values
            newPitchSensorFusion = pitch - pitchOutput;
            newRollSensorFusion = roll - rollOutput;
            newYawSensorFusion = yaw - yawOutput;

            // saving values for calibration
            pitch = pitchOutput;
            roll = rollOutput;
            yaw = yawOutput;
        }

    }

    private class BehaviorAnalysis extends TimerTask {

        float speedLimit;
        int factorSpeed = 0;
        int factorBrakes = 0;
        int factorAcceleration = 0;
        int factorTurn = 0;

        //calculate rateOverYaw and rateOverPitch by taking the division of pitch/yaw over 30 sec interval
        double rateOverPitch = accumulatedPitch / operationCount;
        double rateOverYaw = accumulatedYaw / operationCount;

        @Override
        public void run() {
            if (speedLimitInMph != 0) {
                speedLimit = speedLimitInMph;
            } else {
                speedLimit = 0;
            }

            if (currentSpeed != 0) {
                if (currentSpeed > speedLimit) {
                    factorSpeed = 10;
                    runOnUiThread(() -> {
                        Snackbar.make(mainRelativeLayout, "You speed is above the limit, please drive within the speedlimit", Snackbar.LENGTH_SHORT).show();
                        playSound();
                    });
                } else {
                    factorSpeed = 1;
                }

                if (areBrakesApplied) {
                    factorBrakes = 10;
                    runOnUiThread(() -> {
                        Snackbar.make(mainRelativeLayout, "You shouldn't apply sudden brakes, please be careful", Snackbar.LENGTH_SHORT).show();
                        playSound();
                    });
                } else {
                    factorBrakes = 0;
                }

                // writeCheck is the boolean used above to indicate the change in counters in turn and acc
                if (isWriteEnabled) {

                    if (rateOverPitch < 0.04) {
                        if (isXAccelerationChanged) {
                            // likely unsafe
                            factorAcceleration = 8;
                        } else {
                            // likely safe
                            factorAcceleration = 2;
                        }
                    } else {
                        if (isXAccelerationChanged) {
                            // definitely unsafe
                            factorAcceleration = 10;
                            runOnUiThread(() -> {
                                Snackbar.make(mainRelativeLayout, "Harsh acceleration has been detected, please be safe", Snackbar.LENGTH_SHORT).show();
                                playSound();
                            });
                        } else {
                            // probably unsafe
                            factorAcceleration = 8;
                        }
                    }

                    if (rateOverYaw < 0.01) {
                        if (isYAccelerationChanged) {
                            // likely unsafe
                            factorTurn = 8;
                        } else {
                            // likely safe
                            factorTurn = 2;
                        }
                    } else {
                        if (isYAccelerationChanged) {
                            runOnUiThread(() -> {
                                Snackbar.make(mainRelativeLayout, "Harsh unsafe turn has been detected, please be safe", Snackbar.LENGTH_SHORT).show();
                                playSound();
                            });
                            // definitely unsafe
                            factorTurn = 10;
                        } else {
                            // probably unsafe
                            factorTurn = 8;
                        }
                    }
                } else {
                    factorAcceleration = 0;
                    factorTurn = 0;
                }
            }

            double unsafeScore = 0.3 * factorSpeed + 0.2 * factorBrakes + 0.2 * factorAcceleration + 0.2 * factorTurn;
            if (unsafeScore < 10) {
                overallSafeScore = 10 - unsafeScore;
            }

            if (unsafeScore > 10) {
                overallSafeScore = 0;
            }
//            final double finalSafeScore = safeScore;
            // taking average with the previous score of user
            if (previousScore != 0) {
                overallSafeScore = (overallSafeScore + previousScore) / 2;
            }
            scoreValues.add(overallSafeScore);
            runOnUiThread(() -> drivingScore.setText("Score : " + decimalFormat.format(overallSafeScore)));
            previousScore = overallSafeScore;
            Log.i("MapsActivity", "count : " + operationCount);
            Log.i("MapsActivity", "score : " + overallSafeScore);
            Log.i("MapsActivity", "final Pitch rate : " + rateOverPitch);
            Log.i("MapsActivity", "final Yaw rate : " + rateOverYaw);
        }
    }

    private class ResetSensorValues extends TimerTask {

        @Override
        public void run() {
            accumulatedYaw = accumulatedYaw - finalYaw;
            accumulatedPitch = accumulatedPitch - finalPitch;
            offsetX = offsetX - finalX;
            offsetY = offsetY - finalY;

            finalPitch = accumulatedPitch;
            finalYaw = accumulatedYaw;
            finalX = offsetX;
            finalY = offsetY;
            Log.i("MapsActivity", "final Pitch : " + accumulatedPitch);
            Log.i("MapsActivity", "final Yaw : " + accumulatedYaw);
            Log.i("MapsActivity", "final overX : " + offsetX);
            Log.i("MapsActivity", "final overY : " + offsetY);
        }
    }

    private void playSound() {
        MediaPlayer player = MediaPlayer.create(this,
                Settings.System.DEFAULT_NOTIFICATION_URI);
        player.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.reset();
            mediaPlayer.release();
        });
        player.start();
    }
}