package com.homegarage.garageowner.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentHomeBinding;
import com.homegarage.garageowner.model.Opreation;
import com.homegarage.garageowner.service.FcmNotificationsSender;


public class HomeFragment extends Fragment {

    FirebaseUser user;
    FragmentHomeBinding binding;
    public HomeFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseUtil.mFirebaseAuthl.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentHomeBinding.inflate(getLayoutInflater());


        return binding.getRoot();
    }

    private void getOperationNow(){
        DatabaseReference reference = FirebaseUtil.referenceOperattion;
        Query query =  reference.orderByChild("to").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        Opreation opreation = item.getValue(Opreation.class);
                        if(opreation.getState().equals("Reqest")){
                            Log.i("dsfsdfsdfsdfxzc" , item.toString());}
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}