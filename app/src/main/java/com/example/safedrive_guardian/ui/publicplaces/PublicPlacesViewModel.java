package com.example.safedrive_guardian.ui.publicplaces;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PublicPlacesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> countText = new MutableLiveData<>();
    private final MutableLiveData<String> notificationText = new MutableLiveData<>();


    public PublicPlacesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is public places fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    // Add more LiveData variables if needed

    public void updateCountText(String newText) {
        countText.setValue(newText);
    }

    public void updateNotificationText(String newText) {
        notificationText.setValue(newText);
    }

    public LiveData<String> getCountText() {
        return countText;
    }

    public LiveData<String> getNotificationText() {
        return notificationText;
    }

}