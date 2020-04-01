package com.example.hitschedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePal;
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
