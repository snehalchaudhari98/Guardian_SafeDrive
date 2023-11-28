package com.example.safedrive_guardian.ui.drivingpattern;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.safedrive_guardian.R;
import com.example.safedrive_guardian.databinding.FragmentDrivePatternBinding;
//import com.example.safedrive_guardian.databinding.FragmentHomeBinding;
//import com.example.safedrive_guardian.ui.home.HomeViewModel;

public class DrivePatternFragment extends Fragment {



    private FragmentDrivePatternBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DrivePatternViewModel drivePatternViewModel =
                new ViewModelProvider(this).get(DrivePatternViewModel.class);

        binding = FragmentDrivePatternBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDrivingpattern;
        drivePatternViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}