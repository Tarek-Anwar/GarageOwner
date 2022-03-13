package com.homegarage.garageowner.notifcation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.CarInfo;
import com.homegarage.garageowner.model.Opreation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Dialog extends DialogFragment {
    TextView balanceTV;
    EditText balanceET;
    Button depositBTN;
    CarInfo carInfo;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CarInfo");
    String txt;
    DatabaseReference referenceOperattion = FirebaseUtil.referenceOperattion;
    ArrayList<Opreation> opreations = FirebaseUtil.payOpreations;

    SimpleDateFormat formatterLong = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", new Locale("en"));



    public Dialog(CarInfo carInfo) {
        this.carInfo = carInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog, container, false);
        initViews(view);
        txt = balanceTV.getText().toString();
        balanceTV.setText(txt + carInfo.getBalance());


        getDepsit(new Deposit() {
            @Override
            public void onCarRecived(CarInfo carInfo) {
                balanceTV.setText(txt + carInfo.getBalance());
            }

            @Override
            public void OnDeositAdded(float deposit) {
                depositBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float depositNum = Float.valueOf(balanceET.getText().toString());
                            carInfo.setBalance(depositNum + deposit);
                            balanceTV.setText(txt + carInfo.getBalance());
                            reference.child(carInfo.getId()).child("balance").setValue(carInfo.getBalance());

                        //creat opreation and save to last opreation list
                        Opreation opreation = new Opreation();
                        Date date = new Date(System.currentTimeMillis());
                        String dateOpreation = formatterLong.format(date);
                        opreation.setDate(dateOpreation);
                        opreation.setState("3");
                        opreation.setType("4");
                        opreation.setFrom(FirebaseUtil.mFirebaseAuthl.getUid());
                        opreation.setTo(carInfo.getId());
                        opreation.setPrice(depositNum);
                        opreation.setFromName(FirebaseUtil.userGarageInfo.getNameEn());
                        opreation.setToName(carInfo.getName());
                        opreation.setId(referenceOperattion.push().getKey());
                        referenceOperattion.child(opreation.getId()).setValue(opreation);

                        opreations.add(opreation);
                        Toast.makeText(getContext(), "thanks", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return view;
    }

    private void initViews(View view) {
        balanceTV = view.findViewById(R.id.carBalance);
        balanceET = view.findViewById(R.id.balanceET);
        depositBTN = view.findViewById(R.id.depoistBTN);
    }
    void getDepsit(Deposit deposit) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CarInfo car= snapshot.child(carInfo.getId()).getValue(CarInfo.class);
                deposit.onCarRecived(car);
                float balance = snapshot.child(carInfo.getId()).child("balance").getValue(Float.class);
                deposit.OnDeositAdded(balance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface Deposit {
         void onCarRecived(CarInfo carInfo);
         void OnDeositAdded(float deposit);
    }
}
