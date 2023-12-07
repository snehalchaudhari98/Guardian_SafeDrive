/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.safedrive_guardian.ui.home.helpers.vision;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.safedrive_guardian.services.NotificationService;
import com.example.safedrive_guardian.ui.drivingpattern.DrivePatternFragment;
import com.example.safedrive_guardian.ui.home.HomeFragment;
import com.example.safedrive_guardian.ui.home.object.DriverDrowsinessDetectionActivity;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/** Face Detector Demo. */
public class FaceDetectorProcessor extends VisionProcessorBase<List<Face>> {

    private static final String TAG = "FaceDetectorProcessor";

    private final FaceDetector detector;
    NotificationService notificationService;
    Context context1;
    private final Activity activity;
    private final HashMap<Integer, FaceDrowsiness> drowsinessHashMap = new HashMap<>();
    private int drowsyCount;
    private long lastDrowsyTimestamp;
    public FaceDetectorProcessor(Context context, Activity activity) {
        super(context);
        this.activity = activity;
        context1 = context;
        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build();
        Log.v(MANUAL_TESTING_LOG, "Face detector options: " + faceDetectorOptions);
        detector = FaceDetection.getClient(faceDetectorOptions);
    }

    @Override
    public void stop() {
        super.stop();
        detector.close();
    }

    @Override
    protected Task<List<Face>> detectInImage(InputImage image) {
        return detector.process(image);
    }

    @Override
    protected void onSuccess(@NonNull List<Face> faces, @NonNull GraphicOverlay graphicOverlay) {
        if(context1 != null)
        {
            notificationService = new NotificationService(context1);
        }

        for (Face face : faces) {
            FaceDrowsiness faceDrowsiness = drowsinessHashMap.get(face.getTrackingId());
            if (faceDrowsiness == null) {
                faceDrowsiness = new FaceDrowsiness();
                drowsinessHashMap.put(face.getTrackingId(), faceDrowsiness);
            }
            boolean isDrowsy = faceDrowsiness.isDrowsy(face);
            if (isDrowsy) {
                drowsyCount++;
                Log.d("Drowsiness", String.valueOf(drowsyCount));
                // Update the timestamp when isDrowsy becomes true
                setLastDrowsyTimestamp(System.currentTimeMillis());
            }

            // Check if isDrowsy has been true 5 times within a minute
            if (drowsyCount >= 100 &&
                    System.currentTimeMillis() - getLastDrowsyTimestamp() <= 10 * 1000) {
                notificationService.sendNotification("Drowsy","You are Sleepy!! Take a Break!!", activity);
                Log.d("Drowsiness", "Drowsiness detected 5 times in a minute!");
                // Reset the count
                resetDrowsyCount();
            }
            graphicOverlay.add(new FaceGraphic(graphicOverlay, face, isDrowsy));
            logExtrasForTesting(face);
        }
    }
    public int getDrowsyCount() {
        return drowsyCount;
    }

    public long getLastDrowsyTimestamp() {
        return lastDrowsyTimestamp;
    }
    public void setLastDrowsyTimestamp(long timestamp) {
        lastDrowsyTimestamp = timestamp;
    }

    public void resetDrowsyCount() {
        drowsyCount = 0;
    }
    private static void logExtrasForTesting(Face face) {
        if (face != null) {
            Log.v(MANUAL_TESTING_LOG, "face bounding box: " + face.getBoundingBox().flattenToString());
            Log.v(MANUAL_TESTING_LOG, "face Euler Angle X: " + face.getHeadEulerAngleX());
            Log.v(MANUAL_TESTING_LOG, "face Euler Angle Y: " + face.getHeadEulerAngleY());
            Log.v(MANUAL_TESTING_LOG, "face Euler Angle Z: " + face.getHeadEulerAngleZ());

            // All landmarks
            int[] landMarkTypes =
                    new int[] {
                            FaceLandmark.MOUTH_BOTTOM,
                            FaceLandmark.MOUTH_RIGHT,
                            FaceLandmark.MOUTH_LEFT,
                            FaceLandmark.RIGHT_EYE,
                            FaceLandmark.LEFT_EYE,
                            FaceLandmark.RIGHT_EAR,
                            FaceLandmark.LEFT_EAR,
                            FaceLandmark.RIGHT_CHEEK,
                            FaceLandmark.LEFT_CHEEK,
                            FaceLandmark.NOSE_BASE
                    };
            String[] landMarkTypesStrings =
                    new String[] {
                            "MOUTH_BOTTOM",
                            "MOUTH_RIGHT",
                            "MOUTH_LEFT",
                            "RIGHT_EYE",
                            "LEFT_EYE",
                            "RIGHT_EAR",
                            "LEFT_EAR",
                            "RIGHT_CHEEK",
                            "LEFT_CHEEK",
                            "NOSE_BASE"
                    };
            for (int i = 0; i < landMarkTypes.length; i++) {
                FaceLandmark landmark = face.getLandmark(landMarkTypes[i]);
                if (landmark == null) {
                    Log.v(
                            MANUAL_TESTING_LOG,
                            "No landmark of type: " + landMarkTypesStrings[i] + " has been detected");
                } else {
                    PointF landmarkPosition = landmark.getPosition();
                    String landmarkPositionStr =
                            String.format(Locale.US, "x: %f , y: %f", landmarkPosition.x, landmarkPosition.y);
                    Log.v(
                            MANUAL_TESTING_LOG,
                            "Position for face landmark: "
                                    + landMarkTypesStrings[i]
                                    + " is :"
                                    + landmarkPositionStr);
                }
            }
            Log.v(
                    MANUAL_TESTING_LOG,
                    "face left eye open probability: " + face.getLeftEyeOpenProbability());
            Log.v(
                    MANUAL_TESTING_LOG,
                    "face right eye open probability: " + face.getRightEyeOpenProbability());
            Log.v(MANUAL_TESTING_LOG, "face smiling probability: " + face.getSmilingProbability());
            Log.v(MANUAL_TESTING_LOG, "face tracking id: " + face.getTrackingId());
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }
}