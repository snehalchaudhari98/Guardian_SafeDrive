package com.example.safedrive_guardian.ui.drivingpattern;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.safedrive_guardian.databinding.FragmentDrivePatternBinding;

//import com.example.safedrive_guardian.databinding.FragmentHomeBinding;
//import com.example.safedrive_guardian.ui.home.HomeViewModel;


public class DrivePatternFragment extends Fragment {



    private FragmentDrivePatternBinding binding;
    private String Name = "Darshan";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDrivePatternBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.BtnNewTrip.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), Maps.class);
            i.putExtra("userid", Name);
            startActivity(i);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}