package com.example.bankinglearning.viewModels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import static com.example.bankinglearning.viewModels.ClientAccountViewModel.FirebaseRoot;

public class FirstActivityViewModel extends ViewModel {
    private MutableLiveData<String> userNameLiveData = new MutableLiveData<>();
    private MutableLiveData<String> userEmailLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> userPhotoLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<String, Boolean>> validations = new MutableLiveData<Map<String, Boolean>>(new HashMap<String, Boolean>());

    public FirstActivityViewModel() {
        setValues();
    }

    public MutableLiveData<String> getUserNameLiveData() {
        return userNameLiveData;
    }

    public MutableLiveData<String> getUserEmailLiveData() {
        return userEmailLiveData;
    }

    public MutableLiveData<Uri> getUserPhotoLiveData() {
        return userPhotoLiveData;
    }


    public MutableLiveData<Map<String, Boolean>> getValidations() {
        return validations;
    }

    private void setValues() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(ApplicationClass.getInstance());
        if (googleSignInAccount != null) {
            userPhotoLiveData.setValue(googleSignInAccount.getPhotoUrl());
            userEmailLiveData.setValue(googleSignInAccount.getEmail());
            userNameLiveData.setValue(googleSignInAccount.getDisplayName());
        }

        if (userNameLiveData.getValue() == null) {
            Firebase.setAndroidContext(ApplicationClass.getInstance());
            Firebase accountsRoot = new Firebase(FirebaseRoot);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final String UID = firebaseAuth.getCurrentUser().getUid();
            accountsRoot.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userNameLiveData.setValue(String.valueOf(dataSnapshot.child(UID).child("Client Name").getValue()));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }


    public void signOut() {
        final Map<String, Boolean> validationsValue = validations.getValue();
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.firebaseSignOut), false);
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.googleSignOut), false);
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.logOutPREFS), true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
            Log.e("logout", "Successfully logOut");
            validationsValue.put(ApplicationClass.getInstance().getString(R.string.firebaseSignOut), true);
            validations.setValue(validationsValue);
        }

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(ApplicationClass.getInstance());
        if (googleSignInAccount != null) {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(ApplicationClass.getInstance(), googleSignInOptions);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.googleSignOut), true);
                    validations.setValue(validationsValue);
                }
            });
        }
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.logOutPREFS), false);
    }
}
