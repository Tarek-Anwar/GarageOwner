package com.homegarage.garageowner.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.Opreation;

public class RequstOperAdapter extends RecyclerView.Adapter<RequstOperAdapter.RequstViewHolder> {


    @NonNull
    @Override
    public RequstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RequstViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
            typeOper.setText(opreation.getType());
        }

    }
}
