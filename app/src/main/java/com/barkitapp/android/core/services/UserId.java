package com.barkitapp.android.core.services;

import android.content.Context;

import com.barkitapp.android.core.utility.SharedPrefKeys;

public class UserId {

    public static void store(Context context, String userId) {
        InternalAppData.Store(context, SharedPrefKeys.USER_ID, userId);
    }

    public static String get(Context context) {
        return InternalAppData.getString(context, SharedPrefKeys.USER_ID);
    }
}
