package com.example.hitschedule;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.DisplayMetrics;

import com.example.hitschedule.util.LocaleUtil;

import org.litepal.LitePal;

import java.util.Locale;
//import org.xutils.x;

public class Application extends android.app.Application {
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();
    }
}
