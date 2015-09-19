package com.barkitapp.android._core.utility;

import android.content.Context;

import com.barkitapp.android._core.services.InternalAppData;

public class LastRefresh {

    public static void now(Context context) {
        InternalAppData.Store(context, SharedPrefKeys.LAST_REFRESH, System.currentTimeMillis());
    }

    public static boolean isAvailable(Context context) {
        long value = InternalAppData.getLong(context, SharedPrefKeys.LAST_REFRESH);

        if(value == 0)
            return true;

        long difference = System.currentTimeMillis() - value;

        return (difference / 1000) >= 180;

    }
}
