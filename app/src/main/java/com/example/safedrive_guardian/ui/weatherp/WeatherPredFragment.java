package com.example.safedrive_guardian.ui.weatherp;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.safedrive_guardian.databinding.FragmentWeatherPredBinding;

public class WeatherPredFragment extends Fragment {

    private FragmentWeatherPredBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WeatherPredViewModel slideshowViewModel =
                new ViewModelProvider(this).get(WeatherPredViewModel.class);

        binding = FragmentWeatherPredBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWeatherPred;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}