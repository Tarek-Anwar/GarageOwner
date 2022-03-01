package com.homegarage.garageowner.Sign;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.InfoUserGarageModel;

public class SignUp3Fragment extends Fragment {

    InfoUserGarageModel infoUserGarageModel;
    public SignUp3Fragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoUserGarageModel = FirebaseUtil.userGarageModel;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up3, container, false);
    }
}