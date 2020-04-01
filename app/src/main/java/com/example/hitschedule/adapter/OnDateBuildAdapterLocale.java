package com.example.hitschedule.adapter;

import com.example.hitschedule.Application;
import com.example.hitschedule.R;
import com.zhuangfei.timetable.listener.OnDateBuildAapter;
import com.zhuangfei.timetable.model.ScheduleSupport;

/**
 * 英语日期栏
 */
public class OnDateBuildAdapterLocale extends OnDateBuildAapter {

    @Override
    public String[] getStringArray() {
        String[] dayArray = new String[8];
        dayArray[0] = null;
        String[] dayRes = Application.applicationContext.getResources().getStringArray(R.array.day);
        for (int i = 0; i < 7; ++i) {
            dayArray[i+1] = dayRes[i];
        }
        return dayArray;
        //return new String[]{null, "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    }

    @Override
    public void onUpdateDate(int curWeek, int targetWeek) {
        if (textViews == null || textViews.length < 8) return;

        String[] monthArray=Application.applicationContext.getResources().getStringArray(R.array.month);
        weekDates = ScheduleSupport.getDateStringFromWeek(curWeek, targetWeek);
        int month = Integer.parseInt(weekDates.get(0));
        textViews[0].setText(monthArray[month-1]);
        for (int i = 1; i < 8; i++) {
            if (textViews[i] != null) {
                textViews[i].setText(weekDates.get(i));
            }
        }
    }
}