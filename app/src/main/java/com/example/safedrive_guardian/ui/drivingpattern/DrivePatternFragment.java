package com.example.safedrive_guardian.ui.drivingpattern;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.safedrive_guardian.services.NotificationService;
import com.example.safedrive_guardian.ui.publicplaces.DownloadUrl;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Bundle;

import com.example.safedrive_guardian.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrivePatternFragment extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        SensorEventListener {
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
    NotificationService notificationService;
    private DatabaseReference databaseReference;
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
    double averageScore = 0;
    double previousScore = 0;
    List<Double> scoreValues;
    ScoreArray scoreArrayList;
    RelativeLayout mainRelativeLayout;
    AutocompleteSupportFragment startAddressFragment;
    private FirebaseAuth mAuth;


    /**
     * sc -variable declaration - start
     **/
    private String TAG = "APP: MapsActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_code = 101;
    protected double curr_lat, curr_log;
    private double prev_lat = Integer.MAX_VALUE, prev_log = Integer.MAX_VALUE;
    private String location_types = "school|hospital|park";
    private String search_radius = "1000";// in meter
    private double updated_radius = (1000 / 1000) / 2.0;// in meter
    private double btest_buff = 0.05;
    private int scaler = 1;
    protected TextView countTextView;
    protected TextView notificationView;
    protected CardView topBarCard;
    //    protected Button searchButton;
    private static final double EARTH_RADIUS = 6371;
    private Marker currentLocationMarker;
    private static final double LOCATION_UPDATE_FACTOR = 0.001; // Factor to update location
    private LocationCallback locationCallback;
    private int highThreshold = 10;
    private int mediumThreshold = 7;
    private int lowThreshold = 2;
    private String fcmToken = "";
    int weather_count = 0;
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

//    protected int tripHospitalCnt;
//    protected int tripParksCnt;
//    protected int tripSchoolCnt;

    private AtomicInteger tripHospitalCnt = new AtomicInteger(0);
    private AtomicInteger tripSchoolCnt = new AtomicInteger(0);
    private AtomicInteger tripParksCnt = new AtomicInteger(0);
    /**
     * sc -variable declaration - end
     **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_drive_pattern);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
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

        countTextView = findViewById(R.id.countTextView);
        notificationView = findViewById(R.id.notificationView);
        topBarCard = findViewById(R.id.topBarCard);
        notificationService = new NotificationService(getApplicationContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


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
        startBtn.setOnClickListener(view -> started(view));
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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String uid = currentUser.getUid();

        // Reference to the user's data in the database
        DatabaseReference userRef = databaseReference.child("users").child(uid);

        userRef.child("fcmtoken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fcmtoken = dataSnapshot.getValue(String.class);
                    DrivePatternFragment.this.setFcmToken(fcmToken);
                    Log.d("FCM Token", "Token: " + fcmtoken);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that might occur
                Log.w("FCM Token", "Error fetching token", databaseError.toException());
            }
        });

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

            changeVisibility();
//            changeSearchFragmentVisibility();


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

                topBarCard.setVisibility(View.VISIBLE);
//                tripHospitalCnt = 0;
//                tripSchoolCnt =0;
//                tripParksCnt = 0;


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
                double result = Math.round(averageScore * 100) / 100.0;
                if (result >= 5) {
//                    notificationService.sendFCMMessage("Good Driving", "Trip Score: " + result, this.fcmToken);
                    notificationService.sendNotification("Good Driving", "Trip Score: " + result, DrivePatternFragment.this);
                    Toast.makeText(getApplicationContext(), "Good Driving, Trip Score: " + result, Toast.LENGTH_LONG).show();
                } else {
//                    notificationService.sendFCMMessage("Poor Driving", "Trip Score: " + result, this.fcmToken);
                    notificationService.sendNotification("Poor Driving", "Trip Score: " + result, DrivePatternFragment.this);
                    Toast.makeText(getApplicationContext(), "Poor Driving, Trip Score: " + result, Toast.LENGTH_LONG).show();
                }
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (currentfbUser != null) {
//                    Log.d("App", "started: ");
//                    DatabaseReference lastScoreRef = databaseReference.child("users").child(currentfbUser.getUid()).child("driveStat").child("lastDrivingScore");
//                    lastScoreRef.setValue(result);
//
//                    DatabaseReference tripHospitalCntRef = databaseReference.child("users").child(currentfbUser.getUid()).child("driveStat").child("tripHospitalCnt");
//                    tripHospitalCntRef.setValue(tripHospitalCnt);
//
//
//                    DatabaseReference tripSchoolCntRef = databaseReference.child("users").child(currentfbUser.getUid()).child("driveStat").child("tripSchoolCnt");
//                    tripSchoolCntRef.setValue(tripSchoolCnt);
//
//                    DatabaseReference tripParksCntRef = databaseReference.child("users").child(currentfbUser.getUid()).child("driveStat").child("tripParksCntScore");
//                    tripParksCntRef.setValue(tripSchoolCnt);
//
//
//                    DatabaseReference destinationRef = databaseReference.child("users").child(currentfbUser.getUid()).child("driveStat").child("lastDestination");
//                    destinationRef.setValue(location);
//
//
//                }

                if (currentUser != null) {
                    Log.d("App", "currentUser: "+currentUser);
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Log.d("App", "started: add data");


                            Log.d(TAG, "publicPLacesChangesOnCallback: tripParksCnt: " + tripParksCnt.get()+ " tripHospitalCnt :" +tripHospitalCnt.get()+ " tripSchoolCnt :" +tripSchoolCnt.get());



                            databaseReference.child("users").child(currentUser.getUid()).child("driveStat").child("lastDrivingScore").setValue(result);
                            databaseReference.child("users").child(currentUser.getUid()).child("driveStat").child("tripHospitalCnt").setValue(tripHospitalCnt.get()/2);
                            databaseReference.child("users").child(currentUser.getUid()).child("driveStat").child("tripSchoolCnt").setValue(tripSchoolCnt.get()/2);
                            databaseReference.child("users").child(currentUser.getUid()).child("driveStat").child("tripParksCntScore").setValue(tripSchoolCnt.get()/2);
                            databaseReference.child("users").child(currentUser.getUid()).child("driveStat").child("lastDestination").setValue(location);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Handle the error
                        }
                    });
                }
//
//                tripHospitalCnt = 0;
//                tripSchoolCnt =0;
//                tripParksCnt = 0;
                onadd();
            }

            tripParksCnt.addAndGet(0);
            tripHospitalCnt.addAndGet(0);
            tripSchoolCnt.addAndGet(0);

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


        getCurrentLocation();

//        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(@NonNull LatLng latLng) {
//                Log.d(TAG, "setOnMyLocationButtonClickListener: You can use mMap.getMyLocation() to get the current location");
//
//                handleAPIRequest(latLng.latitude, latLng.longitude);
//            }
//
//        });


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
//                gMap.clear(); // verify this once !!!
                handleAPIRequest(false);
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
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }

                }
            }
            break;

            case R.id.Btnto:
                /** sc - add cardview **/
                changeVisibility();
//                changeSearchFragmentVisibility(); // use other views
                /** sc - add cardview **/

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

    private void changeVisibility() {

//        int visibility = topBarCard.getVisibility();
//
//        if (visibility == View.VISIBLE) {
//
//            Log.d(TAG, "changeVisibility: topBarCard is visible ");
//            topBarCard.setVisibility(View.INVISIBLE);
//        } else {
//
//            Log.d(TAG, "changeVisibility: topBarCard is invisible ");
//            topBarCard.setVisibility(View.VISIBLE);
//        }
    }

    private void changeSearchFragmentVisibility() {

//        View fragmentView = startAddressFragment.getView();
//        if (fragmentView != null) {
//            int visibility = fragmentView.getVisibility();
//
//            if (visibility == View.VISIBLE) {
//                Log.d(TAG, "changeSearchFragmentVisibility: startAddressFragment is visible ");
//                View searchView = startAddressFragment.getView();
//                if (searchView != null) {
//                    Log.d(TAG, "changeSearchFragmentVisibility: startAddressFragment making it invisible ");
//                    searchView.setVisibility(View.GONE);
//                }
//            } else {
//                Log.d(TAG, "changeSearchFragmentVisibility: startAddressFragment is NOT visible ");
//                View searchView = startAddressFragment.getView();
//                if (searchView != null) {
//                    Log.d(TAG, "changeSearchFragmentVisibility: startAddressFragment making it visible ");
//                    searchView.setVisibility(View.VISIBLE);
//                }
//            }
//        }

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
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this); // this is duplicate !!!!
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.####");
        return Double.parseDouble(twoDForm.format(d));
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onLocationChanged(final Location location) {
        Log.d("onLocationChanged", "entered");

        publicPLacesChangesOnCallback(location);

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
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = gMap.addMarker(markerOptions);

        //move map camera
        if (i == 1) {
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        String apiKey = "ee87a5fce22b560ffb1c20b877e78ced";
        String apiUrl = "https://api.openweathermap.org/data/2.5/onecall?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + apiKey;

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        try {
                            JSONObject currentWeather = response.getJSONObject("current");
                            JSONArray weatherArray = currentWeather.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);
                            String icon = weatherObject.getString("icon");
                            RainAndSnow = icon.equals("10d") || icon.equals("13d");
                            if (weather_count == 0) {
                                if (RainAndSnow) {
                                    runOnUiThread(() -> {
                                        notificationService.sendNotification("Weather", "The Weather is Rainy", DrivePatternFragment.this);
                                        Snackbar.make(mainRelativeLayout, "The Weather is Rainy", Snackbar.LENGTH_SHORT).show();
                                        playSound();
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        notificationService.sendNotification("Weather", "The Weather is Sunny", DrivePatternFragment.this);
                                        Snackbar.make(mainRelativeLayout, "The Weather is Sunny", Snackbar.LENGTH_SHORT).show();
                                        playSound();
                                    });
                                }
                                weather_count+=1;
                            }
                            Log.d("Icon", icon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    // Handle the error
                    Toast.makeText(DrivePatternFragment.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
// Add the request to the RequestQueue
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.add(jsObjRequest);


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
        topBarCard.setVisibility(View.GONE);
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

                        Log.d(TAG, "onRequestPermissionsResult: repose granted , calling getCurrentLocation ");
                        getCurrentLocation();
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
        int factorWeather = 0;

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
//                        notificationService.sendFCMMessage("You speed is above the limit","Please drive within the speedlimit", DrivePatternFragment.this.fcmToken);
                        notificationService.sendNotification("You speed is above the limit","Please drive within the speedlimit", DrivePatternFragment.this);
                        Snackbar.make(mainRelativeLayout, "You speed is above the limit, please drive within the speedlimit", Snackbar.LENGTH_SHORT).show();
                        playSound();
                    });
                } else {
                    factorSpeed = 1;
                }

                if (areBrakesApplied) {
                    factorBrakes = 10;
                    runOnUiThread(() -> {
//                        notificationService.sendFCMMessage("Please be careful","You shouldn't apply sudden brakes", DrivePatternFragment.this.fcmToken);
                        notificationService.sendNotification("Please be careful","You shouldn't apply sudden brakes", DrivePatternFragment.this);
                        Snackbar.make(mainRelativeLayout, "You shouldn't apply sudden brakes, please be careful", Snackbar.LENGTH_SHORT).show();
                        playSound();
                    });
                } else {
                    factorBrakes = 0;
                }
                if (RainAndSnow) {
                    factorWeather = 10;
                } else {
                    factorWeather = 0;
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
                                notificationService.sendNotification("Please be safe","Harsh acceleration has been detected", DrivePatternFragment.this);
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
                                notificationService.sendNotification("Please be safe","Harsh unsafe turn has been detected", DrivePatternFragment.this);
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

            double unsafeScore = 0.3 * factorSpeed + 0.2 * factorBrakes +  0.2 * factorWeather + 0.2 * factorAcceleration + 0.2 * factorTurn;
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

    /** sc - code start  **/
    private void handleAPIRequest() {
        handleAPIRequest(curr_lat, curr_log, true);
    }

    private void handleAPIRequest(boolean focus) {
        handleAPIRequest(curr_lat, curr_log, focus);
    }

    private void handleAPIRequest(double xlat, double xlog) {
        handleAPIRequest(xlat, xlog, true);
    }
    private void handleAPIRequest(double xlat, double xlog, boolean focus) {
        Log.d(TAG, "handleAPIRequest: ihandleAPIRequest ");
        // Remove existing markers before adding new ones
        gMap.clear(); // check this for Mukul's integration!!!


        FetchData hospitalData = fetchAndDisplayPlaces("hospital", xlat, xlog, focus);
        FetchData schoolData = fetchAndDisplayPlaces("school", xlat, xlog, focus);
        FetchData parkData = fetchAndDisplayPlaces("park", xlat, xlog, focus);
//                fetchAndDisplayPlaces("park");




        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (countTextView != null) {
                    String countText = String.format("In your vicinity, there are %d hospitals, %d schools, and %d parks.", hospitalData.getHospitalCount(), schoolData.getSchoolCount(), parkData.getParkCount());
                    countTextView.setText(countText);
                }
//                tripParksCnt += parkData.getParkCount();
//                tripHospitalCnt += hospitalData.getHospitalCount();
//                tripSchoolCnt += schoolData.getSchoolCount();

                tripParksCnt.addAndGet(parkData.getParkCount());
                tripHospitalCnt.addAndGet(hospitalData.getHospitalCount());
                tripSchoolCnt.addAndGet(schoolData.getSchoolCount());

                Log.d(TAG, "handleAPIRequest: tripParksCnt: " + tripParksCnt+ " tripHospitalCnt :" +tripHospitalCnt+ " tripSchoolCnt :" +tripSchoolCnt);


//                String countText = String.format("In your vicinity, there are %d hospitals, %d schools, and %d parks.", hospitalData.getHospitalCount(), schoolData.getSchoolCount(), parkData.getParkCount());
//                Toast.makeText(getApplicationContext(), countText, Toast.LENGTH_LONG).show();


                // Your function to be executed after 5 seconds
                int intersectingCount = hospitalData.getIntersectingCount() + schoolData.getIntersectingCount() + parkData.getIntersectingCount();
                Log.d(TAG, "handleAPIRequest: intersectingCount: " + intersectingCount);


//                plotOriginalLocation(curr_lat, curr_log);
                if (notificationView != null) {
                    String message = "";
                    if (intersectingCount >= highThreshold) {
                        message += "Approaching highly crowded area, prepare to slow down. Exercise caution and follow safety guidelines.";
                        topBarCard.setCardBackgroundColor(Color.RED);
                    } else if (intersectingCount < highThreshold && intersectingCount > lowThreshold) {
                        message += "Moderate density :Stay vigilant and be aware of your surroundings. ";
                        topBarCard.setCardBackgroundColor(getResources().getColor(R.color.colorMediumLevel));
                    } else {
                        message += " Low Density : Drive safely, exercise normal precautions and enjoy your surroundings.";
                        topBarCard.setCardBackgroundColor(getResources().getColor(R.color.colorLowLevel));
                    }

                    notificationView.setText(message);

//                    plotOriginalLocation(xlat, xlog);


                    // Set background color based on the level

                }

            }
        }, 3000);
    }


    private FetchData fetchAndDisplayPlaces(String placeType) {
        return fetchAndDisplayPlaces(placeType, curr_lat, curr_log, true);
    }

    private FetchData fetchAndDisplayPlaces(String placeType, double lat, double log, boolean focus) {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=" + lat + "," + log);
//        stringBuilder.append("location=" + curr_lat + "," + curr_log);
        stringBuilder.append("&radius=" + search_radius);
        stringBuilder.append("&type=" + placeType);
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=" + getResources().getString(R.string.google_maps_key));

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];

        dataFetch[0] = gMap;
        dataFetch[1] = url;

        Log.d(TAG, "onClick: url " + url);

        FetchData fetchData = new FetchData(placeType,  focus);
        fetchData.execute(dataFetch);


        return fetchData;

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        Log.d(TAG, "getCurrentLocation: ");
        // check permission first
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {

                Log.d(TAG, "getCurrentLocation: ask for permission now");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);

                return;
            }


        Log.d(TAG, "getCurrentLocation: as we have permission now");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(6000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //        locationRequest.setFastestInterval(5000);
        locationRequest.setFastestInterval(10000);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                //                super.onLocationResult(locationResult);
                //                Toast.makeText(getApplicationContext(), " Location result is = "+locationResult., Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), " Current location is null", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "onLocationResult: length "+ locationResult.getLocations().size());


                for (Location location : locationResult.getLocations()) {//
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

                        if (currentLocationMarker != null) {
                            currentLocationMarker.remove();
                        }
                    }

                }
            }
        };



