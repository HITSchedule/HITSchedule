package com.example.hitschedule.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import com.example.hitschedule.database.MySubject;
import com.example.hitschedule.database.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static String weeks2string(List<Integer> weeks){

        Collections.sort(weeks);

        Log.d("Util", "weeks2string: " + weeks);

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
    public static boolean compareVersion(String newVersion, String oldVersion){

        return ver2num(newVersion) > ver2num(oldVersion);
    }

    /**
     * 将版本号转为数字
     * @param version
     * @return
     */
    private static int ver2num(String version){

        String[] s = version.split("\\.");
        int sum = 0;
        int mut = 1;
        for(int i = 0; i < s.length; i++, mut = mut * 100){
            sum += Integer.valueOf(s[s.length - 1 - i]) * mut;
        }
        Log.d("Util", "ver2num: " + version + " "  + sum);
        return sum;
    }

    /**
     * 缩放bitmap
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        // 获得图片的宽高   
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例   
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数   
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * Subject转为MySubject
     * @param subjects s
     * @return
     */
    public static List<MySubject> subjects2MySubjects(List<Subject> subjects){
        List<MySubject> mySubjects = new ArrayList<>();
        for (Subject subject : subjects){
            mySubjects.add(subject.toMySubject());
        }
        return mySubjects;
    }

    /**
     * MySubject转为Subject
     * @param mySubjects s
     * @return
     */
    public static List<Subject> MySubjects2subjects(List<MySubject> mySubjects){
        List<Subject> subjects = new ArrayList<>();
        for (MySubject mySubject : mySubjects){
            subjects.add(mySubject.toSubject());
        }
        return subjects;
    }

    /**
     * 列表转数组
     * @param weekList
     * @return
     */
    public static Integer[] list2Array(List<Integer> weekList){
        Integer[] weekList_ = new Integer[weekList.size()];
        for (int i = 0; i < weekList.size(); i++){
            weekList_[i] = weekList.get(i);
        }
        return weekList_;
    }

    /**
     * 合并相同课程不同周次的课程
     * @param list 待合并项
     */
    public static List<MySubject> mergeSubject(List<MySubject> list){

        if(list==null) return new ArrayList<>();
        Map<String,List<Integer>> map=new HashMap<>();
        List<MySubject> delete = new ArrayList<>();
        for(MySubject item : list){
            //保证键的唯一性
            String key=item.getName()+"#"+item.getRoom()+"#"+item.getInfo()
                    +"#"+item.getXnxq()+"#"+item.getDay()+"#"
                    +item.getStart()+"#"+item.getStep()+"#"+item.getUsrId();
            if(map.containsKey(key)){
                map.get(key).addAll(item.getWeekList());
                delete.add(item);
            }else{
                map.put(key,item.getWeekList());
            }
        }

        list.removeAll(delete);

        return list;
    }
}
