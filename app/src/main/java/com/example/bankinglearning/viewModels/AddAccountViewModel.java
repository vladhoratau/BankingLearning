package com.example.bankinglearning.viewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAccountViewModel extends ViewModel {
    private MutableLiveData<Map<String, Boolean>> validations = new MutableLiveData<Map<String, Boolean>>(new HashMap<String, Boolean>());
    private final Map<String, Boolean> validationsValue = validations.getValue();
    private MutableLiveData<List<String>> accountTypeList = new MutableLiveData<>();

    public MutableLiveData<Map<String, Boolean>> getValidations() {
        addAccountType();
        return validations;
    }

    public MutableLiveData<List<String>> getAccountTypeList() {
        return accountTypeList;
    }

    public void addAccount(final Firebase accountsRoot, final String accountName, final int selectedPosition, final String accountType) {
        try {
            accountsRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(accountName)) {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.sameNameAcc), true);
                        validationsValue.remove(ApplicationClass.getInstance().getString(R.string.openFirstActivity));
                        validations.setValue(validationsValue);
                    } else if (selectedPosition == -1) {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.selected_position), false);
                        validationsValue.remove(ApplicationClass.getInstance().getString(R.string.openFirstActivity));
                        validations.setValue(validationsValue);
                    } else {
                        accountsRoot.child(accountName).child("AccountBalance").setValue(0);
                        accountsRoot.child(accountName).child("AccountType").setValue(accountType);

                        Log.e("bool",
                                String.valueOf(accountsRoot.child(accountName) != null));

                        if (accountsRoot.child(accountName) != null) {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.openFirstActivity), true);
                        } else {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.openFirstActivity), false);
                        }
                        validations.setValue(validationsValue);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.openFirstActivity), false);
                    validations.setValue(validationsValue);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAccountType() {
        List<String> accountTypeListValues = new ArrayList<>();
        accountTypeListValues.add("Credit");
        accountTypeListValues.add("Debit");
        accountTypeListValues.add("Ticket");
        accountTypeList.setValue(accountTypeListValues);
    }
}
