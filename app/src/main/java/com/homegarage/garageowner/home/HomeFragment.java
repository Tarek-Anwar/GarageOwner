package com.homegarage.garageowner.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentHomeBinding;
import com.homegarage.garageowner.service.FcmNotificationsSender;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    public HomeFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentHomeBinding.inflate(getLayoutInflater());

      /*  FirebaseMessaging.getInstance().subscribeToTopic("all");

        binding.btnSendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.titleNot.getText().toString().isEmpty()
                        && !binding.bodyNot.getText().toString().isEmpty()){

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(

                            "/topics/all" , binding.titleNot.getText().toString()
                            ,binding.bodyNot.getText().toString() , requireContext(),requireActivity());

                    notificationsSender.SendNotifications();
                }else {
                    Toast.makeText(requireContext(), "Write some text", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        return binding.getRoot();
    }
}