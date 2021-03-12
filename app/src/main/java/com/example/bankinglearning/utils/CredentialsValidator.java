package com.example.bankinglearning.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.example.bankinglearning.utils.DateUtil.OUTPUT_DATE_FORMAT;

public class CredentialsValidator {
    private static String datePattern = "^(1[0-9]|0[1-9]|3[0-1]|2[1-9])-(0[1-9]|1[0-2])-[0-9]{4}$";

    public boolean isEmailValid(String email) {
        return !email.isEmpty();
    }

    public boolean isPasswordValid(String pass) {
        return !pass.isEmpty();
    }

    public boolean isNameValid(String name) {
        return !name.isEmpty();
    }

    public boolean isbDateValid(String bDate) {
        return !bDate.isEmpty();
    }

    public boolean iscnpValid(String cnp) {
        return !cnp.isEmpty();
    }

    public boolean isEmailPattern(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public Boolean isDateFormatValid(String date) {
        if (date == null || !date.matches(datePattern))
            return false;
        SimpleDateFormat format = new SimpleDateFormat(OUTPUT_DATE_FORMAT);
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
