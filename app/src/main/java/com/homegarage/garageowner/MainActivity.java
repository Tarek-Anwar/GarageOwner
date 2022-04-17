package com.homegarage.garageowner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.homegarage.garageowner.Sign.SignActivity;
import com.homegarage.garageowner.Sign.SignUp2Fragment;
import com.homegarage.garageowner.databinding.ActivityMainBinding;
import com.homegarage.garageowner.home.EditUserInfoActivity;
import com.homegarage.garageowner.model.InfoUserGarageModel;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView name , balance;
    private ImageView img_profile;

    private ActivityMainBinding binding;
    private FirebaseUser user;
    private ArrayList <InfoUserGarageModel> userGarageInfo;
    FragmentContainerView containerView;
    FragmentContainerView view;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        containerView=(FragmentContainerView)findViewById(R.id.fragmentContainerView2);
        view=(FragmentContainerView)findViewById(R.id.fragmentContainerView);

        FirebaseUtil.openFbReference("GaragerOnwerInfo", "Operation");
        user = FirebaseUtil.mFirebaseAuthl.getCurrentUser();
        userGarageInfo = FirebaseUtil.userGarageInfo;
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.main_nave_view);
        //find header Navigation
        v = navigationView.getHeaderView(0);
        intiHeader(v);

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
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());

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

    void setHeaderNav(InfoUserGarageModel model){
        if (model != null) {
            name.setText(model.getNameEn());
            balance.setText("Balance "+model.getBalance()+" EG");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
    }

    private void checkLogin(){
        if (user == null) {
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
                    if (snapshot.exists()) {
                        containerView.setVisibility(View.VISIBLE);
                        InfoUserGarageModel model = snapshot.getValue(InfoUserGarageModel.class);
                        userGarageInfo.add(model);
                        setHeaderNav(model);
                    }
                    else
                    {
                        view.setVisibility(View.VISIBLE);
                        v.setVisibility(View.GONE);

                        SignUp2Fragment newFragment = new SignUp2Fragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainerView, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        }
    }


}