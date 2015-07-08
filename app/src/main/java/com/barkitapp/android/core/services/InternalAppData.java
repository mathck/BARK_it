package com.barkitapp.android.core.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.barkitapp.android.core.utility.Constants;

public class InternalAppData {

    public static String getString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void Store(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void Store(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
}
