package com.homegarage.garageowner.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.homegarage.garageowner.adapter.ActiveOperAdapter;
import com.homegarage.garageowner.adapter.RequstOperAdapter;
import com.homegarage.garageowner.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    public HomeFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentHomeBinding.inflate(getLayoutInflater());

        binding.recyclerRequst.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false));
        binding.recyclerRequst.setAdapter(new RequstOperAdapter());

        binding.activeRV.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        binding.activeRV.setAdapter(new ActiveOperAdapter());

        return binding.getRoot();
    }


}