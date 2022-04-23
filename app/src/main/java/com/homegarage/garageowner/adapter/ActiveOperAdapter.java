package com.homegarage.garageowner.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.Opreation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActiveOperAdapter extends RecyclerView.Adapter<ActiveOperAdapter.ViewHolder> {
    public ArrayList<Opreation> opreations;


    public ActiveOperAdapter() {
         opreations= FirebaseUtil.activeOpreations;
        DatabaseReference opreationsRef=FirebaseUtil.referenceOperattion;
        Query query = opreationsRef.orderByChild("to").equalTo(FirebaseUtil.mFirebaseAuthl.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        opreations.clear();
                        Opreation opreation= item.getValue(Opreation.class);
                        if ((opreation.getState().equals("2") && opreation.getType().equals("2"))) {
                            opreations.add(opreation);
                            notifyItemChanged(opreations.size()-1);
                        }
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.active_item_view,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(opreations.get(position));
    }

    @Override
    public int getItemCount() {
        return opreations.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView carOnwer,date;
        ProgressBar progressBar;
        Chronometer chronometer;
        Date start = null;
        int progress=0;
        Long diff;
        SimpleDateFormat formatterLong =new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa" , new Locale("en"));

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carOnwer=itemView.findViewById(R.id.carTV);
            date=itemView.findViewById(R.id.dateTV);
            progressBar=itemView.findViewById(R.id.progressBar);
            chronometer=itemView.findViewById(R.id.chronometer);
        }

        public void  bind(Opreation opreation) {
            date.setText("Date : " + opreation.getDate());
            carOnwer.setText("Car Owner : " + opreation.getFromName());

            try { start = formatterLong.parse(opreation.getDate());
                diff = System.currentTimeMillis() - start.getTime();
            } catch (ParseException e) { e.printStackTrace(); }


            progress = (int) (diff / 10000);

            chronometer.setBase(SystemClock.elapsedRealtime() - diff);
            progressBar.setMax(1100);
            chronometer.start();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(progress<1100) {
                        progressBar.setProgress(progress);
                        progress++;
                        handler.postDelayed(this,10000);
                    }
                    else
                    {
                        handler.removeCallbacks(this);
                }}
            },10000);
        }
    }
}
