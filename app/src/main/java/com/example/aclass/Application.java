package com.example.aclass;

import org.litepal.LitePal;
import org.xutils.x;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
