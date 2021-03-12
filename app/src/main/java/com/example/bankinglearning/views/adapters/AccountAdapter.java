package com.example.bankinglearning.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.models.ClientAccount.ClientAccount;

import java.util.List;

public class AccountAdapter extends ArrayAdapter {

    public AccountAdapter(List<ClientAccount> clientAccounts) {
        super(ApplicationClass.getInstance(), 0, clientAccounts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.account, parent, false);
        }
        TextView accountNo = convertView.findViewById(R.id.accountNameTextView);
        ClientAccount currentAccount = (ClientAccount) getItem(position);
        if (currentAccount != null) {
            accountNo.setText(currentAccount.getAccount().getAccountNo());
        }
        return convertView;
    }
}