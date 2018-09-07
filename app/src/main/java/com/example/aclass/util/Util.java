package com.example.aclass.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.Log;

import java.util.Collections;
import java.util.List;

public class Util {

    private String TAG = getClass().getName();

    public String weeks2string(List<Integer> weeks){

        Collections.sort(weeks);

        Log.d(TAG, "weeks2string: " + weeks);

        String s = weeks.get(0).toString();

        for(int i = 1; i < weeks.size(); i++){
            if((weeks.get(i - 1) + 1 < weeks.get(i)) && i > 1){
                s = s + "-" + weeks.get(i-1) + "," + weeks.get(i);
            }
        }

        return s + "-" +weeks.get(weeks.size() - 1) + " 周";
    }

    /**
     * 比较两个版本的大小，若新版本比旧版本大，则返回true
     * @param newVersion
     * @param oldVersion
     * @return
     */
    public boolean compareVersion(String newVersion, String oldVersion){

        return ver2num(newVersion) > ver2num(oldVersion);
    }

    /**
     * 将版本号转为数字
     * @param version
     * @return
     */
    private int ver2num(String version){

        String[] s = version.split("\\.");
        int sum = 0;
        int mut = 1;
        for(int i = 0; i < s.length; i++, mut = mut * 10){
            sum += Integer.valueOf(s[s.length - 1 - i]) * mut;
        }
        Log.d(TAG, "ver2num: " + version + " "  + sum);
        return sum;
    }

}
