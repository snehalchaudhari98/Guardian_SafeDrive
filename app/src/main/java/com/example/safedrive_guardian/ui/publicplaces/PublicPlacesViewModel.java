package com.example.safedrive_guardian.ui.publicplaces;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PublicPlacesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PublicPlacesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is public places fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}