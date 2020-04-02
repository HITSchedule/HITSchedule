package com.example.hitschedule.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.hitschedule.Application;

import java.util.Locale;

public class LocaleUtil {
    public static final String LOCALE_CHINESE = "zh_cn";
    public static final String LOCALE_ENGLISH = "en";
    public static final String LOCALE_DEFAULT = "default";
    private static final String TAG = "LocaleUtil";

    private static SharedPreferences getLanguagePreference() {
        return Application.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    /**
     * 保存语言
     */
    public static void saveLanguage(String language)
    {
        SharedPreferences languagePreference = getLanguagePreference();
        SharedPreferences.Editor editor = languagePreference.edit();
        editor.putString("language", language);
        editor.commit();
    }

    /**
     * 获取保存的语言
     */
    public static String getUserLanguage() {
        SharedPreferences preferences = getLanguagePreference();
        String language = preferences.getString("language", LOCALE_DEFAULT);
        Log.d(TAG, "getSavedLanguage: saved language is " + language);
        return language;
    }

    /**
     * 获取用户Local
     */
    public static Locale getUserLocale() {
        String language = getUserLanguage();
        Locale locale;
        if (language.equals(LocaleUtil.LOCALE_DEFAULT)) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(language);
        }
        return locale;
    }
}
