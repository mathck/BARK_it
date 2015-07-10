package com.barkitapp.android.core.services;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceId {

    public static String get(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }
}
