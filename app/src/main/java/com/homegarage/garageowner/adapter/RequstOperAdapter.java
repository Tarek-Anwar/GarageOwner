package com.homegarage.garageowner.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.Opreation;

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
                Log.i("Data onChildAdded", "onChildAdded  :  " + snapshot.getValue().toString());
                Log.i("Data onChildAdded" , "Size : " + opreationslist.size());
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       /* Query query =  reference.orderByChild("to").equalTo(FirebaseUtil.mFirebaseAuthl.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

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

    public class RequstViewHolder extends RecyclerView.ViewHolder {
        TextView nameCar , dateOper , typeOper;
        Button btnAccpet , btnRefusal;
        public RequstViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCar = itemView.findViewById(R.id.text_name_car_owner);
            dateOper = itemView.findViewById(R.id.text_date_opre);
            typeOper = itemView.findViewById(R.id.text_state_oper);
            btnAccpet = itemView.findViewById(R.id.btn_accpet_requst);
            btnRefusal = itemView.findViewById(R.id.btn_reusal_req);
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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        opreationslist.remove(opreation);
                        notifyDataSetChanged();

                    }
                }, 2000);


                Toast.makeText(itemView.getContext(), FirebaseUtil.typeList.get(Integer.parseInt(opreation.getType())-1), Toast.LENGTH_SHORT).show();
            });

            btnRefusal.setOnClickListener(v -> {
                opreation.setState("2");
                opreation.setType("3");
                reference.child(opreation.getId()).setValue(opreation);
                btnRefusal.setEnabled(false);
                btnAccpet.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       opreationslist.remove(opreation);
                        notifyDataSetChanged();
                    }
                }, 2000);

                Toast.makeText(itemView.getContext(), FirebaseUtil.typeList.get(Integer.parseInt(opreation.getType())-1), Toast.LENGTH_SHORT).show();
            });
        }

    }
}
