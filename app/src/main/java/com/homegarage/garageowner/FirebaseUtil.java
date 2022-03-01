package com.homegarage.garageowner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.homegarage.garageowner.model.InfoUserGarageModel;

import java.util.ArrayList;

public class FirebaseUtil {

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    public static FirebaseAuth mFirebaseAuthl;
    public static InfoUserGarageModel userGarageModel;

    private FirebaseUtil(){}

    public static void openFbReference(String ref){
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuthl = FirebaseAuth.getInstance();
        }
        userGarageModel = new InfoUserGarageModel();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }


}
