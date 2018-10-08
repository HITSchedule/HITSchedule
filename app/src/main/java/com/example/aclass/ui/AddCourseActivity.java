package com.example.aclass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.aclass.R;
import com.example.aclass.adapter.WeekChooseAdapter;
import com.example.aclass.database.MySubject;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AddCourseActivity extends AppCompatActivity {

    private String TAG = getClass().getName();

    private int start;
    private int day;
    private int end;

    private Toolbar toolbar;

    private EditText et_course_name;

    private EditText et_room;

    private EditText et_weeks;

    private EditText et_start;

    private EditText et_teacher;

    private EditText et_other;

    private List<Integer> weekList;
    private Button save;
    private String theDay;

    private List<String> days = new ArrayList<>();
    private List<Integer> starts = new ArrayList<>();
    private List<Integer> ends = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        start = getIntent().getIntExtra("start", 1);
        day = getIntent().getIntExtra("day", 1);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("添加课程");   //设置标题

        et_course_name = findViewById(R.id.et_course_name);
        et_room = findViewById(R.id.et_room);
        et_weeks = findViewById(R.id.et_weeks);
        et_start = findViewById(R.id.et_start);
        et_teacher = findViewById(R.id.et_teacher);
        et_other = findViewById(R.id.et_other);

        save = findViewById(R.id.save);

        et_weeks.setFocusable(false);
        et_start.setFocusable(false);

        weekList = new ArrayList<>();

        initList();

        theDay = day == 0 ? "一" : day == 1 ? "二" : day == 2 ? "三" : day == 3 ? "四" : day == 4 ? "五" : day == 5 ? "六" : day == 6 ? "日" : "天";

        start = start % 2 == 0 ? start - 1 : start;
        end = start + 1;
        et_start.setText("周" + theDay + " 第" + start + "-" + end + " 节");

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
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(AddCourseActivity.this.getCurrentFocus().getWindowToken(), 0);

                String mCurrentLanguage = Locale.getDefault().getLanguage();
                String title = mCurrentLanguage.equals("en") ? "Time" : "设置时间";
                OptionsPickerView pvNoLinkOptions = new OptionsPickerBuilder(AddCourseActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {

                        day = options1;
                        start = options2 + 1;
                        end = options3 + 1;
                        theDay = day == 0 ? "一" : day == 1 ? "二" : day == 2 ? "三" : day == 3 ? "四" : day == 4 ? "五" : day == 5 ? "六" : day == 6 ? "日" : "天";
                        et_start.setText("周" + theDay + " 第" + start + "-" + end + " 节");
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

                if(end < start){
                    Toast.makeText(AddCourseActivity.this, "节次选择不合理", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (weekList.isEmpty()){
                    Toast.makeText(AddCourseActivity.this, "周数不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                String teacher = et_teacher.getText().toString();
                String room = et_room.getText().toString();
                String name = et_course_name.getText().toString();
                String other = et_other.getText().toString();

                if(!teacher.isEmpty() && !room.isEmpty() && !name.isEmpty()){
                    // 该列不为空时，表示节有课
                    MySubject mySubject = new MySubject();
                    // 设置学期
                    mySubject.setTerm("学期");
                    // 周几，由第几列决定
                    mySubject.setDay(day + 1);
                    // 课程名  split分割的偶数位为课程名
                    mySubject.setName(name);
                    // 开始时间 由id决定
                    mySubject.setStart(start);
                    // 连上两节课
                    mySubject.setStep(end - start + 1);

                    Log.d(TAG, "onClick: " + other);

                    if(other.isEmpty()){
                        Log.d(TAG, "onClick: other is empty");
                        mySubject.setInfo("#" + teacher + weekList + "周`");
                    }else {
                        mySubject.setInfo("#" + teacher + weekList + "周`" + other);
                    }

                    mySubject.setRoom(room);
                    mySubject.setWeekList(weekList);
                    mySubject.save();
                    Log.d(TAG, "onClick: " + mySubject);

                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(AddCourseActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void initList() {
        String s[] = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        days = Arrays.asList(s);

        for(int i = 1; i < 13; i++){
            starts.add(i);
            ends.add(i);
        }
    }

}
