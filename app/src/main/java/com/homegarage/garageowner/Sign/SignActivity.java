package com.homegarage.garageowner.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        FirebaseUtil.openFbReference("GaragerOnwerInfo");

    }
}