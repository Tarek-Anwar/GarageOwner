package com.homegarage.garageowner;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homegarage.garageowner.model.InfoUserGarageModel;

import java.util.ArrayList;

public class FirebaseUtil {

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    public static FirebaseAuth mFirebaseAuthl;
    public static InfoUserGarageModel userGarageModel;
    public static StorageReference mStorageReference;
    public static FirebaseStorage mStorage;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static DatabaseReference referenceOperattion;

    private FirebaseUtil(){}

    public static void openFbReference(String ref , String refOperattion ){
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuthl = FirebaseAuth.getInstance();
            mAuthStateListener = firebaseAuth -> {
                if(firebaseAuth.getCurrentUser() != null){
                    Log.i("dsfdsfdsfsdf", "sign im");
                }else{
                    Log.i("dsfdsfdsfsdf", "sign out");
                }
            };
            connectStorage();
        }
        userGarageModel = new InfoUserGarageModel();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
        referenceOperattion = mFirebaseDatabase.getReference().child(refOperattion);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("garage_owner_pictures");

    }

    public static void attachListener() {
        mFirebaseAuthl.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener() { mFirebaseAuthl.removeAuthStateListener(mAuthStateListener); }

}
