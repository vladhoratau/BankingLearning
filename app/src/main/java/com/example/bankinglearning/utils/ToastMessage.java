package com.example.bankinglearning.utils;

import android.widget.Toast;

import com.example.bankinglearning.ApplicationClass;

public class ToastMessage {

    public static void showMessage(String message) {
        Toast.makeText(ApplicationClass.getInstance(), message, Toast.LENGTH_SHORT).show();
    }
}
