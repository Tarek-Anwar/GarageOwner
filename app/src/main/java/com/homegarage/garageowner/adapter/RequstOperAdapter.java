package com.homegarage.garageowner.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.Opreation;
import com.homegarage.garageowner.notifcation.NotificationActivity;
import com.homegarage.garageowner.service.FcmNotificationsSender;

import java.util.ArrayList;

public class RequstOperAdapter extends RecyclerView.Adapter<RequstOperAdapter.RequstViewHolder> {


    ArrayList <Opreation> opreationslist;
    DatabaseReference reference;

    public RequstOperAdapter() {
        reference = FirebaseUtil.referenceOperattion;
        opreationslist = FirebaseUtil.reqstOperaionList;
        reference.addChildEventListener(new ChildEventListener() {
            Opreation opreation;
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    opreation = snapshot.getValue(Opreation.class);
                    if(opreation.getState().equals("1") && opreation.getType().equals("1")) {
                        opreationslist.add(opreation);
                        notifyItemChanged(opreationslist.size()-1);
                    }
                    notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { notifyDataSetChanged(); }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { notifyDataSetChanged(); }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @NonNull
    @Override
    public RequstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.opreation_wait_row,parent,false);
        return  new RequstViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RequstViewHolder holder, int position) {
        holder.BulidUI(opreationslist.get(position));
    }

    @Override
    public int getItemCount() {
        return opreationslist.size();
    }

    public class RequstViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameCar , dateOper , typeOper;
        Button btnAccpet , btnRefusal;
        public RequstViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCar = itemView.findViewById(R.id.text_name_car_owner);
            dateOper = itemView.findViewById(R.id.text_date_opre);
            typeOper = itemView.findViewById(R.id.text_state_oper);
            btnAccpet = itemView.findViewById(R.id.btn_accpet_requst);
            btnRefusal = itemView.findViewById(R.id.btn_reusal_req);
            itemView.setOnClickListener(this);
        }

        public void BulidUI(Opreation opreation){

            nameCar.setText(opreation.getFromName());
            dateOper.setText(opreation.getDate());
            typeOper.setText(FirebaseUtil.typeList.get(Integer.parseInt(opreation.getType())-1));

            btnAccpet.setOnClickListener(v -> {
                opreation.setState("2");
                opreation.setType("2");
                reference.child(opreation.getId()).setValue(opreation);
                btnRefusal.setEnabled(false);
                btnAccpet.setEnabled(false);

                new Handler().postDelayed(() -> {
                    opreationslist.remove(opreation);
                    notifyDataSetChanged();
                    btnRefusal.setEnabled(true);
                    btnAccpet.setEnabled(true);

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            opreation.getFrom()
                            ,"Accpet"
                            ,"Accpet Reservion from Garage " + opreation.getToName()
                            ,opreation.getId(), itemView.getContext());
                    notificationsSender.SendNotifications();

                }, 2000);

                Toast.makeText(itemView.getContext(), FirebaseUtil.typeList.get(Integer.parseInt(opreation.getType())-1), Toast.LENGTH_SHORT).show();
            });

            btnRefusal.setOnClickListener(v -> {
                opreation.setState("2");
                opreation.setType("3");
                reference.child(opreation.getId()).setValue(opreation);
                btnRefusal.setEnabled(false);
                btnAccpet.setEnabled(false);

                new Handler().postDelayed(() -> {
                   opreationslist.remove(opreation);
                    notifyDataSetChanged();
                    btnRefusal.setEnabled(true);
                    btnAccpet.setEnabled(true);

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            opreation.getFrom()
                            ,"Refusal"
                            ,"sorry , Reservion cancel from Garage " + opreation.getToName()
                            ,opreation.getId(), itemView.getContext());
                    notificationsSender.SendNotifications();

                }, 2000);

                Toast.makeText(itemView.getContext(), FirebaseUtil.typeList.get(Integer.parseInt(opreation.getType())-1), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), NotificationActivity.class);
            intent.putExtra("modelOper" , opreationslist.get(getAdapterPosition()));
            v.getContext().startActivity(intent);
        }
    }

}
