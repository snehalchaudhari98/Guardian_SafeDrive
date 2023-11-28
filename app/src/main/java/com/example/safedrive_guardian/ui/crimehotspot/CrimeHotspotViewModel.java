package com.example.safedrive_guardian.ui.crimehotspot;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CrimeHotspotViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final String TAG ="CrimeHotspotViewModel";

    public CrimeHotspotViewModel() {
        mText = new MutableLiveData<>();
        Log.d(TAG, " show msg ");
        mText.setValue("This is crimehotspot fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}