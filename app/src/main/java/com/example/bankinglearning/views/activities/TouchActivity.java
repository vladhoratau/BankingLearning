package com.example.bankinglearning.views.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bankinglearning.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;

public class TouchActivity extends BaseActivity {
    private Switch biometricsLoginButton;
    public static final String switchButtonPREFS = "switchButton";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        biometricsLoginButton = findViewById(R.id.biometricsLoginButton);

        final SharedPreferences.Editor editor = getSharedPreferences(switchButtonPREFS, MODE_PRIVATE).edit();
        SharedPreferences sharedPreferences = getSharedPreferences(switchButtonPREFS, MODE_PRIVATE);
        boolean defaultVal = sharedPreferences.getBoolean(switchButtonPREFS, false);
        biometricsLoginButton.setChecked(defaultVal);
        Log.e("biometrics", String.valueOf(biometricsLoginButton.isChecked()));
        biometricsLoginButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (biometricsLoginButton.isChecked()) {
                    Log.e("biometrics", String.valueOf(biometricsLoginButton.isChecked()));
                    editor.putBoolean(switchButtonPREFS, true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Biometrics login activated!", Toast.LENGTH_SHORT).show();
                } else if (!biometricsLoginButton.isChecked()) {
                    editor.putBoolean(switchButtonPREFS, false);
                    editor.apply();
                    Log.e("biometrics", String.valueOf(biometricsLoginButton.isChecked()));
                    showMessage("Biometrics login desactivated!");
                }
            }
        });



    }


}
