package com.example.bankinglearning.viewModels;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.utils.CredentialsValidator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;


public class LoginViewModel extends ViewModel {
    public static int RC_SIGN_IN = 1;
    private MutableLiveData<Map<String, Boolean>> validations = new MutableLiveData<Map<String, Boolean>>(new HashMap<String, Boolean>());

    public MutableLiveData<Map<String, Boolean>> getValidations() {
        return validations;
    }

    private CredentialsValidator credentialsValidator = new CredentialsValidator();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;


    public void emailLogIn(String email, String pass, Activity activity) {
        final Map<String, Boolean> validationsValue = validations.getValue();
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailValidation), credentialsValidator.isEmailValid(email));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.passwordValidation), credentialsValidator.isPasswordValid(pass));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailPattern), credentialsValidator.isEmailPattern(email));
        if ((validations.getValue().get(ApplicationClass.getInstance().getString(R.string.emailValidation))) && (validations.getValue().get(ApplicationClass.getInstance().getString(R.string.passwordValidation))) &&
                (validations.getValue().get(ApplicationClass.getInstance().getString(R.string.emailPattern)))) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.loginStatus), false);
                    } else {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.loginStatus), true);
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailConfirmed), true);
                        } else {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailConfirmed), false);
                        }
                    }
                    validations.setValue(validationsValue);
                }
            });
        }
    }


    public void googleSignIn(Activity activity) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void createRequest(Activity activity) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
    }


    public void handleSignInResult(Task<GoogleSignInAccount> completedTask, Activity activity) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            firebaseGoogleAuth(acc, activity);
        } catch (ApiException e) {
            firebaseGoogleAuth(null, activity);
        }
    }

    private void firebaseGoogleAuth(GoogleSignInAccount acc, final Activity activity) {
        if (acc != null) {
            final Map<String, Boolean> validationsValue = validations.getValue();
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.googleTask), true);
                    } else {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.googleTask), false);
                    }
                    validations.setValue(validationsValue);
                }
            });
        } else {
            Log.e("GLogin", "Google log in failed");
        }
    }


}
