package com.example.bankinglearning.viewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.models.Account.CreditAccount;
import com.example.bankinglearning.models.Account.DebitAccount;
import com.example.bankinglearning.models.Account.TicketsAccount;
import com.example.bankinglearning.models.Client.Client;
import com.example.bankinglearning.models.ClientAccount.ClientAccount;
import com.example.bankinglearning.utils.DateUtil;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientAccountViewModel extends ViewModel {
    public static final String FirebaseRoot = "https://bankinglearning.firebaseio.com/Clients/";
    public MutableLiveData<List<ClientAccount>> clientAccountLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<String, Boolean>> validations = new MutableLiveData<Map<String, Boolean>>(new HashMap<String, Boolean>());
    private final Map<String, Boolean> validationsValue = validations.getValue();
    public static final int MAX_WITHDRAW_IN_RON = 10000;

    public ClientAccountViewModel() {
        loadClientAccountAttributes();
    }

    public MutableLiveData<List<ClientAccount>> getClientAccountAttributes() {
        return clientAccountLiveData;
    }

    public MutableLiveData<Map<String, Boolean>> getValidations() {
        return validations;
    }

    public void loadClientAccountAttributes() {

        final Firebase client1Root;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String UID = firebaseAuth.getCurrentUser().getUid();
        client1Root = new Firebase(FirebaseRoot + UID);
        client1Root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, String> ClientMap = dataSnapshot.getValue(Map.class);
                    String clientName = ClientMap.get("Client Name");
                    String clientCNP = ClientMap.get("Client CNP");
                    String clientBDate = ClientMap.get("Client Birthdate");
                    final Client client1 = new Client(clientName, clientCNP, DateUtil.getDateFromString(clientBDate));
                    final List<ClientAccount> clientAccounts = new ArrayList();
                    Firebase accountsRoot = new Firebase(client1Root.toString() + "/Accounts");
                    accountsRoot.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            try {
                                Double accountBalance = Double.valueOf(dataSnapshot.child("AccountBalance").getValue().toString());
                                String accountIBAN = dataSnapshot.getKey();
                                String accountType = dataSnapshot.child("AccountType").getValue().toString();
                                switch (accountType) {
                                    case "Credit": {
                                        CreditAccount newCreditAccount = new CreditAccount(accountIBAN, accountBalance, accountType);
                                        ClientAccount clientAccount = new ClientAccount(client1, newCreditAccount);
                                        clientAccounts.add(clientAccount);
                                        break;
                                    }

                                    case "Debit": {
                                        DebitAccount newDebitAccount = new DebitAccount(accountIBAN, accountBalance, accountType);
                                        ClientAccount clientAccount = new ClientAccount(client1, newDebitAccount);
                                        clientAccounts.add(clientAccount);
                                        break;
                                    }

                                    case "Ticket": {
                                        TicketsAccount newTicketAccount = new TicketsAccount(accountIBAN, accountBalance, accountType);
                                        ClientAccount clientAccount = new ClientAccount(client1, newTicketAccount);
                                        clientAccounts.add(clientAccount);
                                        break;
                                    }
                                }
                                clientAccountLiveData.setValue(clientAccounts);
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                            }
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Error", firebaseError.getDetails());
            }
        });

    }

    public void addMoneyToAccount(Firebase accRoot, Double amount, ClientAccount c) {
        String accountName = c.getAccount().getAccountNo();
        Double oldAccountBalance = c.getAccount().getBalance();
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.ticketAccount), false);
        if (accRoot.child(accountName) != null) {
            String accountType = c.getAccount().getAccountType();
            switch (accountType) {
                case "Credit":
                case "Debit": {
                    accRoot.child(accountName).child("AccountBalance").setValue(amount + oldAccountBalance);
                }
                break;
                case "Ticket":
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.ticketAccount), true);
                    validations.setValue(validationsValue);
                    break;
            }
        }
    }

    public void withdrawMoneyFromAccount(Firebase accRoot, Double amount, ClientAccount c) {
        String accountName = c.getAccount().getAccountNo();
        Double oldAccountBalance = c.getAccount().getBalance();
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.maxWithdrawLimit), false);
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit), false);
        validationsValue.put(ApplicationClass.getInstance().getString(R.string.ticketAccount), false);
        if (accRoot.child(accountName) != null) {
            String accountType = c.getAccount().getAccountType();
            switch (accountType) {
                case "Credit": {
                    if (amount > MAX_WITHDRAW_IN_RON) {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.maxWithdrawLimit), true);
                        validations.setValue(validationsValue);
                    } else {
                        accRoot.child(accountName).child("AccountBalance").setValue(oldAccountBalance - amount);
                    }
                    break;
                }

                case "Debit": {
                    if (amount > oldAccountBalance) {
                        validationsValue.put(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit), true);
                        validations.setValue(validationsValue);
                    } else {
                        accRoot.child(accountName).child("AccountBalance").setValue(oldAccountBalance - amount);
                    }
                    break;
                }

                case "Ticket": {
                    validationsValue.put(ApplicationClass.getInstance().getString(R.string.ticketAccount), true);
                    validations.setValue(validationsValue);
                    break;
                }
            }
        }
    }


}