//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
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

                        Log.d(TAG, "fusedLocationProviderClient: tripParksCnt: " + tripParksCnt+ " tripHospitalCnt :" +tripHospitalCnt+ " tripSchoolCnt :" +tripSchoolCnt);

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

            gMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


    //        currentLocationMarker.setPosition(latLng);
        }

        private void publicPLacesChangesOnCallback(Location location ){
//            if (location != null) {
//                Toast.makeText(getApplicationContext(), " Current location is " + location.getLatitude() + " - " + location.getLongitude(), Toast.LENGTH_LONG).show();
//            }

            //
            //                    curr_lat += LOCATION_UPDATE_FACTOR;
            //                    curr_log += LOCATION_UPDATE_FACTOR;


            // Calculate distance
            double distance = calculateDistance(prev_lat, prev_log, curr_lat, curr_log);

            // Print the result
            System.out.println("Distance: " + distance + " km");
            Log.d(TAG, "publicPLacesChangesOnCallback: Distance: " + distance + " km" + "updated_radius " + updated_radius + " curr_lat : " + curr_lat + " curr_log " + curr_log);

            if (distance >= updated_radius) {
                prev_lat = curr_lat;
                prev_log = curr_log;
                Log.d(TAG, "publicPLacesChangesOnCallback: FETCH NEW PLACES : ");
                //                        fetchAndDisplayPlaces("hospital");
                //                        fetchAndDisplayPlaces("school");

                handleAPIRequest();

                Log.d(TAG, "publicPLacesChangesOnCallback: tripParksCnt: " + tripParksCnt+ " tripHospitalCnt :" +tripHospitalCnt+ " tripSchoolCnt :" +tripSchoolCnt);


                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }
            }

            LatLng latLng = new LatLng(curr_lat, curr_log);
        }


    /** sc - code end  **/


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
    private boolean markerFocus = true;

    public int getHospitalCount() {
        return hospitalCount;
    }

    public int getSchoolCount() {
        return schoolCount;
    }


    public int getParkCount() {
        return parkCount;
    }

    public FetchData(String type, boolean markerFocus) {
        this.type = type;
        this.markerFocus =markerFocus;
    }

//    private List<Marker> markers = new ArrayList<>(); // Keep track of markers



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
                markerOptions.alpha(0.9F);
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
                if(  this.markerFocus == true){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }

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
//        if (countTextView != null) {
//            String countText = "Hospitals: " + hospitalCount + " Schools: " + schoolCount + " Parks: " + parkCount;
//            countTextView.setText(countText);
//        }


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
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
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
                .fillColor(Color.parseColor("#302327e8")); // Color of the circle's fill (with transparency)
//                .fillColor(Color.parseColor("#300000FF")); // Color of the circle's fill (with transparency)
//                .fillColor(Color.parseColor("#300000FF")); // Color of the circle's fill (with transparency)

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