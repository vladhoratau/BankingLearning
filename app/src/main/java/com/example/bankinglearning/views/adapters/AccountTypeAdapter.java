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

public class AccountTypeAdapter extends ArrayAdapter {
    public AccountTypeAdapter(List<String> accountTypes) {
        super(ApplicationClass.getInstance(), 0, accountTypes);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounttype, parent, false);
        }
        TextView accountType = convertView.findViewById(R.id.accountTypeTextView);
        String currentAccountType = (String) getItem(position);
        if (currentAccountType != null) {
            accountType.setText(currentAccountType);
        }
        return convertView;
    }
}
