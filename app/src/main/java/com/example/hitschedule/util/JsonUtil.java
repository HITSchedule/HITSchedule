package com.example.hitschedule.util;

import android.util.Log;

import com.example.hitschedule.database.MySubject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    
    private static String TAG = JsonUtil.class.getName();
    
    public static List<MySubject> parseBksJson(ArrayList<String> jsonList,
                                               String xnxq, String usrId) {
        List<MySubject> bksSubjects = new ArrayList<>();
        try {
            for (int day = 1; day <= 7; ++day) {
                JSONObject json = new JSONObject(jsonList.get(day));
                if (!json.getBoolean("isSuccess")) {
                    throw new JSONException("isSuccess == false");
                }

                JSONArray module = json.getJSONArray("module");
                for (int i = 0; i < module.length(); ++i) {
                    JSONObject lesson = module.getJSONObject(i);
                    lesson.put("xqj", day);
                    MySubject mySubject = parseBksLesson(lesson, xnxq, usrId);
                    if (mySubject != null) {
                        bksSubjects.add(mySubject);
                    }
                }
            }
            return bksSubjects;
        } catch (JSONException e) {
            Log.e(TAG, "parseBksJson: JSON 解析异常");
            e.printStackTrace();
            return null;
        }
    }

    private static MySubject parseBksLesson(JSONObject lesson, String xnxq,
                                            String usrId) throws JSONException {
        MySubject mySubject = new MySubject();
        mySubject.setXnxq(xnxq);
        mySubject.setUsrId(usrId);
        mySubject.setName(lesson.getString("kcmc"));
        mySubject.setTeacher(lesson.getString("jsxm"));
        mySubject.setRoom(lesson.getString("cdmc"));
        mySubject.setDay(lesson.getInt("xqj"));

        // 解析上课节数
        String sksjms = lesson.getString("sksjms");
        if (sksjms.contains("9,10,11") || sksjms.contains("9-11")) {
            // 特殊情况: 晚上三节课
            mySubject.setStart(9);
            mySubject.setStep(3);
        } else if (sksjms.contains("1,2") || sksjms.contains("1-2")) {
            mySubject.setStart(1);
            mySubject.setStep(2);
        } else if (sksjms.contains("3,4") || sksjms.contains("3-4")) {
            mySubject.setStart(3);
            mySubject.setStep(2);
        } else if (sksjms.contains("5,6") || sksjms.contains("5-6")) {
            mySubject.setStart(5);
            mySubject.setStep(2);
        } else if (sksjms.contains("7,8") || sksjms.contains("7-8")) {
            mySubject.setStart(7);
            mySubject.setStep(2);
        } else if (sksjms.contains("9,10") || sksjms.contains("9-10")) {
            mySubject.setStart(9);
            mySubject.setStep(2);
        } else if (sksjms.contains("11,12") || sksjms.contains("11-12")) {
            mySubject.setStart(11);
            mySubject.setStep(2);
        } else if (sksjms.contains("12")) {
            // 特殊情况, 晚上第十二节课, 没遇到过, 先写上
            mySubject.setStart(12);
            mySubject.setStep(1);
        } else {
            Log.d(TAG, "parseBksLesson: 节数解析失败: " + lesson);
            return null;
        }

        // 解析周数
        List<Integer> weekList = new ArrayList<>();
        String qsjsz = lesson.getString("qsjsz");
        try {
            // 先把用逗号分隔的周数分开
            String[] weekStrings = qsjsz.split(",");
            for (String weekString : weekStrings) {
                List<Integer> smallWeekList = new ArrayList<>();

                // 判断单双周
                boolean isOdd = false, isEven = false;
                if (weekString.contains("单")) {
                    isOdd = true;
                    weekString = weekString.replace("单", "");
                } else if (weekString.contains("双")) {
                    isEven = true;
                    weekString = weekString.replace("双", "");
                }

                // 添加周数
                if (weekString.contains("-")) {
                    String[] startAndEndWeeks = weekString.split("-");
                    int startWeek = Integer.parseInt(startAndEndWeeks[0]);
                    int endWeek = Integer.parseInt(startAndEndWeeks[1]);
                    if (mySubject.getTeacher().contains("郭萍"))
                    {
                        Log.d(TAG, "parseBksLesson: C语言debug: " + lesson);
                        Log.d(TAG, "parseBksLesson: startweek: " + startWeek +
                                "; endweek: " + endWeek);
                    }
                    for (int i = startWeek; i <= endWeek; ++i) {
                        if (isEven && i % 2 == 1 || isOdd && i % 2 == 0) {
                            continue;
                        }
                        smallWeekList.add(i);
                    }
                } else {
                    smallWeekList.add(Integer.valueOf(weekString));
                }

                // 将小周数列表合并到总列表中
                weekList.addAll(smallWeekList);
            }

            mySubject.setWeekList(weekList);
        } catch (Exception e) {
            Log.d(TAG, "parseBksLesson: 周数解析失败: " + lesson);
            e.printStackTrace();
            return null;
        }

        // 设置课程Info
        String info = mySubject.getTeacher() + "[" + qsjsz + "周]";
        mySubject.setInfo(info);

        // 考试加标记
        if (lesson.getString("zylx").contains("考试")) {
            mySubject.setInfo("[考试]" + info);
            mySubject.setName("[考试]" + mySubject.getName());
        }

        return mySubject;
    }
}
