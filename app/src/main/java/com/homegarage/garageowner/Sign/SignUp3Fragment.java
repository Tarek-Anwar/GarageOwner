package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.MainActivity;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.databinding.FragmentSignUp3Binding;
import com.homegarage.garageowner.model.InfoUserGarageModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;
import java.util.UUID;

public class SignUp3Fragment extends Fragment {

    private InfoUserGarageModel model;
    private AwesomeValidation mAwesomeValidation;
    private FragmentSignUp3Binding binding;
    private ActivityResultLauncher<Object> launcher;

    public SignUp3Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = FirebaseUtil.userGarageSign;
        mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignUp3Binding.inflate(getLayoutInflater());

        ActivityResultContract<Object, Uri> contract = new ActivityResultContract<Object, Uri>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                return CropImage.activity()
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .getIntent(requireContext());
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

        launcher=registerForActivityResult(contract, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    binding.profileImage.setImageURI(uri);
                    SignUp3Fragment.this.uploadImage(uri);
                }
            }
        });

        addValidationForEditText();

        binding.checkSign3.setOnClickListener(v -> {
            if (mAwesomeValidation.validate()) {
                binding.btnSignInFire.setVisibility(View.VISIBLE);
                binding.checkSign3.setVisibility(View.GONE);
            } else {
                binding.btnSignInFire.setVisibility(View.GONE);
                binding.checkSign3.setVisibility(View.VISIBLE);
            }
        });

        binding.profileImage.setOnClickListener(v-> {
            launcher.launch(null);
        });

        binding.btnSignInFire.setOnClickListener(v -> {
                    DatabaseReference databaseReference = FirebaseUtil.mDatabaseReference;
                    FirebaseUser firebaseUser =FirebaseUtil.mFirebaseAuthl.getCurrentUser();
                    assert firebaseUser != null;
                    DatabaseReference newuser = databaseReference.child(firebaseUser.getUid());
                    model.setPriceForHour(Float.parseFloat(binding.etPriceForHoure.getText().toString()));
                    model.setId(firebaseUser.getUid());
                    model.setRate(0f);
                    model.setNumOfRatings(0);
                    newuser.setValue(model);
                    Sign_up4 signUp4=new Sign_up4(firebaseUser);
                    FragmentTransaction transaction=requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainerView,signUp4);
                    transaction.addToBackStack(null);
                    transaction.commit();

        });
        return  binding.getRoot();
    }

    private void addValidationForEditText() {
       mAwesomeValidation.addValidation(binding.etPriceForHoure,RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
       mAwesomeValidation.addValidation(binding.etPhoneSign,"^01[0125][0-9]{8}$",getString(R.string.phone_invalid));

    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = FirebaseUtil.mStorageReference.child(UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> model.setImageGarage(uri.toString()));
                Toast.makeText(requireContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show(); }).addOnFailureListener(e -> { progressDialog.dismiss();
                Toast.makeText(requireContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%"); });
        }
    }

}