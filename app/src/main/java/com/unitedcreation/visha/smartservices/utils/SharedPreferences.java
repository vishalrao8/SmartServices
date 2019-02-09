package com.unitedcreation.visha.smartservices.utils;

import android.content.Context;

public class SharedPreferences {

    private static final String PREFERENCES_NAME = "com.example.visha.smarttechnician";
    private static final String PREFERENCES_KEY = "user_logged_in";
    private static android.content.SharedPreferences sharedPreferences;

    public static void userLoggedIn(Context context) {

        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        sharedPreferences.edit().putBoolean(PREFERENCES_KEY, true).apply();

    }
    public static void userLoggedOut(Context context) {

        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        sharedPreferences.edit().putBoolean(PREFERENCES_KEY, false).apply();

    }

    public static Boolean isUserLoggedIn(Context context) {

        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        return sharedPreferences.getBoolean(PREFERENCES_KEY, false);

    }
}
