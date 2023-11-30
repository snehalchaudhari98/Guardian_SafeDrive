package com.example.safedrive_guardian.ui.home.object;

import android.os.Bundle;

import com.example.safedrive_guardian.ui.home.helpers.MLVideoHelperActivity;
import com.example.safedrive_guardian.ui.home.helpers.vision.FaceDetectorProcessor;

public class DriverDrowsinessDetectionActivity extends MLVideoHelperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setProcessor() {
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this));
    }
}