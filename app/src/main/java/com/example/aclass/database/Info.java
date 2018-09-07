package com.example.aclass.database;

import org.litepal.crud.LitePalSupport;

public class Info extends LitePalSupport {

    private String kbxq; //课表当前学期的开始时间

    private String version;  //  app的最新版本号

    private String downloadUrl;  //  app最新版本的下载地址

    private String weekCount;  // 本学期周数

    public Info(String kbxq, String version, String downloadUrl, String weekCount){
        this.kbxq = kbxq;
        this.version = version;
        this.downloadUrl = downloadUrl;
        this.weekCount = weekCount;
    }

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

    public String getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(String weekCount) {
        this.weekCount = weekCount;
    }
}
