package com.example.hitschedule.database;

import android.util.Log;

import com.example.hitschedule.util.Util;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static com.example.hitschedule.util.Constant.INFO;
import static com.example.hitschedule.util.Constant.RESERVED1;
import static com.example.hitschedule.util.Constant.RESERVED2;
import static com.example.hitschedule.util.Constant.RESERVED3;
import static com.example.hitschedule.util.Constant.RESERVED4;
import static com.example.hitschedule.util.Constant.TYPE;
import static com.example.hitschedule.util.Constant.UDRID;
import static com.example.hitschedule.util.Constant.XNXQ;

public class MySubject extends LitePalSupport implements ScheduleEnable, Serializable {

    private String name; //课程名
    private String xnxq;
    private String usrId;
    private String room = "暂无"; // 教室
    private String teacher; // 教师
    private List<Integer> weekList; // 第几周至第几周上
    private int start; // 开始上课的节次
    private int step; // 上课节数
    private int day; // 周几上
    private String type;
    private String info;
    private String reserved1;
    private String reserved2;
    private String reserved3;
    private String reserved4;

    private int colorRandom = 0;; // 一个随机数，用于对应课程的颜色

    private String objectId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXnxq() {
        return xnxq;
    }

    public void setXnxq(String xnxq) {
        this.xnxq = xnxq;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public List<Integer> getWeekList() {
        return weekList;
    }

    public void setWeekList(List<Integer> weekList) {
        this.weekList = weekList;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public int getColorRandom() {
        return colorRandom;
    }

    public void setColorRandom(int colorRandom) {
        this.colorRandom = colorRandom;
    }

    @Override
    public Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setDay(getDay());
        schedule.setName(getName());
        schedule.setRoom(getRoom());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setTeacher(getTeacher());
        schedule.setWeekList(getWeekList());
        schedule.setColorRandom(getColorRandom());
        schedule.putExtras(INFO, getInfo());
        schedule.putExtras(XNXQ, getXnxq());
        schedule.putExtras(UDRID, getUsrId());
        schedule.putExtras(XNXQ, getXnxq());
        schedule.putExtras(TYPE, getType());
        schedule.putExtras(RESERVED1, getReserved1());
        schedule.putExtras(RESERVED2, getReserved2());
        schedule.putExtras(RESERVED4, getReserved4());
        schedule.putExtras(RESERVED3, getReserved3());
        return schedule;
    }

    public Subject toSubject(){
        Subject subject = new Subject();
        subject.setName(getName());
        subject.setDay(getDay());
        subject.setName(getName());
        subject.setRoom(getRoom());
        subject.setStart(getStart());
        subject.setStep(getStep());
        subject.setTeacher(getTeacher());
        subject.setWeekList(Util.list2Array(getWeekList()));
        subject.setColorRandom(getColorRandom());
        subject.setInfo(getInfo());
        subject.setType(getType());
        subject.setXnxq(getXnxq());
        subject.setUsrId(getUsrId());
        subject.setReserved1(getReserved1());
        subject.setReserved2(getReserved2());
        subject.setReserved3(getReserved3());
        subject.setReserved4(getReserved4());
        return subject;
    }

    public boolean _save() {

        List<MySubject> list = LitePal.where("usrId = ? and name = ? and xnxq = ? and day = ? and start = ?",
                usrId, name, xnxq, String.valueOf(day), String.valueOf(start)).find(MySubject.class);

        if (list.isEmpty()) {
            return this.save();
        } else {
            for (MySubject subject : list){
                subject.setWeekList(weekList);
                subject.save();
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(MySubject.class)){
            return false;
        }

        if (obj == this){
            return true;
        }

        MySubject subject = (MySubject) obj;

        return subject.getName().equals(name) &&
                new HashSet<Integer>(weekList).equals(new HashSet<Integer>(subject.getWeekList())) &&
                subject.getDay() == day &&
//                subject.getInfo().equals(info) &&
                subject.getXnxq().equals(xnxq) &&
//                subject.getRoom().equals(room) &&
                subject.getStart() == start &&
//                subject.getStep() == step &&
                subject.getUsrId().equals(usrId);

    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "MySubject{" +
                "name='" + name + '\'' +
                ", xnxq='" + xnxq + '\'' +
                ", room='" + room + '\'' +
                ", teacher='" + teacher + '\'' +
                ", weekList=" + weekList +
                ", start=" + start +
                ", step=" + step +
                ", day=" + day +
                ", type='" + type + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
