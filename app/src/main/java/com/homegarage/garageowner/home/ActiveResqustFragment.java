package com.homegarage.garageowner.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import com.homegarage.garageowner.databinding.FragmentActiveResqustBinding;
import com.homegarage.garageowner.model.CarInfo;
import com.homegarage.garageowner.model.Opreation;
import com.homegarage.garageowner.model.PurchaseModel;
import com.homegarage.garageowner.notifcation.DialogDespost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ActiveResqustFragment extends Fragment {

    FragmentActiveResqustBinding binding;
    Opreation opreation;
    CarInfo carInfo;
    Date start = null;
    Date end = null;
    Long diff;
    volatile boolean con;
    int countProgress , round ;
    String roundTxt  ;
    DatabaseReference referenceOper ;
    DatabaseReference refPushase = FirebaseUtil.referencePurchase;
    SimpleDateFormat formatterLong =new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa" , new Locale("en"));

    public ActiveResqustFragment(Opreation opreation) {
        this.opreation = opreation;
        referenceOper = FirebaseUtil.referenceOperattion.child(opreation.getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActiveResqustBinding.inflate(getLayoutInflater());

        roundTxt = getActivity().getString(R.string.rotating)+ " : ";
        String finsh= getActivity().getString(R.string.finshed_requst);
        String pay = getActivity().getString(R.string.pay_type);
        String egPound = " " + getActivity().getString(R.string.eg);
        String depost = getActivity().getString(R.string.deposit);

        getCarOwer(new OnCarOwnerGetCallback() {
            @Override
            public void carOwnerGetCallback(CarInfo carInfo) {
                binding.nameCarReq.setText(carInfo.getName());
                binding.phoneCarReq.setText(carInfo.getPhone());
            }

            @Override
            public void operationGetCallback(Opreation opreation) {
                if(opreation.getType().equals("2") && opreation.getState().equals("2")){
                    binding.totalType.setVisibility(View.GONE);
                    binding.btnDepostReser.setText(finsh);
                }else if(opreation.getType().equals("5")) {
                    binding.txtTotalHome.setText((opreation.getPrice()*-1)+"");
                    if( (-1*opreation.getPrice()) < carInfo.getBalance() ){
                        binding.btnDepostReser.setText(pay);
                    }else {
                        binding.btnDepostReser.setText(depost);
                    }
                }

                if(System.currentTimeMillis() > start.getTime() && opreation.getState().equals("2")) {
                    if(binding.btnDepostReser.getText().equals(finsh)) {
                        binding.btnDepostReser.setOnClickListener(v -> {
                            Date date = new Date(System.currentTimeMillis());
                            if (opreation.getDataEnd() == null) {
                                opreation.setDataEnd(formatterLong.format(date));
                            }
                            opreation.setPrice(-1 * calPriceExpect(FirebaseUtil.userGarageInfo.getPriceForHour(), opreation.getDate(), opreation.getDataEnd()));
                            opreation.setState("3");
                            opreation.setType("5");
                            referenceOper.setValue(opreation);
                            binding.chronometer.stop();
                        });
                    }
                }else {
                    if(binding.btnDepostReser.getText().equals(pay)){
                        binding.btnDepostReser.setOnClickListener(v -> {
                            Date date = new Date(System.currentTimeMillis());
                            String dateOpreation = formatterLong.format(date);

                            PurchaseModel  purchase = new PurchaseModel();
                            purchase.setDate(dateOpreation);
                            purchase.setType("3");
                            purchase.setFrom(FirebaseUtil.mFirebaseAuthl.getUid());
                            purchase.setTo(carInfo.getId());
                            purchase.setValue(opreation.getPrice()*-1);
                            purchase.setFromName(FirebaseUtil.mFirebaseAuthl.getCurrentUser().getDisplayName());
                            purchase.setToName(carInfo.getName());
                            purchase.setId(refPushase.push().getKey());
                            refPushase.child(purchase.getId()).setValue(purchase);

                            referenceOper.child("price").setValue(opreation.getPrice()*-1);
                            getParentFragmentManager().popBackStackImmediate();
                        });
                    }else if(binding.btnDepostReser.getText().equals(depost)){
                        binding.btnDepostReser.setOnClickListener(v -> {
                            DialogDespost dialogDespost = new DialogDespost(carInfo);
                            dialogDespost.show(getParentFragmentManager() , "Despost");
                        });
                    }
                }
            }
        });

        setProgressBar();

        return  binding.getRoot();
    }

    interface OnCarOwnerGetCallback{
        void carOwnerGetCallback(CarInfo carInfo);
        void operationGetCallback(Opreation opreation);
    }

    private void getCarOwer(OnCarOwnerGetCallback callback){
        DatabaseReference reference = FirebaseUtil.referenceCar.child(opreation.getFrom());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carInfo = snapshot.getValue(CarInfo.class);
                if(opreation!=null){
                    callback.carOwnerGetCallback(carInfo);
                    callback.operationGetCallback(opreation);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        referenceOper.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 opreation = snapshot.getValue(Opreation.class);
                if(carInfo!=null){
                    callback.carOwnerGetCallback(carInfo);
                    callback.operationGetCallback(opreation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setProgressBar(){
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
            binding.roundTime.setText(roundTxt + round);}

        binding.chronometer.setBase(SystemClock.elapsedRealtime() - diff);
        if(con && (opreation.getState().equals("1") || opreation.getState().equals("2"))){
            binding.progressBar.setMax(2160);
            binding.chronometer.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(countProgress==2160){
                        binding.progressBar.setProgress(countProgress);
                        round++;
                        binding.roundTime.setText(round + round);
                        countProgress=0;
                        handler.postDelayed(this,5000);
                    }else if(countProgress<2160){
                        binding.progressBar.setProgress(countProgress);
                        countProgress++;
                        handler.postDelayed(this,5000);
                    }else{ handler.removeCallbacks(this); }}
            },5000);

        }else if(con == false) {
            binding.progressBar.setMax(countProgress);
            binding.chronometer.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(countProgress==0){
                        con=true;
                        binding.progressBar.setProgress(countProgress);
                        setProgressBar();
                    }
                    if(countProgress>0){
                        binding.progressBar.setProgress(countProgress);
                        countProgress--;
                        handler.postDelayed(this,5000);
                    }else{ handler.removeCallbacks(this); }}
            },5000);
        }
    }

    private float calPriceExpect(Float f , String s_time , String e_time){
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = formatterLong.parse(s_time);
            d2 = formatterLong.parse(e_time);
        } catch (ParseException e) { e.printStackTrace(); }
        Long diff = d2.getTime() - d1.getTime();
        Long diffMinets = diff / (60 * 1000) ;
        float total =   diffMinets * f / 60;
        if(total<10) return 10;
        else  return total ;
    }

}