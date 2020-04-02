package com.example.hitschedule.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hitschedule.util.LocaleUtil;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    private Context updateBaseContextLocale(Context context) {
        String language = LocaleUtil.getUserLanguage(); // Helper method to get saved language from SharedPreferences
        Locale locale = LocaleUtil.getUserLocale();
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return updateResourcesLocaleLegacy(context, locale);
        } else {
            return updateResourcesLocale(context, locale);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
