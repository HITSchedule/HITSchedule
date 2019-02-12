package com.example.hitschedule.database;

import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    private String usrId;

    private String pwd;


    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String stuId) {
        this.usrId = stuId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
