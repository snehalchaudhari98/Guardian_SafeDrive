package com.example.safedrive_guardian.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.safedrive_guardian.ui.home.object.DriverDrowsinessDetectionActivity;
import com.example.safedrive_guardian.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    private TextView lastTripDestination;
    private TextView textLastDrivingScore;
    private TextView hospitalCountTv;
    private TextView schoolCountTv;
    private TextView parkCountTv;

    private TextView introMsgTextview;




    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button buttonHome = binding.buttonHome;

        lastTripDestination = binding.lastTripDestination;
        textLastDrivingScore = binding.textLastDrivingScore;
        hospitalCountTv = binding.hospitalCountTv;
        schoolCountTv = binding.schoolCountTv;
        parkCountTv = binding.parkCountTv;
        introMsgTextview = binding.introMsg;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        // Set up button click listener
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click, start DriverDrowsinessActivity
                startDriverDrowsinessActivity();
            }
        });


        FirebaseUser currentfbUser = mAuth.getCurrentUser();
        // Assuming you have the Firebase database reference
        Log.d("App", "currentfbUser: "+currentfbUser.toString());
        DatabaseReference driveStatRef = databaseReference.child("users").child(currentfbUser.getUid()).child("driveStat");

        driveStatRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("App", "onDataChange: "+dataSnapshot.toString());
                // Check if the dataSnapshot exists and has children
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // Retrieve values from the dataSnapshot
                    // Read values from dataSnapshot and set them to corresponding TextViews
                    lastTripDestination.setText(String.valueOf(dataSnapshot.child("lastDestination").getValue()));
                    textLastDrivingScore.setText(String.valueOf(dataSnapshot.child("lastDrivingScore").getValue()));
                    hospitalCountTv.setText(String.valueOf(dataSnapshot.child("tripHospitalCnt").getValue()));
                    schoolCountTv.setText(String.valueOf(dataSnapshot.child("tripSchoolCnt").getValue()));
                    parkCountTv.setText(String.valueOf(dataSnapshot.child("tripParksCntScore").getValue()));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("App", "onDataChange:  no drive statae");
                // Handle errors
//                Toast.makeText(th, "Failed to read data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference userRef = databaseReference.child("users").child(currentfbUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("App", "onDataChange: " + dataSnapshot.toString());
                // Check if the dataSnapshot exists and has children
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    String username = String.valueOf(dataSnapshot.child("usernamesignup").getValue());
                    // Assuming you have a TextView for the username
                    introMsgTextview.setText("Hello "+username+" , your last trip was:");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("App", "onDataChange: no drive statae");
                // Handle errors
                // Toast.makeText(th, "Failed to read data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    private void startDriverDrowsinessActivity() {
        Intent intent = new Intent(getActivity(), DriverDrowsinessDetectionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
