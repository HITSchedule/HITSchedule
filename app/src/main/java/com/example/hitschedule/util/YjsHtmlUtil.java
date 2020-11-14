package com.example.hitschedule.util;

/**
 * 网页解析封装工具类，用于获取解析后的课程
 */

import android.util.Log;

import com.example.hitschedule.database.MySubject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析获得的HTML的辅助类
 */
public class YjsHtmlUtil {

    private String TAG = getClass().getName();

    private String html;

    public YjsHtmlUtil(String html){
        //  将换行替换为方便识别的字符
        this.html = html.replace("</br>", "!!!!!!");
    }

    /**
     * 解析总课表
     * @return
     */
    public List<MySubject> getzkb(String xnxq, String usrId){
        Document doc = Jsoup.parse(html);

        Elements elements = doc.getElementsByClass("addlist_01");
        if (elements == null)
            return new ArrayList<>();
        Elements kb = elements.first().getElementsByTag("tr");
        List<MySubject> mySubjects = new ArrayList<>();
        for (int i = 1; i < 7; i++){

            // 获得列表的第i+1行

            //  获取到整个列表
            Element element = kb.get(i);

            Elements rows = element.getElementsByTag("td");
            //   周一到周日， 可能为空，即为没课
            for (int j = 2; j < 9; j++){
                Element subject = rows.get(j);
                String text = subject.text().replace("周]", "]周"); // 与之前本科的保持一致
                Log.d(TAG, "getzkb: " + text);
                text = text.replace("节", "节!!!!!!"); // 研究生课表每门课都是按[]节结束
                if(!text.isEmpty()){
                    String [] strings = text.split("!!!!!!");
                    Log.d(TAG, "getzkb: " + strings.length);
                    for(int k = 0; k < strings.length; k++){
                        if(strings[k].contains("考试")){
                            MySubject mySubject = getMyExam(i, j, strings[k]);
                            if(mySubject != null){
                                mySubjects.add(mySubject);
                            } else {
                                Log.d(TAG, "getzkb: 解析考试失败" + strings[k]);
                            }
                        } else {
                            String [] splits = strings[k].split("◇"); // 研究生课表根据◇分割不同的部分
                            if (splits.length == 2){
                                MySubject mySubject = getMySubject(i, j, splits[0], splits[1]);
                                if(mySubject != null){
                                    mySubjects.add(mySubject);
                                } else {
                                    Log.d(TAG, "getzkb: 解析考试失败" + strings[k]);
                                }
                            }else {
                                MySubject mySubject = getMySubject(i, j, splits[0], splits[1], splits[2]);
                                if(mySubject != null){
                                    mySubjects.add(mySubject);
                                } else {
                                    Log.d(TAG, "getzkb: 解析考试失败" + strings[k]);
                                }
                            }

                        }

                    }
                }
            }

        }

        for (MySubject subject : mySubjects){
            subject.setXnxq(xnxq);
            subject.setUsrId(usrId);
        }
        return mySubjects;
    }

    /**
     * 处理考试课表
     * @param i
     * @param j
     * @param text
     * @return
     */
    private MySubject getMyExam(int i, int j, String text){


        String [] splits = text.split("◇");

        MySubject mySubject = new MySubject();

        // 周几，由第几列决定
        mySubject.setDay(j-1);
        // 课程名  split分割的偶数位为课程名
        mySubject.setName(splits[0]);
        // 开始时间 由id决定
        mySubject.setStart(2*(i-1) + 1);
        // 连上两节课
        mySubject.setStep(2);

        mySubject.setInfo(splits[1]);

        // 使用[]来分割为3部分，分别为 教师姓名 上课周数 上课地点
        String [] infos = splits[1].split("[\\[\\]]");
        Log.d(TAG, "getMyExam: " + infos[1]);
        mySubject.setRoom("无");
        mySubject.setWeekList(getWeeks(infos[1], false, false));

        return mySubject;
    }


