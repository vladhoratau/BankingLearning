package com.example.bankinglearning.views.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.utils.DateUtil;
import com.example.bankinglearning.viewModels.SignUpViewModel;
import com.example.bankinglearning.views.fragments.DatePickerFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;

public class RegisterActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private EditText emailTextview, passTextview, nameTextView, birthDateTextView, cnpTextView;
    private Button registerButton;
    private TextView goToLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressBar loading;
    private SignUpViewModel signUpViewModel;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailTextview = findViewById(R.id.emailTextview);
        passTextview = findViewById(R.id.passTextview);
        registerButton = findViewById(R.id.registerButton);
        goToLogin = findViewById(R.id.goToLogin);
        nameTextView = findViewById(R.id.userName);
        birthDateTextView = findViewById(R.id.userBdate);
        cnpTextView = findViewById(R.id.userCNP);
        loading = findViewById(R.id.loading);
        firebaseAuth = FirebaseAuth.getInstance();
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        signUpViewModel.getValidations().observe(RegisterActivity.this, new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> validations) {
                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.emailValidation))
                        && !validations.get((ApplicationClass.getInstance().getString(R.string.emailValidation)))) {
                    emailTextview.setError("Email field is empty.");
                    emailTextview.requestFocus();
                }

                if (validations.containsKey((ApplicationClass.getInstance().getString(R.string.passwordValidation)))
                        && !validations.get((ApplicationClass.getInstance().getString(R.string.passwordValidation)))) {
                    passTextview.setError("Password field is empty.");
                    passTextview.requestFocus();
                }

                if (validations.containsKey((ApplicationClass.getInstance().getString(R.string.nameValidation)))
                        && !validations.get((ApplicationClass.getInstance().getString(R.string.nameValidation)))) {
                    nameTextView.setError("Name field is empty.");
                    nameTextView.requestFocus();
                }

                if (validations.containsKey((ApplicationClass.getInstance().getString(R.string.dateValidation)))
                        && !validations.get((ApplicationClass.getInstance().getString(R.string.dateValidation)))) {
                    birthDateTextView.setError("Date field is empty.");
                    birthDateTextView.requestFocus();
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.cnpValidation))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.cnpValidation))) {
                    cnpTextView.setError("CNP field is empty.");
                    cnpTextView.requestFocus();
                }


                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.emailPattern))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.emailPattern))) {
                    showMessage("Invalid email adress");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.emailExists))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.emailExists))) {
                    showMessage("Email already exists! Please register with another");
                }


                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.isUserRegistered))) {
                    if (validations.get(ApplicationClass.getInstance().getString(R.string.isUserRegistered))) {
                        showMessage("Registered successfully. Please confirm your email.");
                        finish();
                    } else {
                        showMessage("Registered unsuccessfully");
                    }
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.loadingVisibility))) {
                    if (validations.get(ApplicationClass.getInstance().getString(R.string.loadingVisibility))) {
                        loading.setVisibility(View.VISIBLE);
                    } else {
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpViewModel.signUp(emailTextview.getText().toString(), passTextview.getText().toString(),
                        nameTextView.getText().toString(), birthDateTextView.getText().toString(),
                        cnpTextView.getText().toString(), RegisterActivity.this);
                loadingBarInit();
            }
        });

        birthDateTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }
            }
        });

        birthDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });
    }

    private void loadingBarInit() {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter++;
                loading.setProgress(counter);
                if (counter == 100)
                    timer.cancel();
            }
        };
        timer.schedule(timerTask, 0, 100);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String date = DateFormat.getDateInstance().format(calendar.getTime());
        birthDateTextView.setText(DateUtil.convertDateFormat(date));
    }
}

