package com.example.safedrive_guardian.ui.drivingpattern;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DrivePatternViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public DrivePatternViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Driving Pattern fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
