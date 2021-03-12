package com.example.bankinglearning.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.models.ClientAccount.ClientAccount;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import static com.example.bankinglearning.viewModels.ClientAccountViewModel.FirebaseRoot;

public class TransferMoneyViewModel extends ViewModel {
    private MutableLiveData<Map<String, Boolean>> validations = new MutableLiveData<Map<String, Boolean>>(new HashMap<String, Boolean>());

    public MutableLiveData<Map<String, Boolean>> getValidations() {
        return validations;
    }

    public void transferMoneyToAccount(final Double amount, final String recipientAccount, final String recipientName, ClientAccount sender) {

        final Double senderOldBalance = sender.getAccount().getBalance();
        final String senderAccount = sender.getAccount().getAccountNo();
        final String senderAccountType = sender.getAccount().getAccountType();
        final Firebase accountsRoot = new Firebase(FirebaseRoot);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String UID = firebaseAuth.getCurrentUser().getUid();
        final Map<String, Boolean> validationsValue = validations.getValue();
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.ticketAccount), false);
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit), true);
        validationsValue.remove(ApplicationClass.getInstance().getString(R.string.validRecipientAcc));
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.sentMoney), false);
        validations.setValue(validationsValue);

        accountsRoot.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("Client Name").getValue().equals(recipientName)) {
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.validRecipientAcc), true);
                    if (dataSnapshot.child("Accounts").child(recipientAccount).exists()) {
                        String recipientAccountType = String.valueOf(dataSnapshot.child("Accounts").child(recipientAccount).child("AccountType").getValue());
                        if (recipientAccountType.equals("Ticket") || senderAccountType.equals("Ticket")) {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.ticketAccount), true);
                        } else if (senderAccountType.equals("Debit") && senderOldBalance < amount) {
                            validationsValue.put(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit), false);
                        } else {
                            try {
                                Double recipientOldBalance = Double.valueOf(String.valueOf(dataSnapshot.child("Accounts").child(recipientAccount).child("AccountBalance").getValue()));
                                String accountKey = dataSnapshot.getKey();
                                accountsRoot.child(accountKey).child("Accounts").child(recipientAccount).child("AccountBalance").setValue(amount + recipientOldBalance);
                                accountsRoot.child(UID).child("Accounts").child(senderAccount).child("AccountBalance").setValue(senderOldBalance - amount);
                                validationsValue.put(ApplicationClass.getInstance().getString(R.string.sentMoney), true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.validSenderAcc), false);
                    }
                }
                validations.setValue(validationsValue);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

}
