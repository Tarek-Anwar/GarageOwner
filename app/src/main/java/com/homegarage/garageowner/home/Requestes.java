package com.homegarage.garageowner.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homegarage.garageowner.R;
import com.homegarage.garageowner.adapter.RequstOperAdapter;
import com.homegarage.garageowner.databinding.FragmentRequestesBinding;


public class Requestes extends Fragment {
    FragmentRequestesBinding binding;
    RequstOperAdapter adapter;

    public Requestes() { }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter=new RequstOperAdapter();
        binding=FragmentRequestesBinding.inflate(getLayoutInflater());
        binding.recyclerRequst.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerRequst.setAdapter(adapter);

        return binding.getRoot();
    }
}