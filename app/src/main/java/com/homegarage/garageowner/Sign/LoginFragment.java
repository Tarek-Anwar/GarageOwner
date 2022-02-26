package com.homegarage.garageowner.Sign;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;

public class LoginFragment extends Fragment {

    private EditText et_email ,et_password;
    private Button login , sign;
    public LoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_login, container, false);

        intiUI(view);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= et_email.getText().toString().trim();
                String pass = et_password.getText().toString().trim();
                if(!email.isEmpty()&&!pass.isEmpty()){
                    FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Welcome", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity() , MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getContext() ,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

        sign.setOnClickListener(v -> {
            SginUpFragment fragment = new SginUpFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        return view;
    }

    private void intiUI(View view) {

        et_email = view.findViewById(R.id.et_email_log);
        et_password = view.findViewById(R.id.et_password_log);
        login = view.findViewById(R.id.btn_login);
        sign = view.findViewById(R.id.bt_go_sign);

    }


}