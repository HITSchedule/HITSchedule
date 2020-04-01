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
    public static final String LOCALE_ENGLISH = "en_us";
    public static final String LOCALE_DEFAULT = "default";
    private static final String TAG = "LocaleUtil";

    private static SharedPreferences getLanguagePreference() {
        return Application.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    /**
     * 获取保存的语言
     */
    public static String getSavedLanguage() {
        SharedPreferences preferences = getLanguagePreference();
        String language = preferences.getString("language", LOCALE_DEFAULT);
        Log.d(TAG, "getSavedLanguage: saved language is " + language);
        return language;
    }
}
