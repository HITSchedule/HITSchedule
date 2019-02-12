package com.example.hitschedule;

import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePal;
//import org.xutils.x;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();
    }
}
