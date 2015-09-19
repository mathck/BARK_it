package com.barkitapp.android._core.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.barkitapp.android._core.utility.Constants;

import java.util.Date;

public class InternalAppData {

    public static String getString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void Store(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    public static void Store(Context context, String key, Long value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(key, value).apply();
    }

    public static Long getLongTime(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(key, new Date().getTime());
    }

    public static Long getLong(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(key, 0);
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static Boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }

    public static void Store(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
}
