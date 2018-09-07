package com.example.aclass.database;

import org.litepal.LitePal;

import cn.bmob.v3.BmobObject;

/**
 * Bmob上的信息，对应于本地的Info类
 */
public class BmobInfo extends BmobObject {

    private String kbxq; //课表当前学期的开始时间

    private String version;  //  app的最新版本号

    private String downloadUrl;  //  app最新版本的下载地址

    private String weekCount;  // 本学期周数


    public String getKbxq() {
        return kbxq;
    }

    public void setKbxq(String kbxq) {
        this.kbxq = kbxq;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean save(String text){

        Info info = new Info(kbxq, version, downloadUrl, weekCount);

        LitePal.deleteAll(Info.class);

        info.save();

        return true;
    }

    public String getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(String weekCount) {
        this.weekCount = weekCount;
    }
}
