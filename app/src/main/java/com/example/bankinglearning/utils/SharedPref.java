package com.example.bankinglearning.utils;

import android.content.SharedPreferences;

import com.example.bankinglearning.ApplicationClass;

public class SharedPref {

    public void putSharedPref(String id, int mode) {
        SharedPreferences.Editor editor = ApplicationClass.getInstance().getSharedPreferences(id, mode).edit();
        editor.putBoolean(id, true);
        editor.apply();
    }

    public boolean getSharedPref(String id, int mode) {
        SharedPreferences sharedPreferences = ApplicationClass.getInstance().getSharedPreferences(id, mode);
        boolean defaultVal = sharedPreferences.getBoolean(id, false);
        return defaultVal;
    }
}
