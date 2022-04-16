package com.homegarage.garageowner.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.Opreation;

import java.util.ArrayList;

public class FinishAdepter extends RecyclerView.Adapter<FinishAdepter.ViewHolder> {
    ArrayList<Opreation> opreations;
    Query query;
    DatabaseReference reference;

    public FinishAdepter() {
        opreations= FirebaseUtil.payOpreations;
        reference=FirebaseUtil.referenceOperattion;
        query=reference.orderByChild("to").equalTo(FirebaseUtil.mFirebaseAuthl.getUid());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    opreations.clear();
                    for(DataSnapshot item : snapshot.getChildren())
                    {
                        Opreation opreation=item.getValue(Opreation.class);
                        if(opreation.getState().equals("3")&&opreation.getType().equals("6"))
                        {
                            opreations.add(opreation);
                            notifyItemChanged(opreations.size()-1);
                            Log.i("mmmm",opreations.size()+"");
                        }
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @NonNull
    @Override
    public FinishAdepter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.finished_itemview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinishAdepter.ViewHolder holder, int position) {
        holder.bind(opreations.get(position));
    }

    @Override
    public int getItemCount() {
        return opreations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,date,price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.carOwner);
            date=itemView.findViewById(R.id.dateFinish);
            price=itemView.findViewById(R.id.price);
        }
        public void bind(Opreation opreation)
        {
            name.setText(opreation.getFromName());
            date.setText(opreation.getDate());
            price.setText(String.format("%.2f",opreation.getPrice()));
        }
    }
}
