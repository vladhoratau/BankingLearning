package com.example.bankinglearning.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.bankinglearning.R;
import com.example.bankinglearning.viewModels.FirstActivityViewModel;
import com.example.bankinglearning.views.fragments.ClientAccountFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class FirstActivity extends BaseActivity {
    private DrawerLayout drawer;
    public static final String logOutPREFS = "logOut";
    private FirstActivityViewModel firstActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        firstActivityViewModel = new ViewModelProvider(this).get(FirstActivityViewModel.class);
        final View headerView = navigationView.getHeaderView(0);
        final ClientAccountFragment clientAccountFragment;
        clientAccountFragment = new ClientAccountFragment();
        addFragment(R.id.container, clientAccountFragment, "clientAccountFragment");
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logOut: {
                        firstActivityViewModel.signOut();
                        break;
                    }
                    case R.id.nav_transferMoney: {
                        Intent transferMoneyIntent = new Intent(getApplicationContext(), TransferMoneyActivity.class);
                        startActivity(transferMoneyIntent);
                        break;
                    }
                    case R.id.nav_addAccount: {
                        Intent addAccountIntent = new Intent(getApplicationContext(), AddAccountActivity.class);
                        startActivity(addAccountIntent);
                        break;
                    }
                    case R.id.nav_fingerprint: {
                        Intent intent = new Intent(getApplicationContext(), TouchActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
                return true;
            }
        });

        firstActivityViewModel.getValidations().observe(FirstActivity.this, new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> validations) {
                if (validations.containsKey("firebaseSignOut") && validations.get("firebaseSignOut")) {
                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                    startActivity(intent);
                }

                if (validations.containsKey("googleSignOut") && validations.get("googleSignOut")) {
                    finish();
                }

                if (validations.containsKey("logOutPREFS") && !validations.get("logOutPREFS")) {
                    final SharedPreferences.Editor editor = getSharedPreferences(logOutPREFS, MODE_PRIVATE).edit();
                    editor.putBoolean(logOutPREFS, false);
                    editor.apply();
                }
            }
        });


        firstActivityViewModel.getUserEmailLiveData().observe(FirstActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String userEmail) {
                TextView userHeaderEmail = headerView.findViewById(R.id.headerEmail);
                userHeaderEmail.setText(userEmail);
            }
        });

        firstActivityViewModel.getUserNameLiveData().observe(FirstActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String userName) {
                TextView headerUserName = headerView.findViewById(R.id.headerUser);
                headerUserName.setText(userName);
            }
        });

        firstActivityViewModel.getUserPhotoLiveData().observe(FirstActivity.this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                CircleImageView headerUserPic = headerView.findViewById(R.id.userIcon);
                Glide.with(getApplicationContext()).load(String.valueOf(uri)).into(headerUserPic);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}