package com.example.bankinglearning.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.views.activities.FirstActivity;

import java.util.concurrent.Executor;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;

public class Biometrics {
    private BiometricManager biometricManager;
    private boolean loginSuccess = false;

    public void biometricsLogin(final Activity sourceActivity) {
        biometricManager = BiometricManager.from(ApplicationClass.getInstance());
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS: {
                showMessage("You can use your fingerprint sensor to login");
            }
            break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showMessage("The device doesn't have the specific hardware");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showMessage("The sensor is unavailable");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                showMessage("No fingerprint enrolled");
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(ApplicationClass.getInstance());
        final BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) sourceActivity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(ApplicationClass.getInstance(), "Login Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(sourceActivity, FirstActivity.class); //abstractizare
                sourceActivity.startActivity(intent);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                loginSuccess = false;
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(ApplicationClass.getInstance().getString(R.string.biometrics_title))
                .setDescription(ApplicationClass.getInstance().getString(R.string.biometrics_description))
                .setNegativeButtonText(ApplicationClass.getInstance().getString(R.string.biometrics_closeButton)).build();
        biometricPrompt.authenticate(promptInfo);

    }

}
