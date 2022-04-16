package com.homegarage.garageowner.Sign;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentSignUp4Binding;
import com.homegarage.garageowner.model.InfoUserGarageModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.UUID;

public class Sign_up4 extends Fragment {
    FragmentSignUp4Binding binding;
    InfoUserGarageModel garageModel;
    ArrayList<InfoUserGarageModel> userGarageInfos = FirebaseUtil.userGarageInfo;;
    ActivityResultLauncher<Object> launcher,launcher1;
    DatabaseReference reference;
    AwesomeValidation validation;
    FirebaseUser user;

    public Sign_up4(FirebaseUser user) {
        this.user=user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference=FirebaseUtil.mDatabaseReference;
        garageModel = userGarageInfos.get(userGarageInfos.size()-1);
        validation=new AwesomeValidation(ValidationStyle.UNDERLABEL);
        validation.setContext(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSignUp4Binding.inflate(getLayoutInflater());

        ActivityResultContract<Object, Uri> contract=new ActivityResultContract<Object, Uri>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                return CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .getIntent(requireContext());
            }

            @Override
            public Uri parseResult(int resultCode, @Nullable Intent intent) {
                if(CropImage.getActivityResult(intent)!=null) {
                    return CropImage.getActivityResult(intent).getUri();
                }
                else return null;
            }
        };

        launcher=registerForActivityResult(contract, result -> {
            if ((result!=null)) {
                binding.firstIdImg.setImageURI(result);
                uploadIMG(result);
            }
        });
        launcher1=registerForActivityResult(contract, result -> {
            if ((result!=null)) {
                binding.secIdImg.setImageURI(result);
                uploadIMG(result);
            }
        });
        binding.imageID1Edit.setOnClickListener(view -> launcher.launch(null));
        binding.imageID2Edit.setOnClickListener(view -> launcher1.launch(null));
        validat();

        binding.checkSign4.setOnClickListener(view -> {
            if(validation.validate()) {
                binding.checkSign4.setVisibility(View.GONE);
                binding.nextSign4.setVisibility(View.VISIBLE);
                binding.layoutSuccess4.setVisibility(View.VISIBLE);
                garageModel.setBankAcountName(binding.bankAccountName.getEditText().getText().toString());
                garageModel.setBankAcountNum(binding.bankAccountNum.getEditText().getText().toString());
            }
            else
                Toast.makeText(getContext(), "invalid data", Toast.LENGTH_SHORT).show();
        });

        binding.nextSign4.setOnClickListener(view -> {
            garageModel.setBankAcountName(binding.bankAccountName.getEditText().getText().toString());
            garageModel.setBankAcountNum(binding.bankAccountNum.getEditText().getText().toString());
            reference.child(user.getUid()).child("bankAccountName").setValue(garageModel.getBankAcountName());
            reference.child(user.getUid()).child("bankAccountNum").setValue(garageModel.getBankAcountNum());

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    void uploadIMG(Uri uri) {
        StorageReference ref= FirebaseUtil.mStorageReference.child(UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> garageModel.setNationalIDImg1(uri1.toString())));
    }

    void validat(){
        validation.addValidation(binding.bankAccountName.getEditText(), RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
        validation.addValidation(binding.bankAccountNum.getEditText(),".*[0-9]{16}",getString(R.string.text_empt));
    }
}