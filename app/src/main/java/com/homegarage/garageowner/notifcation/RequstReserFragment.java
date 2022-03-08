package com.homegarage.garageowner.notifcation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentRequstReserBinding;
import com.homegarage.garageowner.model.CarInfo;
import com.homegarage.garageowner.model.Opreation;


public class RequstReserFragment extends Fragment {

    String idOpreationl;
    FragmentRequstReserBinding binding;
    CarInfo carInfo;
    Opreation opreation;
    DatabaseReference refOperation;
    public RequstReserFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRequstReserBinding.inflate(getLayoutInflater());

        idOpreationl = NotificationActivity.idOpreation;
        refOperation = FirebaseUtil.mFirebaseDatabase.getReference("Operation").child(idOpreationl);

        getOpreation(new OnDataReceiveCallback() {
            @Override
            public void onDataReceived(String date) {
                binding.txtTimeReser.setText(date);
            }

            @Override
            public void onIDReceived(String id) {
                getCarInfo(id , date -> binding.txtNameCarOwner.setText(date));
            }
        });

        binding.profileImageCar.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerViewNotification,new CarProfileFragment(carInfo) , "CarProfileFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.btnAccpetReser.setOnClickListener(v -> {
            opreation.setFromName(carInfo.getName());
            refOperation.setValue(opreation);
        });
        return binding.getRoot();
    }

    public interface OnDataReceiveCallback {
        void onDataReceived(String date);
        void  onIDReceived(String id);
    }

    public interface OnIDReceiveCallback {
        void onIDReceived(String date);
    }

    private  void getOpreation(OnDataReceiveCallback callback){

        refOperation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opreation = snapshot.getValue(Opreation.class);
                callback.onDataReceived(opreation.getDate());
                callback.onIDReceived(opreation.getFrom());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCarInfo(String id , OnIDReceiveCallback callback){

        DatabaseReference ref = FirebaseUtil.mFirebaseDatabase.getReference("CarInfo").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carInfo = snapshot.getValue(CarInfo.class);
                callback.onIDReceived(carInfo.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}