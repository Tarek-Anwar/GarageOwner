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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
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
import com.homegarage.garageowner.databinding.FragmentSignUp2Binding;
import com.homegarage.garageowner.model.InfoUserGarageModel;
import java.util.ArrayList;
import java.util.Locale;


public class SignUp2Fragment extends Fragment {

    FragmentSignUp2Binding binding;
    AwesomeValidation mAwesomeValidation;
    InfoUserGarageModel infoUserGarageModel;
    private final int locationRequestCode = 1;
    private double longitude , latitude;
    String allLocation = null;
    DatabaseReference spinnerRef;
    ArrayList<String> spinnerListGoverEn , spinnerListGoverAr, spinnerListCityEn , spinnerListCityAr;
    ArrayAdapter<String> adapterAutoGover , adapterAutoCity;
    String getCityAr , getCityEn, getGoverEn ,getGoverAr ;

    public SignUp2Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getContext());
        infoUserGarageModel = FirebaseUtil.userGarageModel;

        spinnerListGoverEn = new ArrayList<>();
        spinnerListGoverAr = new ArrayList<>();

        showDataGover();
        if(Locale.getDefault().getLanguage().equals("en")){
        adapterAutoGover = new ArrayAdapter<>(requireContext(),R.layout.list_item,spinnerListGoverEn); }
        else {
            adapterAutoGover = new ArrayAdapter<>(requireContext(),R.layout.list_item,spinnerListGoverAr); }
        spinnerListCityEn = new ArrayList<>();
        spinnerListCityAr = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignUp2Binding.inflate(getLayoutInflater());
        LocationManager manager2 = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean locationEnable2 = manager2.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!locationEnable2){ binding.btnOpneGps.setVisibility(View.VISIBLE); }
        else{ binding.btnOpneGps.setVisibility(View.GONE); }

        binding.btnOpneGps.setOnClickListener(v -> {
        LocationManager manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean locationEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(!locationEnable){
                binding.btnOpneGps.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "please, opne Gps to get Location", Toast.LENGTH_SHORT).show();
                enableLoaction();
            }else{
                binding.btnOpneGps.setVisibility(View.GONE);
                Toast.makeText(getContext(), "GPS is opne", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnGetlocationSign.setOnClickListener(v -> getCurrantLoaction());

        addValidationForEditText();

        binding.checkSign2.setOnClickListener(v -> {
            if (mAwesomeValidation.validate()) {
                binding.scrollSign2.fullScroll(View.FOCUS_DOWN);
                binding.nextSign2.setVisibility(View.VISIBLE);
                binding.layoutSuccess2.setVisibility(View.VISIBLE);
            } else {
                binding.layoutSuccess2.setVisibility(View.GONE);
            }
        });

        binding.nextSign2.setOnClickListener(v -> {
            infoUserGarageModel.setLocation(longitude+","+latitude);
            infoUserGarageModel.setCityAr(getCityAr);
            infoUserGarageModel.setCityEn(getCityEn);
            infoUserGarageModel.setGovernoateAR(getGoverAr);
            infoUserGarageModel.setGovernoateEn(getGoverEn);
            infoUserGarageModel.setRestOfAddressEN(binding.restOfAddressSignEn.getText().toString());
            infoUserGarageModel.setRestOfAddressAr(binding.restOfAddressSignAr.getText().toString());

            SignUp3Fragment newFragment = new SignUp3Fragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        binding.autoCompleteGover.setAdapter(adapterAutoGover);

        if(spinnerListGoverEn!=null){
        binding.autoCompleteGover.setOnItemClickListener((parent, view, position, id) -> {
            showDataCity(position);
           if(Locale.getDefault().getLanguage().equals("en")){
                adapterAutoCity = new ArrayAdapter<>(getContext(), R.layout.list_item,spinnerListCityEn); }
            else { adapterAutoCity = new ArrayAdapter<>(getContext(), R.layout.list_item,spinnerListCityAr);}
            binding.autoCompleteCity.setAdapter(adapterAutoCity);

            getGoverAr = spinnerListGoverAr.get(position);
            getGoverEn = spinnerListGoverEn.get(position);

        });
    }

        if(spinnerListCityEn!=null){
            binding.autoCompleteCity.setOnItemClickListener((parent, view, position, id) -> {
                getCityAr = spinnerListCityAr.get(position);
                getCityEn = spinnerListCityEn.get(position);
            });
        }

        return binding.getRoot();
    }

    private void addValidationForEditText() {
        mAwesomeValidation.addValidation(binding.restOfAddressSignAr, RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
        mAwesomeValidation.addValidation(binding.restOfAddressSignEn, RegexTemplate.NOT_EMPTY,getString(R.string.text_empt));
        mAwesomeValidation.addValidation(binding.location,RegexTemplate.NOT_EMPTY,getString(R.string.invalid_location));
    }

    private void getCurrantLoaction() {
        LocationRequest locationRequest =  LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(requireActivity(), permission, locationRequestCode);
        } else {
            LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if(locationResult.getLocations().size()>0){
                        int indx = locationResult.getLocations().size()-1;
                        longitude = locationResult.getLocations().get(indx).getLongitude();
                        latitude =    locationResult.getLocations().get(indx).getLatitude();
                        allLocation  =  longitude + "," + latitude;
                        binding.location.setText(allLocation);
                        Toast.makeText(getContext(), allLocation, Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrantLoaction();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
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
                    adapterAutoGover.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
                    adapterAutoCity.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }

}
