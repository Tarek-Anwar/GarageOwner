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
import com.homegarage.garageowner.model.MoneyModel;
import com.homegarage.garageowner.model.Opreation;
import com.homegarage.garageowner.model.PurchaseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DialogDespost extends DialogFragment {

    TextView balanceTV;
    EditText balanceET;
    Button depositBTN;
    CarInfo carInfo;
    MoneyModel moneyModel;
    String txt;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CarInfo");
    DatabaseReference refAppBalance = FirebaseDatabase.getInstance().getReference().child("App").child(FirebaseUtil.mFirebaseAuthl.getUid());
    DatabaseReference referenceOperattion = FirebaseUtil.referenceOperattion;
    DatabaseReference refPushase = FirebaseUtil.referencePurchase;
    ArrayList<Opreation> opreations = FirebaseUtil.payOpreations;


    SimpleDateFormat formatterLong = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", new Locale("en"));


    public DialogDespost(CarInfo carInfo) {
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


        getDepsit(new OnDepositGetCallback() {
            @Override
            public void onCarRecived(CarInfo carInfo) {
                balanceTV.setText(txt + carInfo.getBalance());
            }

            @Override
            public void OnDeositAdded(MoneyModel moneyModel) {
                depositBTN.setOnClickListener(view1 -> {
                    float depositNum =Float.parseFloat(balanceET.getText().toString());
                    carInfo.setBalance(depositNum + carInfo.getBalance());
                    balanceTV.setText("Balance " + carInfo.getBalance());
                    reference.child(carInfo.getId()).child("balance").setValue(carInfo.getBalance());

                    moneyModel.setAppPercent(moneyModel.getAppPercent()+depositNum);
                    refAppBalance.child("appPercent").setValue(moneyModel.getAppPercent());
                    refAppBalance.child("totalBalance").setValue(moneyModel.getMoneyForGarage()-moneyModel.getAppPercent());

                    PurchaseModel opreation = new PurchaseModel();
                    Date date = new Date(System.currentTimeMillis());
                    String dateOpreation = formatterLong.format(date);
                    opreation.setDate(dateOpreation);
                    opreation.setType("3");
                    opreation.setFrom(FirebaseUtil.mFirebaseAuthl.getUid());
                    opreation.setTo(carInfo.getId());
                    opreation.setValue(depositNum);
                    opreation.setFromName(FirebaseUtil.mFirebaseAuthl.getCurrentUser().getDisplayName());
                    opreation.setToName(carInfo.getName());
                    opreation.setId(refPushase.push().getKey());
                    refPushase.child(opreation.getId()).setValue(opreation);
                    Toast.makeText(getContext(), "thanks", Toast.LENGTH_SHORT).show();
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
    void getDepsit(OnDepositGetCallback callback) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CarInfo car= snapshot.child(carInfo.getId()).getValue(CarInfo.class);
                callback.onCarRecived(car);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        refAppBalance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    moneyModel = snapshot.getValue(MoneyModel.class);
                    callback.OnDeositAdded(moneyModel);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }



    public interface OnDepositGetCallback {
         void onCarRecived(CarInfo carInfo);
         void OnDeositAdded(MoneyModel moneyModel);
    }
}
