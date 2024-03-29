package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        launcher=registerForActivityResult(contract,uri->{
            if (uri!=null){
                binding.profileImage.setImageURI(uri);
                uploadImage(uri);
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

        binding.profileImage.setOnClickListener(v->launcher.launch(null));

        binding.btnSignInFire.setOnClickListener(v -> {

            FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
            firebaseAuth.createUserWithEmailAndPassword(model.getEmail(), binding.etPasswordSign.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DatabaseReference databaseReference = FirebaseUtil.mDatabaseReference;
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null;

                    DatabaseReference newuser = databaseReference.child(firebaseUser.getUid());
                    model.setPriceForHour(Float.parseFloat(binding.etPriceForHoure.getText().toString()));
                    model.setId(firebaseUser.getUid());
                    model.setRate(0.0f);
                    model.setNumOfRatings(0);
                    newuser.setValue(model);
                    Toast.makeText(getContext(), "Welcome", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });
        return  binding.getRoot();
    }

    private void addValidationForEditText() {
       mAwesomeValidation.addValidation(binding.etPasswordSign, "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}",getString(R.string.invalid_password));
       mAwesomeValidation.addValidation(binding.etConfirmPassword, binding.etPasswordSign,getString(R.string.password_confirmation));
       mAwesomeValidation.addValidation(binding.etPriceForHoure,RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
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