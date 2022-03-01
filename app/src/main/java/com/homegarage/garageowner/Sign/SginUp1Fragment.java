package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.InfoUserGarageModel;


public class SginUp1Fragment extends Fragment {

    private EditText nameEn , nameAr , phone , email , password , cpassword;
    private Button next , btnDone;
    private AwesomeValidation  mAwesomeValidation;
    private ScrollView mScrollView;
    private LinearLayout mViewSuccess;
    InfoUserGarageModel infoUserGarageModel;

    public SginUp1Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getContext());
        infoUserGarageModel = FirebaseUtil.userGarageModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sgin_up, container, false);
        intiUI(view);

        btnDone.setOnClickListener(v -> {
            addValidationForEditText();
            if (mAwesomeValidation.validate()) {
                mScrollView.fullScroll(View.FOCUS_DOWN);
                next.setVisibility(View.VISIBLE);
                mViewSuccess.setVisibility(View.VISIBLE);
            } else {
                mViewSuccess.setVisibility(View.GONE);
            }
        });

        next.setOnClickListener(v -> {

            infoUserGarageModel.setEmail(email.getText().toString().trim());
            infoUserGarageModel.setNameEn(nameEn.getText().toString().trim());
            infoUserGarageModel.setPhone(phone.getText().toString());
            infoUserGarageModel.setPassword(password.getText().toString().trim());
            infoUserGarageModel.setNameAr(nameAr.getText().toString().trim());

            SignUp2Fragment newFragment = new SignUp2Fragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

           /* FirebaseAuth firebaseAuth = FirebaseUtil.mFirebaseAuthl;
               firebaseAuth.createUserWithEmailAndPassword(emailuser, passwoeduser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference databaseReference = FirebaseUtil.mDatabaseReference;
                                firebaseUser = task.getResult().getUser();
                                DatabaseReference newuser = databaseReference.child(firebaseUser.getUid());
                                newuser.child("Full Name").setValue(nameuser);
                                newuser.child("Phone").setValue(phoneuser);
                                newuser.child("password").setValue(passwoeduser);
                                newuser.child("email").setValue(emailuser);
                                Toast.makeText(getContext(), "Sussful sign", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                });*/
       });

        return view;
    }

    void intiUI(View view){
        nameEn = view.findViewById(R.id.et_nameEN_sign);
        nameAr = view.findViewById(R.id.et_nameAr_sign);
        phone = view.findViewById(R.id.et_phone_sign);
        password = view.findViewById(R.id.et_password_sign);
        email = view.findViewById(R.id.et_email_sign);
        next = view.findViewById(R.id.next_sign1);
        cpassword = view.findViewById(R.id.et_confirm_password);
        mScrollView = view.findViewById(R.id.scroll_view_1);
        mViewSuccess = view.findViewById(R.id.container_success_1);
        btnDone =  view.findViewById(R.id.check_sign1);
    }

   /* private void setValidationButtons(View v) {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    next.setVisibility(View.VISIBLE);
                    mViewSuccess.setVisibility(View.VISIBLE);
                } else {
                    mViewSuccess.setVisibility(View.GONE);
                }
            }
        });
    }*/

    private void addValidationForEditText() {
        mAwesomeValidation.addValidation(password, "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}",getString(R.string.invalid_password));
        mAwesomeValidation.addValidation(cpassword, password,getString(R.string.password_confirmation));
        mAwesomeValidation.addValidation(nameEn, "[a-zA-Z\\s]+",getString(R.string.name_invalid));
        mAwesomeValidation.addValidation(nameAr, RegexTemplate.NOT_EMPTY,getString(R.string.name_invalid));
        mAwesomeValidation.addValidation(email, Patterns.EMAIL_ADDRESS, getString(R.string.email_valid));
        mAwesomeValidation.addValidation(phone,"^01[0125][0-9]{8}$",getString(R.string.phone_invalid));
       // setValidationButtons(v);
    }
}