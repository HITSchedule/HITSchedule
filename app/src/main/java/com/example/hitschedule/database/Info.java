package com.example.hitschedule.database;

import cn.bmob.v3.BmobObject;

public class Info extends BmobObject {

    private String xnxq;

    private int weekNum;

    private String startTime;

    private String latestVersion;

    private String apkUrl;

    private String reserved1;

    private String reserved2;

    private String reserved3;

    private String reserved4;


    public String getXnxq() {
        return xnxq;
    }

    public void setXnxq(String xnxq) {
        this.xnxq = xnxq;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public String getReserved3() {
        return reserved3;
    }

    public void setReserved3(String reserved3) {
        this.reserved3 = reserved3;
    }

    public String getReserved4() {
        return reserved4;
    }

    public void setReserved4(String reserved4) {
        this.reserved4 = reserved4;
    }

    public MyInfo toMyInfo(){
        MyInfo info = new MyInfo();
        info.setApkUrl(getApkUrl());
        info.setLatestVersion(getLatestVersion());
        info.setStartTime(getStartTime());
        info.setWeekNum(getWeekNum());
        info.setXnxq(getXnxq());
        info.setReserved1(getReserved1());
        info.setReserved2(getReserved2());
        info.setReserved3(getReserved3());
        info.setReserved4(getReserved4());
        return info;
    }
}
