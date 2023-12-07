package com.example.safedrive_guardian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safedrive_guardian.services.NotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safedrive_guardian.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_weatherpred, R.id.nav_drivingpattern, R.id.nav_notifications)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Initialize Firebase components
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        // Get the header view
        View headerView = navigationView.getHeaderView(0);

        // Find the TextView for the username in the header view
        TextView useremailView = headerView.findViewById(R.id.textView);
        TextView usernameTextView = headerView.findViewById(R.id.usernameheader);
//         loadUsername(usernameTextView, useremailView);
        loadUsernameFromPref(usernameTextView, useremailView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Handle logout here
            logout();
            return true;
        }else if(id == R.id.action_settings){
            Toast.makeText(getApplicationContext(), "Setting actions", Toast.LENGTH_SHORT).show();
            // Launch HealthQuestionsActivity
            Intent intent = new Intent(getApplicationContext(), healthQuestions.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Update SharedPreferences or any other session management logic if needed
        clearUserEmail();
        Toast.makeText(getApplicationContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();

        // Redirect to the login screen or any other screen as needed
        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
        startActivity(intent);
        finish();
    }


    // Method to clear user email from SharedPreferences
    private void clearUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_email");
        editor.apply();
    }

    // Method to load the username from Firebase Realtime Database
//    private void loadUsername(TextView usernameTextView, TextView emailTextView) {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // Assuming you store the username under a 'username' field in the 'users' node
//            databaseReference.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    // Check if the 'username' field exists in the database
//                    if (snapshot.hasChild("username")) {
//                        String username = snapshot.child("usernamesignuo").getValue(String.class);
//                        String useremail = snapshot.child("emailaddress").getValue(String.class);
//
//                        // Update the TextView with the new username
//                        usernameTextView.setText(username);
//                        emailTextView.setText(useremail);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Handle the error
//                }
//            });
//        }
//    }


    private void loadUsernameFromPref(TextView usernameTextView, TextView emailTextView) {
        String key = "user_email"; // Change this to your specific key

// Access the SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

// Retrieve the value using the key
        String userEmail = sharedPreferences.getString(key, "");
        Log.d("MainActvyt", "loadUsernameFromPref: "+userEmail);
        emailTextView.setText(userEmail);

    }
}