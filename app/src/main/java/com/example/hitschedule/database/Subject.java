package com.example.hitschedule.database;

import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import java.util.Arrays;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.disposables.Disposable;

import static com.example.hitschedule.util.Constant.INFO;
import static com.example.hitschedule.util.Constant.RESERVED1;
import static com.example.hitschedule.util.Constant.RESERVED2;
import static com.example.hitschedule.util.Constant.RESERVED3;
import static com.example.hitschedule.util.Constant.RESERVED4;
import static com.example.hitschedule.util.Constant.TYPE;
import static com.example.hitschedule.util.Constant.UDRID;
import static com.example.hitschedule.util.Constant.XNXQ;


/**
 * 自定义课程项
 */
public class Subject extends BmobObject implements ScheduleEnable {

    private String name; //课程名

    private String xnxq;

    private String usrId;

    private String room; // 教室

    private String teacher; // 教师

    private Integer[] weekList; // 第几周至第几周上

    private int start; // 开始上课的节次

    private int step; // 上课节数

    private int day; // 周几上

    private String type;

    private String info;

    private String reserved1;

    private String reserved2;

    private String reserved3;

    private String reserved4;

    private int colorRandom = 0 ; // 一个随机数，用于对应课程的颜色


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

    public Integer[] getWeekList() {
        return weekList;
    }

    public void setWeekList(Integer [] weekList) {
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
        Schedule schedule=new Schedule();
        schedule.setDay(getDay());
        schedule.setName(getName());
        schedule.setRoom(getRoom());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setTeacher(getTeacher());
        schedule.setWeekList(Arrays.asList(getWeekList()));
        schedule.setColorRandom(getColorRandom());
        schedule.putExtras(INFO, getInfo());
        schedule.putExtras(XNXQ, getXnxq());
        schedule.putExtras(UDRID, getUsrId());
        schedule.putExtras(TYPE, getType());
        schedule.putExtras(RESERVED1, getReserved1());
        schedule.putExtras(RESERVED2, getReserved2());
        schedule.putExtras(RESERVED4, getReserved4());
        schedule.putExtras(RESERVED3, getReserved3());
        return schedule;
    }

    @Override
    public Disposable save(SaveListener<String> listener) {
        return super.save(listener);
    }

    public MySubject toMySubject(){
        MySubject subject = new MySubject();
        subject.setObjectId(getObjectId());
        subject.setName(getName());
        subject.setDay(getDay());
        subject.setName(getName());
        subject.setRoom(getRoom());
        subject.setStart(getStart());
        subject.setStep(getStep());
        subject.setTeacher(getTeacher());
        subject.setWeekList(Arrays.asList(getWeekList()));
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


}

