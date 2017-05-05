package com.ramgaunt.autorunotify;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Утилитный класс для работы с данными на устройстве
 */
public class PrefUtils {

    /** Сохраняет Cookie на устройство */
    public static void setCookie(Context context, String cookie) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("Cookie", cookie)
                .apply();
    }

    /** Загружает Cookie с устройства */
    public static String getCookie(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("Cookie", null);
    }

    /** Сохраняет UserAgent на устройство */
    public static void setUserAgent(Context context, String userAgent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("UserAgent", userAgent)
                .apply();
    }

    /** Загружает UserAgent с устройства */
    public static String getUserAgent(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("UserAgent", null);
    }
}
