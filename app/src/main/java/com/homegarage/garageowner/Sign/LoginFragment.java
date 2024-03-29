package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentLoginBinding;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private AwesomeValidation mAwesomeValidation;
    private FragmentLoginBinding binding;

    public LoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        addValidationForEditText();

        binding.btnLogin.setOnClickListener(v -> {

            if(mAwesomeValidation.validate()){
                String email= binding.etEmailLog.getText().toString().trim();
                String pass = binding.etPasswordLog.getText().toString().trim();
                FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Welcome", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity() , MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getContext() , Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.btnGoSign.setOnClickListener(v -> {
            SginUp1Fragment fragment = new SginUp1Fragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return  binding.getRoot();
    }

    private void addValidationForEditText() {
        mAwesomeValidation.addValidation(binding.etPasswordLog,RegexTemplate.NOT_EMPTY,getString(R.string.invalid_password));
        mAwesomeValidation.addValidation(binding.etEmailLog, Patterns.EMAIL_ADDRESS, getString(R.string.email_valid));
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }
}