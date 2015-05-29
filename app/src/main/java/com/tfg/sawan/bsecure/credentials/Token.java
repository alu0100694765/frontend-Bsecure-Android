package com.tfg.sawan.bsecure.credentials;

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
}
