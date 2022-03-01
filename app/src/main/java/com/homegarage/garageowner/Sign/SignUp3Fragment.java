package com.homegarage.garageowner.Sign;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentSignUp3Binding;
import com.homegarage.garageowner.model.InfoUserGarageModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SignUp3Fragment extends Fragment {

    InfoUserGarageModel model;

    private FragmentSignUp3Binding binding;
    private ActivityResultLauncher<Object> launcher;
    public SignUp3Fragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = FirebaseUtil.userGarageModel;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignUp3Binding.inflate(getLayoutInflater());

        ActivityResultContract<Object, Uri> contract = new ActivityResultContract<Object, Uri>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                return CropImage.activity()
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .getIntent(getContext());

            }

            @Override
            public Uri parseResult(int resultCode, @Nullable Intent intent) {
                if(CropImage.getActivityResult(intent)!=null){
                    return CropImage.getActivityResult(intent).getUri();
                }else {
                    return null;
                }
            }
        };

        launcher=registerForActivityResult(contract,uri->{
            if (uri!=null){
                binding.profileImage.setImageURI(uri);
            }
        });

        binding.profileImage.setOnClickListener(v->launcher.launch(null));

        binding.btnSignInFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
                firebaseAuth.createUserWithEmailAndPassword(model.getEmail(), model.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference databaseReference = FirebaseUtil.mDatabaseReference;
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            DatabaseReference newuser = databaseReference.child(firebaseUser.getUid());
                            model.setPriceForHour(Float.parseFloat(binding.etPriceForHoure.getText().toString()));
                            model.setImageGarage(" ");
                            newuser.setValue(model);
                            Toast.makeText(getContext(), "Sussful sign", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return  binding.getRoot();
    }
}