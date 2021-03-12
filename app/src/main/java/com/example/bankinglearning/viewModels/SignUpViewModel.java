package com.example.bankinglearning.viewModels;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.utils.CredentialsValidator;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.bankinglearning.viewModels.ClientAccountViewModel.FirebaseRoot;

public class SignUpViewModel extends ViewModel {

    private CredentialsValidator credentialsValidator = new CredentialsValidator();
    private MutableLiveData<Map<String, Boolean>> validations = new MutableLiveData<Map<String, Boolean>>(new HashMap<String, Boolean>());
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;

    public MutableLiveData<Map<String, Boolean>> getValidations() {
        return validations;
    }

    public void signUp(final String email, final String pass, final String name, final String bDate, final String cnp, final Activity activity) {

        final Map<String, Boolean> validationsValue = validations.getValue();
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailValidation), credentialsValidator.isEmailValid(email));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.passwordValidation), credentialsValidator.isPasswordValid(pass));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.nameValidation), credentialsValidator.isNameValid(name));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.dateValidation), credentialsValidator.isbDateValid(bDate));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.cnpValidation), credentialsValidator.iscnpValid(cnp));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailPattern), credentialsValidator.isEmailPattern(email));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.datePattern), credentialsValidator.isDateFormatValid(bDate));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), true);
        validations.setValue(validationsValue);

        if (validations.getValue().get(ApplicationClass.getInstance().getString(R.string.emailValidation))
                && validations.getValue().get(ApplicationClass.getInstance().getString(R.string.passwordValidation)) &&
                validations.getValue().get(ApplicationClass.getInstance().getString(R.string.nameValidation))
                && validations.getValue().get(ApplicationClass.getInstance().getString(R.string.dateValidation)) &&
                validations.getValue().get(ApplicationClass.getInstance().getString(R.string.cnpValidation))
                && validations.getValue().get(ApplicationClass.getInstance().getString(R.string.emailPattern)) &&
                validations.getValue().get(ApplicationClass.getInstance().getString(R.string.datePattern))) {

            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    boolean check = !task.getResult().getSignInMethods().isEmpty();
                    if (!check) {
                        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.isUserRegistered), false);
                                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
                                    validations.setValue(validationsValue);
                                } else {
                                    try {
                                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.isUserRegistered), true);
                                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
                                        insertNewUser(name, cnp, bDate);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.emailExists), true);
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
                        validations.setValue(validationsValue);
                    }
                }
            });
        } else {
            validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
            validations.setValue(validationsValue);
        }
    }


    private void insertNewUser(final String userName, final String userCNP,
                               final String userBdate) {
        Firebase.setAndroidContext(ApplicationClass.getInstance());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userUID = firebaseAuth.getCurrentUser().getUid();
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final Map<String, Boolean> validationsValue = validations.getValue();
                if (task.isSuccessful()) {
                    mDatabase.child("Clients").child(userUID).child("Client Birthdate").setValue(userBdate);
                    mDatabase.child("Clients").child(userUID).child("Client CNP").setValue(userCNP);
                    mDatabase.child("Clients").child(userUID).child("Client Name").setValue(userName);
                    mDatabase.child("Clients").child(userUID).child("Accounts").child("Account1").child("AccountBalance").setValue("0");
                    mDatabase.child("Clients").child(userUID).child("Accounts").child("Account1").child("AccountType").setValue("Debit");

                    final Firebase accountsRoot = new Firebase(FirebaseRoot);
                    accountsRoot.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userUID).exists()) {
                                Log.e("userUID", String.valueOf(dataSnapshot.child(userUID)));
                                Log.e("dataSnapshot", String.valueOf(dataSnapshot));
                                validationsValue.put(ApplicationClass.getInstance().getString(R.string.isUserRegistered), true);
                                validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
                                validations.setValue(validationsValue);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.isUserRegistered), false);
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
                            validations.setValue(validationsValue);
                        }
                    });
                } else {
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.isUserRegistered), false);
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.loadingVisibility), false);
                    validations.setValue(validationsValue);
                }
            }
        });
    }
}
