package com.barkitapp.android._core.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.barkitapp.android._core.services.InternalAppData;

public class Settings {

    public static boolean isNotificationSoundEnabled(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean(SettingKeys.NOTIFICATION_SOUND, true);
    }

    public static boolean isVoteNotificationEnabled(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean(SettingKeys.NOTIFICATION_VOTE_ENABLED, true);
    }

    public static boolean isReplyNotificationEnabled(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean(SettingKeys.NOTIFICATION_VOTE_REPLY, true);
    }

    public static boolean isBarkOfTheDayNotificationEnabled(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean(SettingKeys.NOTIFICATION_BARK_OF_THE_DAY_ENABLED, true);
    }
}
