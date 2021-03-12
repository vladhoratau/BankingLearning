package com.example.bankinglearning.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.models.ClientAccount.ClientAccount;
import com.example.bankinglearning.viewModels.ClientAccountViewModel;
import com.example.bankinglearning.views.adapters.AccountAdapter;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;
import static com.example.bankinglearning.viewModels.ClientAccountViewModel.FirebaseRoot;
import static com.example.bankinglearning.viewModels.ClientAccountViewModel.MAX_WITHDRAW_IN_RON;


public class ClientAccountFragment extends Fragment implements Serializable {
    private TextView clientNameTextView, clientBdateTextView, clientCNPTextView, accountBalanceTextView, accountTypeTextView, transactionHistoryTextView;
    public ClientAccountViewModel clientAccountViewModel;
    private AccountAdapter accountAdapter;
    private Spinner spinner;
    private Button addMoney, withdrawMoney, showContent;
    private NumberPicker operationsPicker;
    private List<ClientAccount> recievedClientAccounts;
    private View accountOperationsContainer, transactionHistoryContainer;
    private boolean showContainer = true;
    private List<String> transactionHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clientaccount, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clientNameTextView = view.findViewById(R.id.clientNameTextView);
        clientBdateTextView = view.findViewById(R.id.clientBDateTextView);
        clientCNPTextView = view.findViewById(R.id.clientCNPTextView);
        spinner = view.findViewById(R.id.accountChooserSpinner);
        accountBalanceTextView = view.findViewById(R.id.accountBalanceTextView);
        addMoney = view.findViewById(R.id.addButton);
        withdrawMoney = view.findViewById(R.id.withdrawButton);
        operationsPicker = view.findViewById(R.id.operationsPicker);
        accountTypeTextView = view.findViewById(R.id.accountTypeTextView);
        showContent = view.findViewById(R.id.showContent);
        accountOperationsContainer = view.findViewById(R.id.accountOperationsContainer);
        transactionHistoryTextView = view.findViewById(R.id.transactionHistory);
        transactionHistoryContainer = view.findViewById(R.id.transactionHistoryContainer);
        operationsPicker.setMinValue(1);
        operationsPicker.setMaxValue(10000);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Firebase.setAndroidContext(getContext());
        clientAccountViewModel = new ViewModelProvider(this).get(ClientAccountViewModel.class);
        recievedClientAccounts = new ArrayList<>();
        accountAdapter = new AccountAdapter(recievedClientAccounts);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final String UID = firebaseAuth.getCurrentUser().getUid();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClientAccount selectedAccount = (ClientAccount) parent.getItemAtPosition(position);
                accountBalanceTextView.setText(ApplicationClass.getInstance().getString(R.string.account_balance, selectedAccount.getAccount().getBalance().toString()));
                accountTypeTextView.setText(ApplicationClass.getInstance().getString(R.string.account_type, selectedAccount.getAccount().getAccountType()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                addMoney.setEnabled(false);
                withdrawMoney.setEnabled(false);
            }
        });
        spinner.setAdapter(accountAdapter);
        clientAccountViewModel.getClientAccountAttributes().observe(getViewLifecycleOwner(), new Observer<List<ClientAccount>>() {
            @Override
            public void onChanged(List<ClientAccount> clientAccounts) {
                if (clientAccounts != null) {
                    clientNameTextView.setText("Holder Name: " + clientAccounts.get(0).getClient().getName());
                    clientBdateTextView.setText("Holder Birth date: " + clientAccounts.get(0).getClient().getBirthDate().toString());
                    clientCNPTextView.setText("Personal Identity Number: " + clientAccounts.get(0).getClient().getCnp());
                    recievedClientAccounts.clear();
                    recievedClientAccounts.addAll(clientAccounts);
                    if (spinner.getSelectedItemPosition() >= 0 && spinner.getSelectedItemPosition() < clientAccounts.size()) {
                        accountBalanceTextView.setText(ApplicationClass.getInstance().getString(R.string.account_balance, clientAccounts.get(spinner.getSelectedItemPosition()).getAccount().getBalance().toString()));
                        accountTypeTextView.setText(ApplicationClass.getInstance().getString(R.string.account_type, clientAccounts.get(spinner.getSelectedItemPosition()).getAccount().getAccountType()));
                    }
                    accountAdapter.notifyDataSetChanged();
                }
            }
        });

        clientAccountViewModel.getValidations().observe(getViewLifecycleOwner(), new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> validations) {
                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.ticketAccount))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.ticketAccount))) {
                    showMessage("You cannot add/withdraw money on the ticket account!");
                }


                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit))) {
                    showMessage("You don't have enough money");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.maxWithdrawLimit))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.maxWithdrawLimit))) {
                    showMessage("The maximum value you can withdraw is: " + MAX_WITHDRAW_IN_RON);
                }

            }
        });
        transactionHistory = new ArrayList<>();

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoney.setEnabled(true);
                Firebase accountsRoot = new Firebase(FirebaseRoot + UID + "/Accounts");
                Double amount = Double.valueOf(operationsPicker.getValue());
                ClientAccount c = (ClientAccount) spinner.getSelectedItem();
                clientAccountViewModel.addMoneyToAccount(accountsRoot, amount, c);


            }
        });

        withdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawMoney.setEnabled(true);
                Firebase accountsRoot = new Firebase(FirebaseRoot + UID + "/Accounts");
                //Firebase transactionHistoryRoot = new Firebase(FirebaseRoot + UID + "/Transaction History");
                Double amount = Double.valueOf(operationsPicker.getValue());
                ClientAccount c = (ClientAccount) spinner.getSelectedItem();
                clientAccountViewModel.withdrawMoneyFromAccount(accountsRoot, amount, c);

            }
        });


        showContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showContainer) {
                    showContent.setText("Make a transaction");
                    showContainer = false;
                    accountOperationsContainer.setVisibility(View.INVISIBLE);
                    transactionHistoryContainer.setVisibility(View.VISIBLE);
                    transactionHistoryTextView.setText(String.valueOf(transactionHistory));

                } else {
                    showContent.setText("Transaction details");
                    showContainer = true;
                    accountOperationsContainer.setVisibility(View.VISIBLE);
                    transactionHistoryContainer.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}









