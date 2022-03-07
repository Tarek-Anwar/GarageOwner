package com.homegarage.garageowner.notifcation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.homegarage.garageowner.databinding.ActivityNotificationBinding;
import com.homegarage.garageowner.service.FirebaseMessagingService;

public class NotificationActivity extends AppCompatActivity {
    public static String idOpreation;

    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        idOpreation  = getIntent().getStringExtra(FirebaseMessagingService.ID_OPERATTON);
    }

}