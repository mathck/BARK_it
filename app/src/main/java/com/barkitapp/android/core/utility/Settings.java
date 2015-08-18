package com.barkitapp.android.core.utility;

import android.content.Context;

import com.barkitapp.android.core.services.InternalAppData;

public class Settings {

    public static boolean isNotificationSoundEnabled(Context context) {
        return InternalAppData.getBoolean(context, SettingKeys.NOTIFICATION_SOUND, true);
    }

    public static boolean isNotificationEnabled(Context context) {
        return InternalAppData.getBoolean(context, SettingKeys.NOTIFICATION_ENABLED, true);
    }
}
