package com.tfg.sawan.bsecure.credentials;

import android.util.Log;

import com.tfg.sawan.bsecure.utils.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sawan on 30/05/2015.
 */
public class Token {
    private static String token;
    private static String expiry_date;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Token.token = token;
    }

    public static String getExpiry_date() {
        return expiry_date;
    }

    public static void setExpiry_date(String expiry_date) {
        Token.expiry_date = expiry_date;
    }

    public static boolean isExpired() {
        SimpleDateFormat date_format = new SimpleDateFormat("MM-dd-yyyy");
        Date expiry_date_token = new Date();
        Date actual_date =  new Date();

        Log.d("Date", Token.expiry_date);

        try {
            expiry_date_token = date_format.parse(Token.expiry_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // If expiry date is before actual date it means it has not expired
        if (expiry_date_token.before(actual_date)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean tokenExists() {
        if ((Token.token != null) && (Token.token.compareTo(Preferences.NOT_FOUND_MESSAGE) != 0)) {
            return true;
        } else {
            return false;
        }
    }
}
