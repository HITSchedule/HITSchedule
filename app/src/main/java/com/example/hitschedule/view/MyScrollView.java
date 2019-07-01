package com.example.hitschedule.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.example.hitschedule.ui.MainActivity;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.view.WeekView;

import java.util.logging.Handler;

/**
 * 自定义ScrollView，方便左右滑动
 */

public class MyScrollView extends ScrollView {
    private int lx, ly;
    private TimetableView mTimeTableView;
    private WeekView mWeekView;
    private int week;
    private MainActivity.UIHandler handler;
    private final int CHANGE_WEEK = 2115;

    public MyScrollView(Context context) {
        super(context);
    }

    public void setHandler(MainActivity.UIHandler handler){
        this.handler = handler;
    }

    public void setWeek(int week) {
        this.week = week;
    }


    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTimeTableView(TimetableView mTimeTableView) {
        this.mTimeTableView = mTimeTableView;
    }

    public void setWeekView(WeekView mWeekView) {
        this.mWeekView = mWeekView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                getParent().requestDisallowInterceptTouchEvent(true);
                lx = x;
                ly = y;
                break;
            }
            case MotionEvent.ACTION_UP:{
                int deltaX = x - lx;
                Log.d("xxxxxxxxxxxxx", "dispatchTouchEvent: " + week + "  " + mTimeTableView.curWeek());
                if (deltaX > 200 && week > 1){
                    week = week - 1;
                    //更新切换后的日期，从当前周cur->切换的周week
                    mTimeTableView.changeWeekOnly(week);
//                    mTimeTableView.updateDateView();
                    mTimeTableView.onDateBuildListener()
                            .onUpdateDate(mTimeTableView.curWeek(), week);
                    Message msg = new Message();
                    msg.what = CHANGE_WEEK;
                    msg.arg1 = week;
                    handler.sendMessage(msg);
                    mWeekView.updateView();
                }else if(deltaX < -200 && week < mWeekView.itemCount()){
                    week = week + 1;
                    //更新切换后的日期，从当前周cur->切换的周week
                    mTimeTableView.changeWeekOnly(week);
                    mTimeTableView.onDateBuildListener()
                            .onUpdateDate(mTimeTableView.curWeek(), week);
                    Message msg = new Message();
                    msg.what = CHANGE_WEEK;
                    msg.arg1 = week;
                    handler.sendMessage(msg);
                    mWeekView.updateView();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}