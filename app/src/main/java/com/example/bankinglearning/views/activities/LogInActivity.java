package com.example.bankinglearning.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.utils.Biometrics;
import com.example.bankinglearning.utils.SharedPref;
import com.example.bankinglearning.viewModels.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;
import static com.example.bankinglearning.viewModels.LoginViewModel.RC_SIGN_IN;
import static com.example.bankinglearning.views.activities.FirstActivity.logOutPREFS;
import static com.example.bankinglearning.views.activities.TouchActivity.switchButtonPREFS;


public class LogInActivity extends BaseActivity {

    private EditText emailTextview, passTextview;
    private Button logInButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SignInButton googleSignInButton;
    private TextView goToRegister;
    private ImageView finger;
    private Biometrics biometrics;
    private LoginViewModel loginViewModel;
    private SharedPref sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailTextview = findViewById(R.id.emailTextview);
        passTextview = findViewById(R.id.passTextview);
        logInButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleLogin);
        goToRegister = findViewById(R.id.goToRegister);
        finger = findViewById(R.id.finger);
        firebaseAuth = FirebaseAuth.getInstance();
        biometrics = new Biometrics();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sharedPreferences = new SharedPref();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        loginViewModel.createRequest(LogInActivity.this);

        loginViewModel.getValidations().observe(LogInActivity.this, new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> validations) {
                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.emailValidation))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.emailValidation))) {
                    emailTextview.setError("Email field is empty");
                    emailTextview.requestFocus();
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.passwordValidation))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.passwordValidation))) {
                    passTextview.setError("Password field is empty");
                    passTextview.requestFocus();
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.emailPattern))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.emailPattern))) {
                    showMessage("Email format is invalid");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.loginStatus))) {
                    if (validations.get(ApplicationClass.getInstance().getString(R.string.loginStatus))) {
                        if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.emailConfirmed))
                                && validations.get(ApplicationClass.getInstance().getString(R.string.emailConfirmed))) {
                            goToProfile();
                            sharedPreferences.putSharedPref(logOutPREFS, MODE_PRIVATE);
                        } else {
                            showMessage("Please confirm your email!");
                        }
                    } else {
                        showMessage("Login error.Please try again!");
                    }
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.googleTask))) {
                    if (validations.get(ApplicationClass.getInstance().getString(R.string.googleTask))) {
                        goToProfile();
                        sharedPreferences.putSharedPref(logOutPREFS, MODE_PRIVATE);
                    } else {
                        showMessage("Google log In failed!");
                    }
                }
            }
        });


        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.emailLogIn(emailTextview.getText().toString(), passTextview.getText().toString(), LogInActivity.this);
            }
        });


        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.googleSignIn(LogInActivity.this);
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

        if (!sharedPreferences.getSharedPref(switchButtonPREFS, MODE_PRIVATE) || !sharedPreferences.getSharedPref(logOutPREFS, MODE_PRIVATE)) {
            finger.setVisibility(View.INVISIBLE);
        } else {
            finger.setVisibility(View.VISIBLE);
            biometrics.biometricsLogin(LogInActivity.this);
        }


        finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometrics.biometricsLogin(LogInActivity.this);
            }
        });

    }

    private void goToProfile() {
        Intent goToProfile = new Intent(LogInActivity.this, FirstActivity.class);
        startActivity(goToProfile);
    }


    private void goToRegister() {
        Intent goToRegister = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(goToRegister);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finger.setVisibility(View.INVISIBLE);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loginViewModel.handleSignInResult(task, LogInActivity.this);
        }
    }


}


