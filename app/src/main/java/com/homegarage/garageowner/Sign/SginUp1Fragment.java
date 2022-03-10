package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentSginUpBinding;
import com.homegarage.garageowner.model.InfoUserGarageModel;


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
            infoUserGarageModel.setNameEn(binding.etNameENSign.getText().toString().trim());
            infoUserGarageModel.setNameAr(binding.etNameArSign.getText().toString().trim());
            infoUserGarageModel.setPhone(binding.etPhoneSign.getText().toString());

            SignUp2Fragment newFragment = new SignUp2Fragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return binding.getRoot();
    }

    private void addValidationForEditText() {
        mAwesomeValidation.addValidation(binding.etNameENSign, RegexTemplate.NOT_EMPTY,getString(R.string.name_invalid));
        mAwesomeValidation.addValidation(binding.etNameArSign, RegexTemplate.NOT_EMPTY,getString(R.string.name_invalid));
        mAwesomeValidation.addValidation(binding.etEmailSign, Patterns.EMAIL_ADDRESS, getString(R.string.email_valid));
        mAwesomeValidation.addValidation(binding.etPhoneSign,"^01[0125][0-9]{8}$",getString(R.string.phone_invalid));
    }

}