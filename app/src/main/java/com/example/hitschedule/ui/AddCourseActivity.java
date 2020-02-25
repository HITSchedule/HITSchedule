package com.example.hitschedule.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.hitschedule.R;
import com.example.hitschedule.adapter.WeekChooseAdapter;
import com.example.hitschedule.database.MyInfo;
import com.example.hitschedule.database.MySubject;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddCourseActivity extends AppCompatActivity {

    private String TAG = getClass().getName();
    private final String mCurrentLanguage = Locale.getDefault().getLanguage();

    private int start;
    private int day;
    private int end;

    private MySubject subject;

    private Toolbar toolbar;

    private EditText et_course_name;

    private EditText et_room;

    private EditText et_weeks;

    private EditText et_start;

    private EditText et_teacher;

    private EditText et_info;

    private List<Integer> weekList;
    private Button save;

    private List<String> days = new ArrayList<>();
    private List<Integer> starts = new ArrayList<>();
    private List<Integer> ends = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        subject = (MySubject) getIntent().getSerializableExtra("subject");
        // 获取已有的存储S
        if (subject.getName() != null){
            List<MySubject> subjects = LitePal.where("name = ? and day = ? and start = ?",
                    subject.getName(),
                    String.valueOf(subject.getDay()),
                    String.valueOf(subject.getStart())).find(MySubject.class);
            subject = subjects.get(0);
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_course);   //设置标题

        et_course_name = findViewById(R.id.et_course_name);
        et_room = findViewById(R.id.et_room);
        et_weeks = findViewById(R.id.et_weeks);
        et_start = findViewById(R.id.et_start);
        et_teacher = findViewById(R.id.et_teacher);
        et_info = findViewById(R.id.et_info);

        save = findViewById(R.id.save);

        et_weeks.setFocusable(false);
        et_start.setFocusable(false);

        weekList = new ArrayList<>();

        initList();

        initView();

        et_weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                /**
//                 * 点击空白位置 隐藏软键盘
//                 */
//                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                mInputMethodManager.hideSoftInputFromWindow(AddCourseActivity.this.getCurrentFocus().getWindowToken(), 0);
//

                final WeekChooseAdapter adapter = new WeekChooseAdapter(AddCourseActivity.this, R.layout.layout_week_choose, weekList);
                DialogPlus dialog = DialogPlus.newDialog(AddCourseActivity.this)
                        .setMargin(110, 0, 110, 0)
                        .setGravity(Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialog, View view) {
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel(DialogPlus dialog) {
                                Collections.sort(weekList);
                                et_weeks.setText(weekList.toString());
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        et_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //条件选择器
                /**
                 * 点击空白位置 隐藏软键盘
                 */
//                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                mInputMethodManager.hideSoftInputFromWindow(AddCourseActivity.this.getCurrentFocus().getWindowToken(), 0);

                String title = getString(R.string.set_time);
                OptionsPickerView pvNoLinkOptions = new OptionsPickerBuilder(AddCourseActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        day = options1;
                        start = options2 + 1;
                        end = options3 + 1;
                        et_start.setText(timeToString(day, start, end));
                    }
                })
                        .setTitleText(title)
                        .build();
                pvNoLinkOptions.setNPicker(days,starts,ends);
                pvNoLinkOptions.setSelectOptions(day, start - 1, end - 1);
                pvNoLinkOptions.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setClickable(false);
                if(end < start){
                    Toast.makeText(AddCourseActivity.this, R.string.wrong_period, Toast.LENGTH_SHORT).show();
                    save.setClickable(true);
                    return;
                }

                if (weekList.isEmpty()){
                    Toast.makeText(AddCourseActivity.this, R.string.no_empty_weeks, Toast.LENGTH_SHORT).show();
                    save.setClickable(true);
                    return;
                }

                String teacher = et_teacher.getText().toString();
                String room = et_room.getText().toString();
                String name = et_course_name.getText().toString();
                String other = et_info.getText().toString();

                if(!teacher.isEmpty() && !room.isEmpty() && !name.isEmpty()){
                    // 周几，由第几列决定
                    subject.setDay(day + 1);
                    // 课程名  split分割的偶数位为课程名
                    subject.setName(name);
                    // 开始时间 由id决定
                    subject.setStart(start);
                    // 连上两节课
                    subject.setStep(end - start + 1);

                    subject.setTeacher(teacher);

                    Log.d(TAG, "onClick: " + other);

                    if(other.isEmpty()){
                        Log.d(TAG, "onClick: other is empty");
                        subject.setInfo(teacher + weekList);
                    }else {
                        subject.setInfo(other);
                    }

                    subject.setRoom(room);
                    subject.setWeekList(weekList);
                    subject.setType("SELF");

                    Toast.makeText(getApplicationContext(), R.string.add_or_modify_succeeded, Toast.LENGTH_SHORT).show();
                    subject.save();
                    finish();

                } else {
                    Toast.makeText(AddCourseActivity.this, R.string.fill_complete_info, Toast.LENGTH_SHORT).show();
                    save.setClickable(true);
                }
            }
        });



    }

    // 上课星期数和节数转为字符串
    private String timeToString(int day, int start, int end)
    {
        String[] days_str = getResources().getStringArray(R.array.day);
        String text;
        text = getString(R.string.start_time_string, days_str[day], start, end);
        return text;
    }

    private void initView() {

        day = subject.getDay() - 1;

        if (subject.getStart() == 0){
            subject.setStart(1);
        }
        start = subject.getStart();
        start = start % 2 == 0 ? start - 1 : start;
        if (subject.getStep() == 0){
            subject.setStep(2);
        }
        end = start + subject.getStep() - 1;
        et_start.setText(timeToString(day, start, end));

        if (subject.getName() != null){
            et_course_name.setText(subject.getName());
        }

        if (subject.getTeacher() != null){
            et_teacher.setText(subject.getTeacher());
        }

        if(subject.getRoom() != null){
            et_room.setText(subject.getRoom());
        }

        if (subject.getInfo() != null){
            et_info.setText(subject.getInfo());
        }

        if (subject.getWeekList() != null){
            weekList = subject.getWeekList();
            et_weeks.setText(subject.getWeekList().toString());
        }
    }

    private void initList() {
        String s[] = getResources().getStringArray(R.array.day);

        days = Arrays.asList(s);

        for(int i = 1; i < 13; i++){
            starts.add(i);
            ends.add(i);
        }
    }

    private String getDay(int day){
        switch (day){
            case 0:
                return "一";
            case 1:
                return "二";
            case 2:
                return "三";
            case 3:
                return "四";
            case 4:
                return "五";
            case 5:
                return "六";
            default:
                return "日";
        }
    }

}
