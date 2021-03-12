package com.example.bankinglearning.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.models.ClientAccount.ClientAccount;
import com.example.bankinglearning.viewModels.ClientAccountViewModel;
import com.example.bankinglearning.viewModels.TransferMoneyViewModel;
import com.example.bankinglearning.views.activities.FirstActivity;
import com.example.bankinglearning.views.adapters.AccountAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;

public class TransferMoneyFragment extends Fragment {
    private EditText recipientNameTextView, recipientAccountTextView, amountToSend;
    private Button sendMoneyButton;
    private AccountAdapter accountAdapter;
    private Spinner spinner;
    public ClientAccountViewModel clientAccountViewModel;
    public TransferMoneyViewModel transferMoneyViewModel;
    private List<ClientAccount> recievedClientAccounts;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfermoney, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        amountToSend = view.findViewById(R.id.amountToSend);
        recipientAccountTextView = view.findViewById(R.id.recipientAccount);
        recipientNameTextView = view.findViewById(R.id.recipientName);
        sendMoneyButton = view.findViewById(R.id.sendMoney);
        spinner = view.findViewById(R.id.spinner);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clientAccountViewModel = new ViewModelProvider(this).get(ClientAccountViewModel.class);
        transferMoneyViewModel = new ViewModelProvider(this).get(TransferMoneyViewModel.class);
        recievedClientAccounts = new ArrayList<>();
        accountAdapter = new AccountAdapter(recievedClientAccounts);
        spinner.setAdapter(accountAdapter);
        clientAccountViewModel.getClientAccountAttributes().observe(getViewLifecycleOwner(), new Observer<List<ClientAccount>>() {
            @Override
            public void onChanged(List<ClientAccount> clientAccounts) {
                if (clientAccounts != null) {
                    recievedClientAccounts.clear();
                    recievedClientAccounts.addAll(clientAccounts);
                    accountAdapter.notifyDataSetChanged();
                }
            }
        });

        sendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientAccount = String.valueOf(recipientAccountTextView.getText());
                Double amount = Double.valueOf(String.valueOf(amountToSend.getText()));
                String recipientName = String.valueOf(recipientNameTextView.getText());
                ClientAccount c = (ClientAccount) spinner.getSelectedItem();
                transferMoneyViewModel.transferMoneyToAccount(amount, recipientAccount, recipientName, c);
            }

        });

        transferMoneyViewModel.getValidations().observe(getViewLifecycleOwner(), new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> validations) {
                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.ticketAccount))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.ticketAccount))) {
                    showMessage("You cannot send money to a ticket account!");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.minWithdrawLimit))) {
                    showMessage("You don't have enough money. The maximum amount you can send is: " + ((ClientAccount) spinner.getSelectedItem()).getAccount().getBalance().intValue() + " RON");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.sentMoney))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.sentMoney))) {
                    showMessage("You successfully sent " + amountToSend.getText() + " RON to " + recipientAccountTextView.getText());
                    openFirstActivity();
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.validSenderAcc))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.validSenderAcc))) {
                    showMessage("Invalid sender account name");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.validRecipientAcc))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.validRecipientAcc))) {
                    showMessage("Invalid recipient account name");
                }
            }
        });
    }

    private void openFirstActivity() {
        Intent i = new Intent(getContext(), FirstActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}

