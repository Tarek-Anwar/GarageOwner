package com.homegarage.garageowner.notifcation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentCarProfileBinding;
import com.homegarage.garageowner.model.CarInfo;

public class CarProfileFragment extends Fragment {

    FragmentCarProfileBinding binding;
    CarInfo carInfo;

    public CarProfileFragment(CarInfo carInfo) {
        this.carInfo = carInfo;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCarProfileBinding.inflate(getLayoutInflater());

        binding.txtEmailCar.setText(carInfo.getEmail());
        binding.txtNameCar.setText(carInfo.getName());
        binding.txtPhoneCar.setText(carInfo.getPhone());

        return binding.getRoot();
    }
}