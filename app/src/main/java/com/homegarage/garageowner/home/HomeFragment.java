package com.homegarage.garageowner.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.adapter.RequstOperAdapter;
import com.homegarage.garageowner.databinding.FragmentHomeBinding;
import com.homegarage.garageowner.model.Opreation;
import com.homegarage.garageowner.service.FcmNotificationsSender;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    RequstOperAdapter  adapter ;
    HomeFragment fragment;

    public HomeFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentHomeBinding.inflate(getLayoutInflater());

        adapter  = new RequstOperAdapter();

        binding.recyclerRequst.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false));
        binding.recyclerRequst.setAdapter(adapter);

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }


}