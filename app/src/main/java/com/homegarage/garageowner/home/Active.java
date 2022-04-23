package com.homegarage.garageowner.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homegarage.garageowner.R;
import com.homegarage.garageowner.adapter.ActiveOperAdapter;
import com.homegarage.garageowner.databinding.FragmentActiveBinding;
import com.homegarage.garageowner.model.Opreation;


public class Active extends Fragment {
    FragmentActiveBinding binding;
    ActiveOperAdapter adapter;

    public Active() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActiveBinding.inflate(getLayoutInflater());

        adapter=new ActiveOperAdapter();
        binding.recyclerActive.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.recyclerActive.setAdapter(adapter);


        return binding.getRoot();
    }
}