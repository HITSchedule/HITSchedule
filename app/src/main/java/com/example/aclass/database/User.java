package com.example.aclass.database;

import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    private String stuId;

    private String pwd;


    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
