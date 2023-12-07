package com.example.safedrive_guardian.ui.notifications;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.safedrive_guardian.databinding.FragmentNotificationsBinding;
import com.example.safedrive_guardian.services.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationService notificationService;
    private static final int REQUEST_CODE = 100;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel slideshowViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationService = new NotificationService(getContext());

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fatigueDrowsinessBtn.setOnClickListener(view ->
                notificationService.sendNotification("You are fatigued!", "Please be alert...", getActivity()));

        binding.speedLimitBtn.setOnClickListener(view ->
                notificationService.sendNotification("You're too fast!", "Please slow down your speed...", getActivity()));

        binding.drivingPatternBtn.setOnClickListener(view ->
                notificationService.sendNotification("Erratic driving detected!", "Please drive rationally...", getActivity()));


        final TextView textView = binding.textNotifications;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}