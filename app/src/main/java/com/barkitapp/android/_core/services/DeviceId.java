package com.barkitapp.android._core.services;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.barkitapp.android._core.utility.SharedPrefKeys;

import java.util.UUID;

public class DeviceId {

    public static boolean isDeviceIdUnique(Context context) {
        return InternalAppData.getBoolean(context, SharedPrefKeys.DEVICE_ID_UNIQUE);
    }

    public static String get(Context context) {

        // try 1
        final String try1 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if(try1 != null && !try1.isEmpty()) {
            InternalAppData.Store(context, SharedPrefKeys.DEVICE_ID_UNIQUE, true);
            return try1;
        }

        InternalAppData.Store(context, SharedPrefKeys.DEVICE_ID_UNIQUE, false);

        // try 2
        final String try2 = android.os.Build.SERIAL;
        if(try2 != null && !try2.isEmpty()) {
            return try2;
        }

        // try 3
        /*
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String try3 = deviceUuid.toString();
        if(!try3.trim().isEmpty()) {
            return try3;
        }
        */

        // try 4
        return UUID.randomUUID().toString();
    }
}
