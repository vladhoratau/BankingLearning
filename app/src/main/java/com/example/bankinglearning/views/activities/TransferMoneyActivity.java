package com.example.bankinglearning.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bankinglearning.R;
import com.example.bankinglearning.views.fragments.TransferMoneyFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TransferMoneyActivity extends BaseActivity {
    private FloatingActionButton back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfermoney);
        TransferMoneyFragment transferMoneyFragment = new TransferMoneyFragment();
        addFragment(R.id.transferMoneyContainer, transferMoneyFragment, "transferMoneyFragment");


    }
}
