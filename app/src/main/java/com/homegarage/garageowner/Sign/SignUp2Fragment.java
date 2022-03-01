package com.homegarage.garageowner.Sign;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.homegarage.garageowner.FirebaseUtil;
import com.homegarage.garageowner.R;
import com.homegarage.garageowner.model.InfoUserGarageModel;

import java.util.ArrayList;
import java.util.Locale;


public class SignUp2Fragment extends Fragment {

    private EditText restOfAddressEn , restOfAddressAr , location ;
    private Button next , getlocation , open_gps , btnDone ;
    private Spinner goverSpinner , citySpinner;
    AwesomeValidation mAwesomeValidation;
    ScrollView mScrollView;
    private LinearLayout mViewSuccess;

    InfoUserGarageModel infoUserGarageModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int locationRequestCode = 1;
    private double longitude , latitude;

    DatabaseReference spinnerRef;
    ArrayList<String> spinnerListGoverEn , spinnerListGoverAr, spinnerListCityEn , spinnerListCityAr;
    ArrayAdapter<String> adapterGover , adapterCity;
    String getCityAr ,  getCityEn, getGoverEn ,getGoverAr ;
    public SignUp2Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getContext());
        infoUserGarageModel = FirebaseUtil.userGarageModel;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        spinnerListGoverEn = new ArrayList<>();
        spinnerListGoverAr = new ArrayList<>();

        if(Locale.getDefault().getLanguage()=="en"){
        adapterGover = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerListGoverEn);}
        else { adapterGover = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerListGoverAr);}

        spinnerListCityEn = new ArrayList<>();
        spinnerListCityAr = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_sign_up2, container, false);
        intiUI(root);

        LocationManager manager2 = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean locationEnable2 = manager2.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(locationEnable2){
            open_gps.setVisibility(View.GONE);
        }else{ open_gps.setVisibility(View.VISIBLE); }

        open_gps.setOnClickListener(v -> {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean locationEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(!locationEnable){
                open_gps.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "please, opne Gps to get Location", Toast.LENGTH_SHORT).show();
                enableLoaction();
            }else{
                open_gps.setVisibility(View.GONE);
                Toast.makeText(getContext(), "GPS is opne", Toast.LENGTH_SHORT).show();
            }
        });

        getlocation.setOnClickListener(v -> getCurrantLoaction());

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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoUserGarageModel.setLocation(longitude+","+latitude);
                infoUserGarageModel.setCityAr(getCityAr);
                infoUserGarageModel.setCityEn(getCityEn);
                infoUserGarageModel.setGovernoateAR(getGoverAr);
                infoUserGarageModel.setGovernoateEn(getGoverEn);
                infoUserGarageModel.setRestOfAddressEN(restOfAddressEn.getText().toString());
                infoUserGarageModel.setRestOfAddressAr(restOfAddressAr.getText().toString());

                SignUp3Fragment newFragment = new SignUp3Fragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerView, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        showDataGover();

        goverSpinner.setAdapter(adapterGover);

        if(spinnerListGoverEn!=null){
            goverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(Locale.getDefault().getLanguage()=="en"){
                    adapterCity = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerListCityEn);}
                    else { adapterCity = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerListCityAr);}
                    citySpinner.setAdapter(adapterCity);
                    showDataCity(position);
                    getGoverEn = spinnerListGoverEn.get(position);
                    getGoverAr = spinnerListGoverAr.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        if(spinnerListCityEn!=null){
            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getCityEn = spinnerListCityEn.get(position);
                    getCityAr = spinnerListCityAr.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return root;
    }

    void intiUI(View view){
        getlocation = view.findViewById(R.id.btn_getlocation_sign);
        goverSpinner = view.findViewById(R.id.governoateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        restOfAddressEn = view.findViewById(R.id.restOfAddress_sign_en);
        restOfAddressAr = view.findViewById(R.id.restOfAddress_sign_ar);
        location = view.findViewById(R.id.location_sign);
        mScrollView = view.findViewById(R.id.scroll_view_2);
        mViewSuccess = view.findViewById(R.id.container_success_2);
        open_gps = view.findViewById(R.id.btn_opne_gps);
        btnDone = view.findViewById(R.id.check_sign2);
        next = view.findViewById(R.id.next_sign2);
    }

   /* private void setValidationButtons(View v) {
        Button btnDone = v.findViewById(R.id.check_sign2);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    sign.setVisibility(View.VISIBLE);
                    mViewSuccess.setVisibility(View.VISIBLE);
                } else {
                    mViewSuccess.setVisibility(View.GONE);
                }
            }
        });
    }*/

    private void addValidationForEditText() {
        mAwesomeValidation.addValidation(restOfAddressEn, RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
        mAwesomeValidation.addValidation(restOfAddressAr, RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
        mAwesomeValidation.addValidation(location, RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
    }

    private void getCurrantLoaction() {

        LocationRequest locationRequest =  LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(getActivity(), permission, locationRequestCode);
        } else {

            LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if(locationResult != null && locationResult.getLocations().size()>0){
                        int indx = locationResult.getLocations().size()-1;
                        longitude = locationResult.getLocations().get(indx).getLongitude();
                        latitude =    locationResult.getLocations().get(indx).getLatitude();
                        String data = "Longitude : "+ longitude + "Latitude : " + latitude;
                        location.setText(data);
                        location.setEnabled(false);
                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                    }

                }
            }, Looper.getMainLooper());
        }

    }

    private void enableLoaction(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case locationRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrantLoaction();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showDataGover(){
        spinnerRef = FirebaseDatabase.getInstance().getReference("Governorate");
        spinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        spinnerListGoverEn.add(item.child("governorate_name_en").getValue(String.class));
                        spinnerListGoverAr.add(item.child("governorate_name_ar").getValue(String.class));
                    }
                    adapterGover.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataCity(int pos){
        pos++;
        spinnerRef = FirebaseDatabase.getInstance().getReference("cities");
        Query query =  spinnerRef.orderByChild("governorate_id").equalTo(pos+"");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    spinnerListCityEn.clear();
                    spinnerListCityAr.clear();
                    for (DataSnapshot item : snapshot.getChildren()){
                        spinnerListCityEn.add(item.child("city_name_en").getValue(String.class));
                        spinnerListCityAr.add(item.child("city_name_ar").getValue(String.class));
                    }
                    adapterCity.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
