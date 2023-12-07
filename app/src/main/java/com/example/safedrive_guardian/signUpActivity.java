package com.example.safedrive_guardian;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Intent;

public class signUpActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//    }

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();

        signUpUser();

        findViewById(R.id.redirectsignin).setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(this, loginActivity.class);
            startActivity(intent);
        });

    }

    private void signUpUser() {
        findViewById(R.id.signupbutton).setOnClickListener(view -> {
            String username = ((EditText) findViewById(R.id.usernamesignup)).getText().toString();
            String email = ((EditText) findViewById(R.id.signupemail)).getText().toString();
            String pass = ((EditText) findViewById(R.id.passwordsignup)).getText().toString();

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                ((EditText) findViewById(R.id.usernamesignup)).setError("Username is Required!");
                ((EditText) findViewById(R.id.signupemail)).setError("Email is Required!");
                ((EditText) findViewById(R.id.passwordsignup)).setError("Password is Required!");
            } else {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            databaseReference.child("users").child(currentUser.getUid()).child("usernamesignup").setValue(username);
                                            databaseReference.child("users").child(currentUser.getUid()).child("emailaddress").setValue(email);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Handle the error
                                        }
                                    });
                                }

                                Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }





}