    /**
     * 处理只有一个老师上课的情况
     * @param i
     * @param j
     * @param course
     * @param info
     * @return
     */
    private MySubject getMySubject(int i, int j, String course, String info){

        MySubject mySubject = new MySubject();
        // 周几，由第几列决定
        mySubject.setDay(j-1);
        // 课程名  split分割的偶数位为课程名
        mySubject.setName(course);
        // 开始时间 由id决定
        mySubject.setStart(2*(i-1) + 1);
        // 连上两节课
        mySubject.setStep(2);

        mySubject.setInfo(info);


        boolean isEven = false;
        boolean isOdd = false;


        if (info.contains("双")){
            isEven = true;
            info = info.replace("双", "");
        }

        if (info.contains("单")){
            isOdd = true;
            info = info.replace("单", "");
        }

        // 使用[]来分割为3部分，分别为 教师姓名 上课周数 上课地点
        String [] infos = info.split("[\\[\\]]");


        mySubject.setTeacher(infos[0]);
        String room = infos[2].replace("周","");
        if (room.startsWith("[")){
            room = "暂无";
        }
        if (room.endsWith("节")) {
            room = room.split("\\[")[0];
        }
        mySubject.setRoom(room);
        mySubject.setWeekList(getWeeks(infos[1], isEven, isOdd));

        return mySubject;
    }


    // TODO  考虑将每个老师设为一门课程
    /**
     * 处理一门课有多个老师上课的情况
     * @param i
     * @param j
     * @param course
     * @param info
     * @param room
     * @return
     */
    private MySubject getMySubject(int i, int j, String course, String info, String room){
        MySubject mySubject = new MySubject();
        // 周几，由第几列决定
        mySubject.setDay(j-1);
        // 课程名  split分割的偶数位为课程名
        mySubject.setName(course);
        // 开始时间 由id决定
        mySubject.setStart(2*(i-1) + 1);
        // 连上两节课
        mySubject.setStep(2);

        mySubject.setInfo(info);


        // 使用[]来分割为N部分，每一部分是一个上课老师的信息
        String [] infos = info.split("周，");

        List<Integer> weekList = new ArrayList<>();
        for(String each : infos){

            boolean isDoubole = false;

            boolean isOdd = false;

            // 这里考虑replace之后就不再是同一个字符串了
            String s = each.replace("周", "");

            if (each.contains("双")){
                isDoubole = true;
                s = s.replace("双", "");
            }

            if (each.contains("单")){
                isOdd = true;
                info = info.replace("单", "");
            }

            String[] eaches = s.split("[\\[\\]]");

            mySubject.setTeacher(eaches[0]);
            Log.d(TAG, "getMySubject: each1" + eaches[1] + isDoubole + isOdd);
            weekList.addAll(getWeeks(eaches[1], isDoubole, isOdd));

            Log.d(TAG, "getzkb: mySubject" + mySubject);
        }

        if (room.startsWith("[")){
            room = "暂无";
        }
        if (room.endsWith("节")) {
            room = room.split("\\[")[0];
        }
        mySubject.setRoom(room);
        mySubject.setWeekList(weekList);
        return mySubject;
    }


    /**
     * 解析上课周数
     * @param s
     * @return
     */
    private List<Integer> getWeeks(String s, boolean isEven, boolean isOdd){
        List<Integer> weeks = new ArrayList<>();

        s = s.replace("周","");
        String[] w1 = s.split("，");
        for(String s1 : w1){
            String[] w2 = s1.split("-");
            if(w2.length > 1){
                int start = Integer.valueOf(w2[0]);
                int end = Integer.valueOf(w2[1]);
                for(int week = start; week <= end; week++){
                    if(isEven){
                        if (week % 2 == 0){
                            weeks.add(week);
                        }
                    } else if(isOdd){
                        if (week % 2 == 1){
                            weeks.add(week);
                        }
                    }else {
                        weeks.add(week);
                    }
                }
            }else {
                int week = Integer.valueOf(w2[0]);
                weeks.add(week);
            }
        }

        return weeks;
    }

    /**
     * 获取课表当前学期
     * @return
     */
    public String getStartTime(){
        Document doc = Jsoup.parse(html);

        Elements elements = doc.getElementsByClass("xfyq_top");
        Log.d(TAG, "getStartTime: " + elements.size());
        Elements title = elements.first().getElementsByTag("span");
        Log.d(TAG, "getStartTime: " + title.text());
        String s = title.text();
        return s.split("学期")[0];
    }

}