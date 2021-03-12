package com.example.bankinglearning.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bankinglearning.R;
import com.example.bankinglearning.views.fragments.AddAccountFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddAccountActivity extends BaseActivity {
    private FloatingActionButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccount);
        final AddAccountFragment addAccountFragment = new AddAccountFragment();
        addFragment(R.id.addAccountContainer, addAccountFragment, "addAccountFragment");

    }

}