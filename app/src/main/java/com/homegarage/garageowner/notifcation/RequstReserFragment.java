package com.homegarage.garageowner.notifcation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentRequstReserBinding;
import com.homegarage.garageowner.model.CarInfo;
import com.homegarage.garageowner.model.Opreation;
import com.homegarage.garageowner.service.FcmNotificationsSender;


public class RequstReserFragment extends Fragment {

    String idOpreationl = null;
    FragmentRequstReserBinding binding;
    CarInfo carInfo;
    Opreation opreation = null;
    DatabaseReference refOperation;

    public RequstReserFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRequstReserBinding.inflate(getLayoutInflater());

        idOpreationl = NotificationActivity.idOpreation;
        if(idOpreationl != null){
            Log.i("oper_string" , idOpreationl);
            refOperation = FirebaseUtil.mFirebaseDatabase.getReference("Operation").child(idOpreationl);
            getOpreation(new OnDataReceiveCallback() {
                @Override
                public void onDataReceived(String date) {
                    binding.txtTimeReser.setText(date);
                }

                @Override
                public void onIDReceived(String id) {
                    getCarInfo(opreation.getFrom(), date -> binding.txtNameCarOwner.setText(date));
                }
            });
        }

        opreation = NotificationActivity.allOpreation;
        if(opreation != null){
            refOperation = FirebaseUtil.mFirebaseDatabase.getReference("Operation").child(opreation.getId());
            binding.txtTimeReser.setText(opreation.getDate());
            getCarInfo(opreation.getFrom(), date -> binding.txtNameCarOwner.setText(date));
        }

        binding.profileImageCar.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerViewNotification,new CarProfileFragment(carInfo) , "CarProfileFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        });

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        binding.btnAccpetReser.setOnClickListener(v -> {
            opreation.setState("2");
            opreation.setType("2");
            refOperation.setValue(opreation);

            binding.btnAccpetReser.setEnabled(false);
            binding.btnRefusalReser.setEnabled(false);

            binding.btnAccpetReser.setEnabled(false);
            binding.btnRefusalReser.setEnabled(false);
            new Handler().postDelayed(() -> {

                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                        carInfo.getId()
                        , getString(FirebaseUtil.typeList.get(1))
                        ,"Accpet Reservion from Garage " + opreation.getToName()
                        , opreation.getId(), getContext());
                notificationsSender.SendNotifications();

                refOperation.setValue(opreation);
                FirebaseUtil.reqstOperaionList.remove(opreation);
                startActivity(intent);
            }, 2000);

        });

        binding.btnRefusalReser.setOnClickListener(v -> {
            opreation.setState("3");
            opreation.setType("3");

            binding.btnAccpetReser.setEnabled(false);
            binding.btnRefusalReser.setEnabled(false);
            new Handler().postDelayed(() -> {
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                        carInfo.getId()
                        , getString(FirebaseUtil.typeList.get(2))
                        ,"sorry , reservion cancel from Garage " + opreation.getToName()
                        , opreation.getId(), getContext());

                notificationsSender.SendNotifications();
                refOperation.setValue(opreation);
                FirebaseUtil.reqstOperaionList.remove(opreation);
                startActivity(intent);
            }, 2000);


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
                assert opreation != null;
                callback.onDataReceived(opreation.getDate());
                callback.onIDReceived(opreation.getFrom());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getCarInfo(String id , OnIDReceiveCallback callback){

        DatabaseReference ref = FirebaseUtil.mFirebaseDatabase.getReference("CarInfo").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carInfo = snapshot.getValue(CarInfo.class);
                assert carInfo != null;
                opreation.setFromName(carInfo.getName());
                refOperation.setValue(opreation);
                callback.onIDReceived(carInfo.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}