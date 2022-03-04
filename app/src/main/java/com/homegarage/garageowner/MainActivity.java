package com.homegarage.garageowner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.homegarage.garageowner.Sign.SignActivity;
import com.homegarage.garageowner.Sign.SignUp3Fragment;
import com.homegarage.garageowner.databinding.ActivityMainBinding;
import com.homegarage.garageowner.home.EditUserInfoActivity;
import com.homegarage.garageowner.model.InfoUserGarageModel;
import com.homegarage.garageowner.service.FcmNotificationsSender;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView name , balance;
    ImageView img_profile;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor;

    ActivityMainBinding binding;

    public static final String TAG = "TAG556";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUtil.openFbReference("GaragerOnwerInfo");

        binding = ActivityMainBinding.inflate(getLayoutInflater());

       // FirebaseMessaging.getInstance().subscribeToTopic("all");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        Button sendAll = findViewById(R.id.btn_send_all);
        EditText title = findViewById(R.id.title_not);
        EditText body = findViewById(R.id.body_not);
        sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!title.getText().toString().isEmpty()
                        && !body.getText().toString().isEmpty()){

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(

                            "/topics/all" ,title.getText().toString()
                            ,body.getText().toString() , getApplicationContext(),MainActivity.this);

                    notificationsSender.SendNotifications();
                }else {
                    Toast.makeText(getApplicationContext(), "Write some text", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_send_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

         preferences = getSharedPreferences(getString(R.string.file_user_info),Context.MODE_PRIVATE);
         editor = preferences.edit();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.main_nave_view);
        //find header Navigation
        View v = navigationView.getHeaderView(0);

        intiHeader(v);

        preferences = getSharedPreferences(getString(R.string.file_user_info),Context.MODE_PRIVATE);

        //set usr information if their
        img_profile.setOnClickListener( V->{
            Intent intent =  new Intent(this , EditUserInfoActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // set action Bar to Navigation
        actionBarDrawerToggle = new ActionBarDrawerToggle(this ,drawerLayout,R.string.open_menu,R.string.close_menu);
        actionBarDrawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // set listener to item in navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.log_out_nav) {
                FirebaseUtil.mFirebaseAuthl.signOut();

                editor.clear();

                drawerLayout.closeDrawer(GravityCompat.START);
                Toast.makeText(getApplicationContext(), "Log Out .... GoodBye ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SignActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
            return true;
        });

        checkLogin();
    }

    void intiHeader(View v){
        name = v.findViewById(R.id.user_name_nav);
        balance = v.findViewById(R.id.user_balance);
        img_profile = v.findViewById(R.id.img_profile);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setHeaderNav(SharedPreferences preferences){
        if (preferences.getString(SignUp3Fragment.NAME_EN,null)!= null) {
            name.setText(preferences.getString(SignUp3Fragment.NAME_EN, "New User"));
            balance.setText(preferences.getFloat(SignUp3Fragment.PRICE,0.0f)+"");
        }
        if(preferences.getString(SignUp3Fragment.IMAGE_PROFILE,null)!= null){
            img_profile.setImageURI(Uri.parse((preferences.getString(SignUp3Fragment.IMAGE_PROFILE,null))));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    private void checkLogin(){
        FirebaseUser user = FirebaseUtil.mFirebaseAuthl.getCurrentUser();
        if (user == null) {
            Log.i("dsfsdfsdf","not user sign");
            Intent intent = new Intent(this, SignActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else {
            DatabaseReference ref = FirebaseUtil.mFirebaseDatabase.getReference("GaragerOnwerInfo").child(user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    InfoUserGarageModel model = snapshot.getValue(InfoUserGarageModel.class);
                    editor.putString(SignUp3Fragment.NAME_AR, model.getNameAr());
                    editor.putString(SignUp3Fragment.NAME_EN, model.getNameEn());
                    editor.putString(SignUp3Fragment.EMAIL, model.getEmail());
                    editor.putString(SignUp3Fragment.PHONE, model.getPhone());
                    editor.putString(SignUp3Fragment.GOVER_EN, model.getGovernoateEn());
                    editor.putString(SignUp3Fragment.GOVER_Ar, model.getGovernoateAR());
                    editor.putString(SignUp3Fragment.CITY_AR, model.getCityAr());
                    editor.putString(SignUp3Fragment.CITY_EN, model.getCityEn());
                    editor.putFloat(SignUp3Fragment.PRICE, model.getPriceForHour());
                    editor.putString(SignUp3Fragment.LOCATION, model.getLocation());
                    editor.putString(SignUp3Fragment.STREET_AR, model.getRestOfAddressAr());
                    editor.putString(SignUp3Fragment.STREET_EN, model.getRestOfAddressEN());
                    editor.commit();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
          /*editor.putString(SignUp3Fragment.NAME_AR, model.getNameAr());
            editor.putString(SignUp3Fragment.NAME_EN, model.getNameEn());
            editor.putString(SignUp3Fragment.EMAIL, model.getEmail());
            editor.putString(SignUp3Fragment.PHONE, model.getPhone());
            editor.putString(SignUp3Fragment.GOVER_EN, model.getGovernoateEn());
            editor.putString(SignUp3Fragment.GOVER_Ar, model.getGovernoateAR());
            editor.putString(SignUp3Fragment.CITY_AR, model.getCityAr());
            editor.putString(SignUp3Fragment.CITY_EN, model.getCityEn());
            editor.putFloat(SignUp3Fragment.PRICE, model.getPriceForHour());
            editor.putString(SignUp3Fragment.LOCATION, model.getLocation());
            editor.putString(SignUp3Fragment.STREET_AR, model.getRestOfAddressAr());
            editor.putString(SignUp3Fragment.STREET_EN, model.getRestOfAddressEN());
            editor.commit();*/
            setHeaderNav(preferences);
            Log.i("dsfsdfsdf","user sign id "+ user.getUid());
        }
    }
}