package com.example.hitschedule.util;

import android.util.Log;

import com.example.hitschedule.database.MyInfo;
import com.example.hitschedule.database.MySubject;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.MapTimeZoneCache;

import java.util.List;
import java.util.Locale;

public class IcalUtil {
    private Date termStartDate;
    private MyInfo info;
    final String TAG = getClass().getName();
    private TimeZone timeZone;

    private void setStartTime(java.util.Calendar calendar, int period) {
        final int[][] TIME = { {0, 0},
                {8, 0}, {9, 0}, {10, 0}, {11, 0},
                {13, 45}, {14, 45}, {15, 45}, {16, 45},
                {18, 30}, {19, 30}, {20, 30}, {21, 30}
        };
        calendar.set(java.util.Calendar.HOUR_OF_DAY, TIME[period][0]);
        calendar.set(java.util.Calendar.MINUTE, TIME[period][1]);
        calendar.set(java.util.Calendar.SECOND, 0);
    }

    private void setEndTime(java.util.Calendar calendar, int period) {
        final int[][] TIME = { {0, 0},
                {8, 45}, {9, 45}, {10, 45}, {11, 45},
                {14, 30}, {15, 30}, {16, 30}, {17, 30},
                {19, 15}, {20, 15}, {21, 15}, {22, 15}
        };
        calendar.set(java.util.Calendar.HOUR_OF_DAY, TIME[period][0]);
        calendar.set(java.util.Calendar.MINUTE, TIME[period][1]);
        calendar.set(java.util.Calendar.SECOND, 0);
    }

    public IcalUtil(MyInfo i) {
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());
        timeZone = TimeZoneRegistryFactory.getInstance().createRegistry()
                .getTimeZone("Asia/Shanghai");
        info = i;
        String startTimeString = i.getStartTime();
        setTermStartDate(startTimeString);
    }

    private void setTermStartDate(String startString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        sdf.setTimeZone(timeZone);
        long startTime = 0;
        try {
            startTime = sdf.parse(startString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        termStartDate = new Date(startTime);
        Log.d(TAG, "setStartDate: startDate=" + termStartDate);
    }

    public String serializeIcal(List<MySubject> subjects) {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//HITSchedule//iCal4j 2.4.6//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);

        VTimeZone vTimeZone = timeZone.getVTimeZone();
        calendar.getComponents().add(vTimeZone);

        for (MySubject subject : subjects) {
            VEvent vEvent = new VEvent();

            // 添加主题
            String summaryString = subject.getName();
            if (subject.getTeacher().length() > 0) {
                summaryString += " by " + subject.getTeacher();
            }
            if (subject.getRoom().length() > 0) {
                summaryString += " at " + subject.getRoom();
            }
            vEvent.getProperties().add(new Summary(summaryString));

            // 添加描述
            vEvent.getProperties().add(new Description(subject.getInfo()));

            // 添加位置
            vEvent.getProperties().add(new Location(subject.getRoom()));

            // 添加开始和结束时间
            addDtStartAndDtEnd(vEvent, subject);

            // 添加重复
            if (subject.getWeekList().size() > 1)
            {
                DateList dateList = new DateList();
                List<Integer> weekList = subject.getWeekList();
                int day = subject.getDay();
                for (int index = 1; index < weekList.size(); ++index) {
                    java.util.Calendar subjectCalendar = java.util.Calendar.getInstance(timeZone);
                    subjectCalendar.setTime((java.util.Date) termStartDate);
                    int week = weekList.get(index);

                    subjectCalendar.add(java.util.Calendar.DATE, calcDateOffset(day, week));
                    dateList.add(new Date(subjectCalendar.getTime()));
                }
                vEvent.getProperties().add(new RDate(dateList));
            }

            calendar.getComponents().add(vEvent);
        }

        Log.d(TAG, "serializeIcal: " + calendar.toString());

        return null;
    }

    private void addDtStartAndDtEnd(VEvent vEvent, MySubject subject) {
        java.util.Calendar subjectCalendar = java.util.Calendar.getInstance(timeZone);
        subjectCalendar.setTime((java.util.Date) termStartDate);
        subjectCalendar.add(java.util.Calendar.DATE,
                subject.getDay() - 1 + (subject.getWeekList().get(0) - 1) * 7);
        java.util.Calendar startCalendar = (java.util.Calendar) subjectCalendar.clone();
        java.util.Calendar endCalendar = (java.util.Calendar) subjectCalendar.clone();
        setStartTime(startCalendar, subject.getStart());
        setEndTime(endCalendar, subject.getStart() + subject.getStep() - 1);
        vEvent.getProperties().add(new DtStart(new DateTime(startCalendar.getTime())));
        vEvent.getProperties().add(new DtEnd(new DateTime(endCalendar.getTime())));
    }

    private int calcDateOffset(int day, int week) {
        return day - 1 + (week - 1) * 7;
    }
}
