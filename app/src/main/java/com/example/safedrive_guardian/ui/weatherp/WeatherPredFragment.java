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

public class WeatherPredFragment extends Fragment {

    private FragmentWeatherPredBinding binding;
    private WeatherPredViewModel weatherPredViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        weatherPredViewModel =
                new ViewModelProvider(this).get(WeatherPredViewModel.class);

        binding = FragmentWeatherPredBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWeatherPred;
        EditText cityEditText = binding.cityEditText;
        Button weatherButton = binding.weatherButton;

        weatherPredViewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            textView.setText(newText);
            if(newText.contains("good") || newText.contains("bad")) {
                showWeatherAlert(newText);
            }
        });

        weatherButton.setOnClickListener(view -> {
            String city = cityEditText.getText().toString();
            if (!city.isEmpty()) {
                weatherPredViewModel.fetchWeatherPrediction(city, "6597169d8893c98f17c7b5045bcd7c65");
            } else {
                Toast.makeText(getContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
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
