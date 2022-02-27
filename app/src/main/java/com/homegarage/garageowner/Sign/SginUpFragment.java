package com.homegarage.garageowner.Sign;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class SginUpFragment extends Fragment {

    private EditText user , phone , email , password;
    private Button sign;
    FirebaseUser firebaseUser;
    public SginUpFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sgin_up, container, false);
        intiUI(view);

            sign.setOnClickListener(v -> {
                FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
                String nameuser = user.getText().toString().trim();
                String phoneuser = phone.getText().toString().trim();
                String passwoeduser = password.getText().toString().trim();
                String emailuser = email.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(emailuser,passwoeduser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DatabaseReference databaseReference  = FirebaseUtil.mDatabaseReference;
                            firebaseUser=task.getResult().getUser();
                            DatabaseReference newuser = databaseReference.child(firebaseUser.getUid());
                            newuser.child("Full Name").setValue(nameuser);
                            newuser.child("Phone").setValue(phoneuser);
                            newuser.child("password").setValue(passwoeduser);
                            newuser.child("email").setValue(emailuser);
                            Toast.makeText(getContext(), "Sussful sign", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity() , MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        return view;
    }

    void intiUI(View view){
        user = view.findViewById(R.id.full_name);
        phone = view.findViewById(R.id.phone);
        password = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        sign = view.findViewById(R.id.sign_up);
    }
}