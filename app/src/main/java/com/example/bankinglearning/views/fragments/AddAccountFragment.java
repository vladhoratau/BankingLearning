package com.example.bankinglearning.views.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bankinglearning.ApplicationClass;
import com.example.bankinglearning.R;
import com.example.bankinglearning.viewModels.AddAccountViewModel;
import com.example.bankinglearning.views.activities.FirstActivity;
import com.example.bankinglearning.views.adapters.AccountTypeAdapter;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.bankinglearning.utils.ToastMessage.showMessage;
import static com.example.bankinglearning.viewModels.ClientAccountViewModel.FirebaseRoot;

public class AddAccountFragment extends Fragment {
    private ListView listView;
    private Button submit;
    private EditText newAccountName;
    private int selectedPosition = -1;
    private View previousSelectedItem;
    private AddAccountViewModel addAccountViewModel;
    private AccountTypeAdapter accountTypeAdapter;
    private List<String> accountTypes;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addaccount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newAccountName = view.findViewById(R.id.newAccountNameEditText);
        listView = view.findViewById(R.id.ListView);
        submit = view.findViewById(R.id.submit);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addAccountViewModel = new ViewModelProvider(this).get(AddAccountViewModel.class);
        accountTypes = new ArrayList<>();
        accountTypeAdapter = new AccountTypeAdapter(accountTypes);
        listView.setAdapter(accountTypeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousSelectedItem != null) {
                    previousSelectedItem.setBackgroundColor(Color.WHITE);
                }
                previousSelectedItem = view;
                previousSelectedItem.setBackgroundColor(Color.WHITE);
                String selectedItem = (String) parent.getItemAtPosition(position);
                selectedPosition = position;
                view.setBackgroundColor(Color.parseColor("#EFF0F1"));
            }
        });

        addAccountViewModel.getAccountTypeList().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> accountTypeListValues) {
                if (accountTypeListValues != null) {
                    accountTypes.clear();
                    accountTypes.addAll(accountTypeListValues);
                    accountTypeAdapter.notifyDataSetChanged();
                }
            }
        });

        addAccountViewModel.getValidations().observe(getViewLifecycleOwner(), new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> validations) {
                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.sameNameAcc))
                        && validations.get(ApplicationClass.getInstance().getString(R.string.sameNameAcc))) {
                    showMessage("Account with same name already exists!");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.selectedPosition))
                        && !validations.get(ApplicationClass.getInstance().getString(R.string.selectedPosition))) {
                    showMessage("Please select the account type!");
                }

                if (validations.containsKey(ApplicationClass.getInstance().getString(R.string.openFirstActivity))) {
                    if (validations.get(ApplicationClass.getInstance().getString(R.string.openFirstActivity))) {
                        showMessage("Account successfully added.");
                        openFirstActivity();
                    } else {
                        showMessage("Could not add the account. Please try again.");
                    }
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    String UID = firebaseAuth.getCurrentUser().getUid();
                    final Firebase accountsRoot = new Firebase(FirebaseRoot + UID + "/Accounts");
                    final String accountName = newAccountName.getText().toString();
                    addAccountViewModel.addAccount(accountsRoot, accountName, selectedPosition, accountTypes.get(selectedPosition));

                } catch (Exception e) {
                    e.printStackTrace();
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