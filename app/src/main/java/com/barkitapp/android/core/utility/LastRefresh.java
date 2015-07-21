package com.barkitapp.android.core.utility;

import android.content.Context;

import com.barkitapp.android.core.services.InternalAppData;

public class LastRefresh {

    public static void now(Context context) {
        InternalAppData.Store(context, SharedPrefKeys.LAST_REFRESH, System.currentTimeMillis());
    }

    public static boolean isAvailable(Context context) {
        long value = InternalAppData.getLong(context, SharedPrefKeys.LAST_REFRESH);

        if(value == 0)
            return true;

        long difference = System.currentTimeMillis() - value;

        if((difference / 1000) >= 180)
            return true;

        return false;
    }
}
