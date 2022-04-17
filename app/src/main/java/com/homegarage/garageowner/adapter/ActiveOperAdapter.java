package com.homegarage.garageowner.adapter;

import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

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

    public ArrayList<Opreation> opreations = FirebaseUtil.activeOpreations;;
    ActiveListenr activeListenr;
    public ActiveOperAdapter(ActiveListenr activeListenr) {
        this.activeListenr = activeListenr;
        DatabaseReference opreationsRef=FirebaseUtil.referenceOperattion;
        Query query=opreationsRef.orderByChild("to").equalTo(FirebaseUtil.mFirebaseAuthl.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        opreations.clear();
                        Opreation opreation= item.getValue(Opreation.class);
                        if ( (opreation.getState().equals("2") && opreation.getType().equals("2"))
                                || opreation.getPrice() < 0
                        ) {
                            opreations.add(opreation);
                            notifyItemChanged(opreations.size()-1);
                        }notifyDataSetChanged();
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


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(opreations.get(position));
    }

    @Override
    public int getItemCount() {
        return opreations.size();

    }

    public  interface ActiveListenr{
        void onActiveListenr(Opreation opreation);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View activeOpreation;
        TextView carOnwer,date , roundTime;
        ProgressBar progressBar;
        Chronometer chronometer;
        Date start = null;
        Date end = null;
        String roundTxt  ;
        volatile boolean con;
        int countProgress , round ;
        Long diff;
        SimpleDateFormat formatterLong =new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa" , new Locale("en"));

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carOnwer=itemView.findViewById(R.id.carTV);
            date=itemView.findViewById(R.id.dateTV);
            progressBar=itemView.findViewById(R.id.progressBar);
            chronometer=itemView.findViewById(R.id.chronometer);
            activeOpreation = itemView.findViewById(R.id.active_opreation);
            roundTime = itemView.findViewById(R.id.round_time_txt);
        }

        public void  bind(Opreation opreation) {
            date.setText("Date : " + opreation.getDate());
            carOnwer.setText("Car Owner : " + opreation.getFromName());
          /*  try { start = formatterLong.parse(opreation.getDate());
            } catch (ParseException e) { e.printStackTrace(); }

             diff = System.currentTimeMillis() - start.getTime();
            progress = (int) (diff / 5000);

            chronometer.setBase(SystemClock.elapsedRealtime() - diff);
            progressBar.setMax(2160);
            chronometer.start();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(progress<2160) {
                        progressBar.setProgress(progress);
                        progress++;
                        handler.postDelayed(this,5000);
                    } else {handler.removeCallbacks(this); }
                }
            },5000);*/
            setProgressBar(opreation);
            activeOpreation.setOnClickListener(v -> activeListenr.onActiveListenr(opreation));
        }

        private void setProgressBar(Opreation opreation){

            roundTxt = itemView.getContext().getString(R.string.rotating)+ " : ";;
            try { start = formatterLong.parse(opreation.getDate());
            } catch (ParseException e) { e.printStackTrace(); }

            if(opreation.getDataEnd()==null) {
                diff = System.currentTimeMillis() - start.getTime();
            }else {
                try { end = formatterLong.parse(opreation.getDataEnd());
                } catch (ParseException e) { e.printStackTrace(); }
                diff = end.getTime() - start.getTime();
            }

            if(diff<0){ con = false;countProgress = (int) (-1 * diff / 5000);
            }else { con=true;countProgress = (int) (diff / 5000);
                round = (countProgress/2160) + 1;
                roundTime.setText(roundTxt + round);}

            chronometer.setBase(SystemClock.elapsedRealtime() - diff);
            if(con && (opreation.getState().equals("1") || opreation.getState().equals("2"))){
                progressBar.setMax(2160);
                chronometer.start();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(countProgress==2160){
                            progressBar.setProgress(countProgress);
                            round++;
                            roundTime.setText(roundTxt + round);
                            countProgress=0;
                            handler.postDelayed(this,5000);
                        }else if(countProgress<2160){
                            progressBar.setProgress(countProgress);
                            countProgress++;
                            handler.postDelayed(this,5000);
                        }else{ handler.removeCallbacks(this); }}
                },5000);

            }else if(con == false) {
                progressBar.setMax(countProgress);
                chronometer.start();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(countProgress==0){
                            con=true;
                            progressBar.setProgress(countProgress);
                        }
                        if(countProgress>0){
                            progressBar.setProgress(countProgress);
                            countProgress--;
                            handler.postDelayed(this,5000);
                        }else{ handler.removeCallbacks(this); }}
                },5000);
            }
        }

    }
}
