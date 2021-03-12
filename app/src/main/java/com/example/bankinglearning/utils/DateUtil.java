package com.example.bankinglearning.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final String OUTPUT_DATE_FORMAT = "dd-MM-yyyy";
    public static final String INPUT_DATE_FORMAT = "MMM dd, yyyy";


    public static String convertDateFormat(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.ENGLISH);

        Date date;
        String str;

        try {
            date = inputFormat.parse(inputDate);
            str = outputFormat.format(date);
            Log.e("Log ", "str " + str);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDateFromString(String data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;
    }

}


