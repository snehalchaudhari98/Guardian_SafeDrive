package com.example.safedrive_guardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class healthQuestions extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    RadioGroup radioGroupGender;
    EditText editTextAge ;
    EditText editTextWeight ;
    RadioGroup radioGroupMedicalConditions ;
    EditText editTextMedicalConditions ;
    RadioGroup radioGroupPreviousSurgeries ;
    RadioGroup radioGroupDietaryHabits ;
    RadioGroup radioGroupPhysicalActivity ;
    RadioGroup radioGroupSleepDuration;
    RadioGroup radioGroupAlcoholConsumption ;
    RadioGroup radioGroupHealthSymptoms ;
    EditText editTextHealthSymptoms ;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_questions);


        // Initialize Firebase components
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Load existing user data and populate UI elements
        loadUserData();

        // Find views
         radioGroupGender = findViewById(R.id.radioGroupGender);
         editTextAge = findViewById(R.id.editTextAge);
         editTextWeight = findViewById(R.id.editTextWeight);
         radioGroupMedicalConditions = findViewById(R.id.radioGroupMedicalConditions);
         editTextMedicalConditions = findViewById(R.id.editTextMedicalConditions);
         radioGroupPreviousSurgeries = findViewById(R.id.radioGroupPreviousSurgeries);
         radioGroupDietaryHabits = findViewById(R.id.radioGroupDietaryHabits);
         radioGroupPhysicalActivity = findViewById(R.id.radioGroupPhysicalActivity);
         radioGroupSleepDuration = findViewById(R.id.radioGroupSleepDuration);
         radioGroupAlcoholConsumption = findViewById(R.id.radioGroupAlcoholConsumption);
         radioGroupHealthSymptoms = findViewById(R.id.radioGroupHealthSymptoms);
         editTextHealthSymptoms = findViewById(R.id.editTextHealthSymptoms);
         submitButton = findViewById(R.id.submitButton);
        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user UID
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    // Get responses from UI elements
                    String gender = getSelectedRadioButtonText(radioGroupGender);
                    String age = editTextAge.getText().toString().trim();
                    String weight = editTextWeight.getText().toString().trim();
                    String medicalConditions = getSelectedRadioButtonText(radioGroupMedicalConditions);
                    String specifiedMedicalConditions = editTextMedicalConditions.getText().toString().trim();
                    String previousSurgeries = getSelectedRadioButtonText(radioGroupPreviousSurgeries);
                    String dietaryHabits = getSelectedRadioButtonText(radioGroupDietaryHabits);
                    String physicalActivity = getSelectedRadioButtonText(radioGroupPhysicalActivity);
                    String sleepDuration = getSelectedRadioButtonText(radioGroupSleepDuration);
                    String alcoholConsumption = getSelectedRadioButtonText(radioGroupAlcoholConsumption);
                    String healthSymptoms = getSelectedRadioButtonText(radioGroupHealthSymptoms);
                    String specifiedHealthSymptoms = editTextHealthSymptoms.getText().toString().trim();

                    // Store responses in Firebase Realtime Database
                    storeResponsesInDatabase(uid, gender, age, weight, medicalConditions,
                            specifiedMedicalConditions, previousSurgeries, dietaryHabits, physicalActivity,
                            sleepDuration, alcoholConsumption, healthSymptoms, specifiedHealthSymptoms);
                } else {
                    // User not authenticated
                    Toast.makeText(getApplicationContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserData() {
        // Get user UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Reference to the user's data in the database
            DatabaseReference userRef = databaseReference.child("users").child(uid);

            // Retrieve data from the database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Populate UI elements with existing data
                        // Example: Set gender
                        String gender = snapshot.child("gender").getValue(String.class);
                        if (gender != null) {
                            setRadioButtonByValue(radioGroupGender, gender);
                        }

                        // Populate Age
                        String age = snapshot.child("age").getValue(String.class);
                        if (age != null) {
                            editTextAge.setText(age);
                        }

                        // Populate Weight
                        String weight = snapshot.child("weight").getValue(String.class);
                        if (weight != null) {
                            editTextWeight.setText(weight);
                        }

                        // Populate Medical Conditions
                        String medicalConditions = snapshot.child("medicalConditions").getValue(String.class);
                        if (medicalConditions != null) {
                            setRadioButtonByValue(radioGroupMedicalConditions, medicalConditions);

                            // If 'Yes' is selected, show and populate specified conditions
                            if ("Yes".equals(medicalConditions)) {
                                editTextMedicalConditions.setVisibility(View.VISIBLE);
                                String specifiedMedicalConditions = snapshot.child("specifiedMedicalConditions").getValue(String.class);
                                if (specifiedMedicalConditions != null) {
                                    editTextMedicalConditions.setText(specifiedMedicalConditions);
                                }
                            }
                        }

                        // Populate Previous Surgeries
                        String previousSurgeries = snapshot.child("previousSurgeries").getValue(String.class);
                        if (previousSurgeries != null) {
                            setRadioButtonByValue(radioGroupPreviousSurgeries, previousSurgeries);
                        }

                        // Populate Dietary Habits
                        String dietaryHabits = snapshot.child("dietaryHabits").getValue(String.class);
                        if (dietaryHabits != null) {
                            setRadioButtonByValue(radioGroupDietaryHabits, dietaryHabits);
                        }

                        // Populate Physical Activity
                        String physicalActivity = snapshot.child("physicalActivity").getValue(String.class);
                        if (physicalActivity != null) {
                            setRadioButtonByValue(radioGroupPhysicalActivity, physicalActivity);
                        }

                        // Populate Sleep Duration
                        String sleepDuration = snapshot.child("sleepDuration").getValue(String.class);
                        if (sleepDuration != null) {
                            setRadioButtonByValue(radioGroupSleepDuration, sleepDuration);
                        }

                        // Populate Alcohol Consumption
                        String alcoholConsumption = snapshot.child("alcoholConsumption").getValue(String.class);
                        if (alcoholConsumption != null) {
                            setRadioButtonByValue(radioGroupAlcoholConsumption, alcoholConsumption);
                        }

                        // Populate Health Symptoms
                        String healthSymptoms = snapshot.child("healthSymptoms").getValue(String.class);
                        if (healthSymptoms != null) {
                            setRadioButtonByValue(radioGroupHealthSymptoms, healthSymptoms);

                            // If 'Yes' is selected, show and populate specified symptoms
                            if ("Yes".equals(healthSymptoms)) {
                                editTextHealthSymptoms.setVisibility(View.VISIBLE);
                                String specifiedHealthSymptoms = snapshot.child("specifiedHealthSymptoms").getValue(String.class);
                                if (specifiedHealthSymptoms != null) {
                                    editTextHealthSymptoms.setText(specifiedHealthSymptoms);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        }
    }

    // Helper method to set the selected radio button based on a value
    private void setRadioButtonByValue(RadioGroup radioGroup, String value) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View radioButton = radioGroup.getChildAt(i);
            if (radioButton instanceof RadioButton) {
                RadioButton radioBtn = (RadioButton) radioButton;
                if (radioBtn.getText().toString().equals(value)) {
                    radioBtn.setChecked(true);
                    break;
                }
            }
        }
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            return selectedRadioButton.getText().toString();
        }
        return "";
    }

    private void storeResponsesInDatabase(String uid, String gender, String age, String weight,
                                          String medicalConditions, String specifiedMedicalConditions,
                                          String previousSurgeries, String dietaryHabits,
                                          String physicalActivity, String sleepDuration,
                                          String alcoholConsumption, String healthSymptoms,
                                          String specifiedHealthSymptoms) {
        // Create a new node in the database using the user's UID
        DatabaseReference userRef = databaseReference.child("users").child(uid);

        // Store responses under the user's UID
        userRef.child("gender").setValue(gender);
        userRef.child("age").setValue(age);
        userRef.child("weight").setValue(weight);
        userRef.child("medicalConditions").setValue(medicalConditions);
        userRef.child("specifiedMedicalConditions").setValue(specifiedMedicalConditions);
        userRef.child("previousSurgeries").setValue(previousSurgeries);
        userRef.child("dietaryHabits").setValue(dietaryHabits);
        userRef.child("physicalActivity").setValue(physicalActivity);
        userRef.child("sleepDuration").setValue(sleepDuration);
        userRef.child("alcoholConsumption").setValue(alcoholConsumption);
        userRef.child("healthSymptoms").setValue(healthSymptoms);
        userRef.child("specifiedHealthSymptoms").setValue(specifiedHealthSymptoms);

        // Inform the user that responses are stored successfully
        Toast.makeText(getApplicationContext(), "Responses stored successfully", Toast.LENGTH_SHORT).show();
    }
}