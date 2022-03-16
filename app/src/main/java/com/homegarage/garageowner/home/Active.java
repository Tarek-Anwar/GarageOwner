package com.homegarage.garageowner.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homegarage.garageowner.R;
import com.homegarage.garageowner.adapter.ActiveOperAdapter;
import com.homegarage.garageowner.databinding.FragmentActiveBinding;


public class Active extends Fragment {
    FragmentActiveBinding binding;
    ActiveOperAdapter adapter;

    public Active() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter=new ActiveOperAdapter();

        binding=FragmentActiveBinding.inflate(getLayoutInflater());
        binding.recyclerActive.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.recyclerActive.setAdapter(adapter);


        return binding.getRoot();
    }
}