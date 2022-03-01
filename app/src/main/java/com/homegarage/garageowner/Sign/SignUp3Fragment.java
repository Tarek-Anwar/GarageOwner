package com.homegarage.garageowner.Sign;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentSignUp3Binding;
import com.homegarage.garageowner.model.InfoUserGarageModel;

public class SignUp3Fragment extends Fragment {

    InfoUserGarageModel infoUserGarageModel;

    private FragmentSignUp3Binding binding;
    private ActivityResultLauncher<Object> launcher;
    public SignUp3Fragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoUserGarageModel = FirebaseUtil.userGarageModel;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignUp3Binding.inflate(getLayoutInflater());


        return  binding.getRoot();
    }
}