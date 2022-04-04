package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentSginUpBinding;
import com.homegarage.garageowner.model.InfoUserGarageModel;

import java.util.Objects;


public class SginUp1Fragment extends Fragment {

    private FragmentSginUpBinding binding;
    private AwesomeValidation  mAwesomeValidation;
    private InfoUserGarageModel infoUserGarageModel;

    public SginUp1Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getContext());
        infoUserGarageModel = FirebaseUtil.userGarageSign;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSginUpBinding.inflate(getLayoutInflater());

        addValidationForEditText();
        binding.checkSign1.setOnClickListener(v -> {
            if (mAwesomeValidation.validate()) {
                binding.scrollSign1.fullScroll(View.FOCUS_DOWN);
                binding.nextSign1.setVisibility(View.VISIBLE);
                binding.layoutSuccess1.setVisibility(View.VISIBLE);
            } else {
                binding.layoutSuccess1.setVisibility(View.GONE);
            }
        });

        binding.nextSign1.setOnClickListener(v -> {
            infoUserGarageModel.setEmail(binding.etEmailSign.getText().toString().trim());

            FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
            firebaseAuth.createUserWithEmailAndPassword(infoUserGarageModel.getEmail(), binding.etPasswordSign.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        SignUp2Fragment newFragment = new SignUp2Fragment();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainerView, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else {
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        return binding.getRoot();
    }

    private void addValidationForEditText() {
        mAwesomeValidation.addValidation(binding.etPasswordSign, "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}",getString(R.string.invalid_password));
        mAwesomeValidation.addValidation(binding.etConfirmPassword, binding.etPasswordSign,getString(R.string.password_confirmation));
        mAwesomeValidation.addValidation(binding.etEmailSign, Patterns.EMAIL_ADDRESS, getString(R.string.email_valid));

    }

}