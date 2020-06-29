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
public class HtmlUtil {

    private String TAG = getClass().getName();

    private String html;

    public HtmlUtil(String html){
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
                String text = subject.text();
                Log.d(TAG, "getzkb: " + text);
                if(!text.isEmpty()){
                    String [] strings = text.split("!!!!!!");
                    for(int k = 0; k < strings.length;){

                        int num = 0;
                        // 找到下一个k应该增加的数，这样即便解析出现异常，也不耽误下一个课表的解析
                        for(int x = 1; x < strings.length; x++){
                            Pattern pattern = Pattern.compile("[0-9]*");
                            if(k+x == strings.length - 1){
                                num = x;
                                break;
                            }
                            Matcher isNum = pattern.matcher(strings[k+x].charAt(strings[x+k].length() -  1)+ "");
                            if (isNum.matches()) {
                                num = x;
                                break;
                            } else if (strings[k+x].endsWith("其他")){
                                num = x;
                                break;
                            } else if (strings[x+k].endsWith("线上教学") || strings[x+k].endsWith("线上考试")) {
                                num = x;
                                break;
                            }
                        }

                        if (strings[k].contains("体育")){
                            num = 2;
                        }

                        try{
                            // 如果含有研究生课程，替换为本科客户才能形式
                            if (strings[k].startsWith("[研]")){
                                for (int m = 0; m < num; m++){
                                    strings[k+m] = strings[k+m].replace("周]", "]周");
                                }
                            }

                            // 不好解析，略过
                            if(strings[k].contains("机械设计A")){
                                k = k + num + 1;
                            }  else if(strings[k].contains("考试")){
                                MySubject mySubject = getMyExam(i, j, strings[k], strings[k + 1]);
                                k += 2;
                                if(mySubject != null){
//                                    mySubject.save();
                                    mySubjects.add(mySubject);
                                } else {
                                    Log.d(TAG, "getzkb: 解析考试失败" + strings[k] + strings[k + 1]);
                                }
                            } else if(strings[k+1].contains("，[")){
                                Log.d(TAG, "getzkb: 存在一个老师，不同教室" + strings[k+1]);
                                List<MySubject> subjects = new ArrayList<>();
                                if(num == 1){
                                    subjects = getMySubject1(i, j, strings[k], strings[k + 1]);
                                    k = k + 2;
                                }else if (num == 2){
                                    subjects = getMySubject1(i, j, strings[k], strings[k + 1], strings[k + 2]);
                                    k = k + 3;
                                }

                                if(subjects.size() > 0){
                                    for(MySubject subject1 : subjects){
//                                        subject1.save();
                                        mySubjects.add(subject1);
                                    }
                                } else {
                                    Log.d(TAG, "getzkb: 解析课表失败，一个老师多个教室" + strings[k] + strings[k + 1]);
                                    k += num + 1;
                                }
                            } else if(strings[k + 1].endsWith("周") && !strings[k].contains("体育")){
                                // 这里要剔除体育课，因为体育课也可能没有教室
                                MySubject mySubject = getMySubject(i, j, strings[k], strings[k + 1], strings[k+2]);
                                k += 3;
                                if(mySubject != null){
//                                    mySubject.save();
                                    mySubjects.add(mySubject);
                                } else {
                                    Log.d(TAG, "getzkb: 解析课表失败, 一门课，多个老师授课" + strings[k] + strings[k + 1] + strings[k+2]);
                                }
                            } else {
                                MySubject mySubject = getMySubject(i, j, strings[k], strings[k + 1]);
                                k += 2;
                                if(mySubject != null){
//                                    mySubject.save();
                                    mySubjects.add(mySubject);
                                } else {
                                    Log.d(TAG, "getzkb: 解析课表失败，正常课表" + strings[k] + strings[k + 1]);
                                }
                            }

                        }catch (Exception ee){
                            Log.d(TAG, "getzkb失败，解析异常: " + ee.getMessage());
                            k = k + num + 1;
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
     * @param course
     * @param info
     * @return
     */
    private MySubject getMyExam(int i, int j, String course, String info){

        MySubject mySubject = new MySubject();

        // 周几，由第几列决定
        mySubject.setDay(j-1);
        // 课程名  split分割的偶数位为课程名
        mySubject.setName(course);
        // 开始时间 由id决定
        mySubject.setStart(2*(i-1) + 1);
        // 连上两节课
        mySubject.setStep(2);

        mySubject.setInfo(info.split("周")[0] + "周");

        // 使用[]来分割为3部分，分别为 教师姓名 上课周数 上课地点
        String [] infos = info.split("[\\[\\]]");

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

        int index = info.lastIndexOf("周");
//        mySubject.setInfo(info.split("周")[0] + "周");

        mySubject.setInfo(info.substring(0, index));

        boolean isEven = false;
        boolean isOdd = false;

        if (course.contains("体育")){
            Log.d(TAG, "getMySubject: " + info);
        }
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
        mySubject.setRoom(infos[2].replace("周",""));
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
        mySubject.setRoom(room);
        mySubject.setWeekList(weekList);
        return mySubject;
    }

    /**
     * 处理一门课一个老师，多个教室的情况,针对18级新生，青年公寓
     * @param i
     * @param j
     * @param course
     * @param info
     * @return
     */
    public List<MySubject> getMySubject1(int i, int j, String course, String info){

        // 使用[]来分割为N部分，每一部分是一个上课老师的信息
        String [] infos = info.split("，\\[");

        List<MySubject> mySubjects = new ArrayList<>();

        String teacher = info.split("\\[")[0];

        Log.d(TAG, "getMySubject1: teacher" + teacher);
        for(String each : infos){

            Log.d(TAG, "getMySubject1: each: " + each);
            String s = each;

            // 补全成一个正常格式
            if(!each.contains("[")){
                s = teacher + "[" + each;
            }

            // 借助解析一个老师的函数来进行
            MySubject mySubject = getMySubject(i, j, course, s);

            mySubjects.add(mySubject);
        }

        return mySubjects;
    }

    /**
     * 处理一门课一个老师，多个教室的情况,针对18级新生，青年公寓
     * @param i
     * @param j
     * @param course
     * @param info
     * @return
     */
    public List<MySubject> getMySubject1(int i, int j, String course, String info, String room){

        // 使用[]来分割为N部分，每一部分是一个上课老师的信息
        String [] infos = info.split("，\\[");

        List<MySubject> mySubjects = new ArrayList<>();

        String teacher = info.split("\\[")[0];

        Log.d(TAG, "getMySubject1: teacher" + teacher);
        for(String each : infos){

            Log.d(TAG, "getMySubject1: each: " + each);
            String s = each;

            // 补全成一个正常格式
            if(!each.contains("[")){
                s = teacher + "[" + each + room;
            } else {
                s = s + room;
            }

            Log.d(TAG, "getMySubject1: " + s);

            // 借助解析一个老师的函数来进行
            MySubject mySubject = getMySubject(i, j, course, s);

            mySubjects.add(mySubject);
        }

        return mySubjects;
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