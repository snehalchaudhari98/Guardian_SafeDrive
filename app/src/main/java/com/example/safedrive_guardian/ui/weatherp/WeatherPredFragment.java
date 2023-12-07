package com.example.safedrive_guardian.ui.weatherp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.safedrive_guardian.databinding.FragmentWeatherPredBinding;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Geocoder;
import android.location.Address;
import android.content.Context;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherPredFragment extends Fragment {

    private FragmentWeatherPredBinding binding;
    private WeatherPredViewModel weatherPredViewModel;
    private LocationManager locationManager;
    private LocationListener locationListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        weatherPredViewModel =
                new ViewModelProvider(this).get(WeatherPredViewModel.class);

        binding = FragmentWeatherPredBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWeatherPred;
//        EditText cityEditText = binding.cityEditText;
//        Button weatherButton = binding.weatherButton;

        weatherPredViewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            textView.setText(newText);
            if(newText.contains("good") || newText.contains("bad")) {
                showWeatherAlert(newText);
            }
        });

        final String[] city = new String[1];
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String cityName = getCityName(location.getLatitude(), location.getLongitude());
                if (cityName != null && !cityName.isEmpty()) {
                    weatherPredViewModel.fetchWeatherPrediction(cityName, "6597169d8893c98f17c7b5045bcd7c65");
                } else {
                    Toast.makeText(getContext(), "City name not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch(SecurityException e) {
            e.printStackTrace();
        }


//        weatherButton.setOnClickListener(view -> {
////            String city = cityEditText.getText().toString();
////            if (!city[0].isEmpty()) {
//                weatherPredViewModel.fetchWeatherPrediction(city[0], "6597169d8893c98f17c7b5045bcd7c65");
////            } else {
////                Toast.makeText(getContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
////            }
//        });

        return root;
    }

    private String getCityName(double lat, double lon) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showWeatherAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Weather Prediction")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
