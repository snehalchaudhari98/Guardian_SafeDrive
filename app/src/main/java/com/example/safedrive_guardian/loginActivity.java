package com.example.safedrive_guardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class loginActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // TODO: Add this logically into the database
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        String token = task.getResult();
//                        this.token = token;
//                        Log.d("FCM", "FCM Token: " + token);
//                    }
//                });

        findViewById(R.id.signinbutton).setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.signinemail)).getText().toString();
            String password = ((EditText) findViewById(R.id.signinpassword)).getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                ((EditText) findViewById(R.id.signinemail)).setError("Email is Required!");
                ((EditText) findViewById(R.id.signinpassword)).setError("Password is Required");
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                                addUsernameToPreferences(email);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        findViewById(R.id.redirectsignup).setOnClickListener(view -> {
            Intent intent = new Intent(this, signUpActivity.class);
            startActivity(intent);
        });
    }

    private void addUsernameToPreferences(String email){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.apply();
    }

}