package com.example.safedrive_guardian.ui.publicplaces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.safedrive_guardian.R;
//import com.example.safedrive_guardian.databinding.ActivityMapsBinding;
import com.example.safedrive_guardian.databinding.FragmentGalleryBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class PublicPlacesFragment extends Fragment implements OnMapReadyCallback {

    private FragmentGalleryBinding fragmentGalleryBinding;

    private GoogleMap mMap;
//    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_code = 101;
    protected double curr_lat, curr_log;
    private double prev_lat = Integer.MAX_VALUE, prev_log = Integer.MAX_VALUE;

    private Button searchButton;
    private String TAG = "APP: MapsActivity";
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
//    private NearbyPlacesFinder nearbyPlacesFinder;

    // for testing purpose - simulation
    private static final double LOCATION_UPDATE_FACTOR = 0.001; // Factor to update location



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PublicPlacesViewModel galleryViewModel =
                new ViewModelProvider(this).get(PublicPlacesViewModel.class);

        fragmentGalleryBinding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = fragmentGalleryBinding.getRoot();


        final TextView countTextView = fragmentGalleryBinding.countTextView;
        final TextView notificationView = fragmentGalleryBinding.notificationView;


//        final TextView textView = fragmentGalleryBinding.textGallary;

//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        galleryViewModel.getCountText().observe(getViewLifecycleOwner(), countTextView::setText);
        galleryViewModel.getNotificationText().observe(getViewLifecycleOwner(), notificationView::setText);


        // Call methods from ViewModel to initiate data fetching or updates
        galleryViewModel.updateCountText("Counts will be displayed here");
        galleryViewModel.updateNotificationText("Crowdness will be displayed");

//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
////        setContentView(binding.getRoot());
//        searchButton = fragmentGalleryBinding.myButton;
//
//        topBarCard = fragmentGalleryBinding.topBarCard;
//        topBarCard.setBackgroundColor(getResources().getColor(R.color.colorHighLevel));
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentGalleryBinding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}


