package com.example.hitschedule.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.hitschedule.R;
import com.example.hitschedule.adapter.ChangeLogAdapter;
import com.example.hitschedule.adapter.OnDateBuildAdapterLocale;
import com.example.hitschedule.adapter.SubjectAdapter;
import com.example.hitschedule.database.Info;
import com.example.hitschedule.database.MyInfo;
import com.example.hitschedule.database.MySubject;
import com.example.hitschedule.database.Subject;
import com.example.hitschedule.database.User;
import com.example.hitschedule.dialog.CustomCaptchaDialog;
import com.example.hitschedule.dialog.CustomDialog;
import com.example.hitschedule.util.DensityUtil;
import com.example.hitschedule.util.HtmlUtil;
import com.example.hitschedule.util.HttpUtil;
import com.example.hitschedule.util.LocaleUtil;
import com.example.hitschedule.util.ScreenUtil;
import com.example.hitschedule.util.Util;
import com.example.hitschedule.view.MyScrollView;
import com.example.hitschedule.view.WeekView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.hitschedule.util.Constant.CAPTCHA_ERROR;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getName();

    private Toolbar toolbar;

    // 课表控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;
    private MyScrollView mScrollView;
    // 个人基本信息
    private String usrId;
    private String pwd;
    private String type;
    private String captcha;
    private Bitmap bitmap;

    // 课表信息
    private int cWeek = 1;

    // UI刷新
    private UIHandler handler = new UIHandler();

    private MyInfo info;
    private List<MySubject> subjects;
    private Schedule lastest_clicked_schedule;

    private final int UPDATEVIEW = 2112;
    private final int CAPTCHAVIEW = 2113;
    private final int TOAST = 2114;
    private final int CHANGE_WEEK = 2115;

    private Dialog progressDialog;
    private Dialog refreshDialog;
    private Dialog deleteDialog;
    private Dialog updateDialog;
    private CustomCaptchaDialog captchaDialog;
    private DialogPlus scheduleDialog;
    private CustomDialog languageDialog;
    private View localeDialogInstance;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate: " + getResources().getConfiguration().getLocales().get(0));
        setContentView(R.layout.activity_main);
        type = getIntent().getStringExtra("type");
        Bmob.initialize(this, "d2ad693a0277f5fc81c6dc84a91ca08f");
        chechForUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForLogin();
    }

    /**
     * 检查是否已登录，若用户未登录，调至登录界面
     */
    private void checkForLogin() {
        List<User> users = LitePal.findAll(User.class);
        Log.d(TAG, "checkForLogin: user size=" + users.size());
        if(users.size() > 0){
            usrId = users.get(0).getUsrId();
            pwd = users.get(0).getPwd();
            initView();
            initData();
        } else {
            Toast.makeText(MainActivity.this, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    /**
     * 初始化view,设置mWeekView和mTimetableView
     */
    private void initView() {
        mTimetableView = findViewById(R.id.timetableView);
        mWeekView = findViewById(R.id.weekView);
        toolbar = findViewById(R.id.m_toolbar);
        setBackground();
        int height = ScreenUtil.getScreenHeight(getWindowManager());
        int itemHeight = height * 1380 / (1920 * 12);
        Log.d(TAG, "initView: itemHeight=" + itemHeight);

        //设置周次选择属性
        mWeekView.curWeek(mTimetableView.curWeek())
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
//                        mTimetableView.curWeek(week).updateView();
                        mScrollView.setWeek(week);
                        cWeek = week;
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        //TODO  做一个显示的栏
                        Toast.makeText(MainActivity.this, "待开发", Toast.LENGTH_SHORT).show();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .hideLeftLayout() // 隐藏尚未实现的周次选择
                .attachContext(this)
                .showView();

        mTimetableView
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
                .alpha(0.4f, 0.4f, 0.8f)
                .itemHeight(itemHeight)  // 设置每一项的高度，适配屏幕
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        if(mWeekView.isShowing()){
                            mWeekView.isShow(false);
                        }
                        display(scheduleList);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(MainActivity.this,
                                getString(R.string.cur_class_time_format,
                                        getResources().getStringArray(R.array.day)[day - 1], start),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        toolbar.setTitle(getString(R.string.week_format, curWeek));
                    }
                })
                .callback(new ISchedule.OnScrollViewBuildListener() {
                    @Override
                    public View getScrollView(LayoutInflater mInflate) {
                        mScrollView = (MyScrollView) mInflate.inflate(R.layout.custom_myscrollview, null, false);
                        mScrollView.setTimeTableView(mTimetableView);
                        mScrollView.setWeekView(mWeekView);
                        mScrollView.setWeek(mTimetableView.curWeek()); // 将当前周传入
                        mScrollView.setHandler(handler); // 处理周数改变
                        return mScrollView;
                    }
                })
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        Intent addIntent = new Intent(MainActivity.this, AddCourseActivity.class);
                        MySubject subject = new MySubject();
                        subject.setDay(day + 1);
                        subject.setStart(start);
                        subject.setXnxq(info.getXnxq());
                        subject.setUsrId(usrId);
                        addIntent.putExtra("subject", subject);
                        Log.d(TAG, "onFlaglayoutClick: " + day);
                        Log.d(TAG, "onFlaglayoutClick: " + start);
                        startActivity(addIntent);
                    }
                })
                .callback(new OnDateBuildAdapterLocale().attachContext(this)) // 设置多语言日期栏
                .isShowNotCurWeek(false)
                .showView();
        setToolbar();
        showTime();
    }

    private void setBackground() {
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String bg_path = preferences.getString("bgPath",null);

        Log.d(TAG, "setBackground: " + bg_path);
        if (bg_path != null){
            File file = new File(bg_path);
            Drawable drawable = null;
            if (file.exists()){
                drawable = Drawable.createFromPath(bg_path);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mTimetableView.setBackground(drawable);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mTimetableView.setBackground(null);
            }
        }
    }


    /**
     * 加载Info项以及本地课表
     */
    private void initData() {
        final List<MyInfo> infos = LitePal.findAll(MyInfo.class);
        Log.d(TAG, "initData: info size=" + infos.size());
        if (infos.size() > 0){
            info = infos.get(0);
            Log.d(TAG, "initData: " + info.getXnxq());
            subjects = LitePal.where("usrId = ? and xnxq = ?",
                    usrId, info.getXnxq()).find(MySubject.class);
            if (subjects.size() > 0){
                updateTimeTable();
            } else {
                if (type != null){
                    showProgressDialog();
                    getDataFromJwts();
                    Log.d(TAG, "done: 首次登录");
                }else {
                    Log.d(TAG, "done: 非首次登录");
                }

            }

        } else {
            showProgressDialog();
            BmobQuery<Info> query = new BmobQuery<>();
            query.findObjects(new FindListener<Info>() {
                @Override
                public void done(List<Info> list, BmobException e) {
                    if (e == null && list.size() > 0){
                        Info bombInfo = list.get(0);
                        info = bombInfo.toMyInfo();
                        info.save();
                        subjects = LitePal.where("usrId = ? and xnxq = ?",
                                usrId, info.getXnxq()).find(MySubject.class);
                        if (subjects.size() > 0){
                            updateTimeTable();
                        } else {
                            if (type != null){
                                getDataFromJwts();
                                Log.d(TAG, "done: 首次登录");
                            }else {
                                Log.d(TAG, "done: 非首次登录");
                            }
                            
                        }
                    } else {
                        makeToast(getString(R.string.data_update_failed));
                    }
                }
            });
        }
    }

    /**
     * 从Bmob的数据库获取数据
     */
    private void getDataFromBmob(){
        BmobQuery<Subject> query = new BmobQuery<>();
        query.addWhereEqualTo("usrId", usrId);
        query.addWhereEqualTo("xnxq", info.getXnxq());
        query.findObjects(new FindListener<Subject>() {
            @Override
            public void done(List<Subject> list, BmobException e) {
                if(e == null){
                    if (list.size() > 0){
                        Log.d(TAG, "done: bmob size=" + list.size());
                        subjects = Util.subjects2MySubjects(list);
                        for(MySubject subject : subjects){
                            subject.save();
                        }
                        updateTimeTable();
                    }else {
                        getDataFromJwts();
                    }
                } else {
                    Log.d(TAG, "done: " + e.getMessage());
                    makeToast(getString(R.string.table_update_failed_check_connection));
                    hideProgressDialog();
                }
            }
        });
    }

    /**
     * 从Jwts抓取课表
     */
    private void getDataFromJwts(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil.vpn_login(usrId, pwd);
                    bitmap = HttpUtil.getCaptchaImage();
                    Message msg = new Message();
                    msg.what = CAPTCHAVIEW;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 获取课表
     */
    private void getkb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int code = HttpUtil.vpn_jwts_login(usrId, pwd, captcha);
                    if (code == CAPTCHA_ERROR){
                        makeToast(getString(R.string.wrong_captcha));
                        Message msg = new Message();
                        msg.what = CAPTCHAVIEW;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                        return;
                    }

                    if (info == null){
                        makeToast("初始信息为空，请联系维护人员检查");
                        return;
                    }

                    String html = HttpUtil.vpn_kb_post(info.getXnxq());

                    if (html != null){
                        // 捕获一下解析异常
                        try{
                            HtmlUtil util = new HtmlUtil(html);
                            List<MySubject> newSubjects = util.getzkb(info.getXnxq(), usrId);
                            subjects = newSubjects;
                            updateDataBase(newSubjects);
                        }catch (Exception e){
                            makeToast(getString(R.string.curriculum_update_failed));
                            Log.d(TAG, "run: 获取课表失败 Error" + e);
                        }

                    } else {
                        makeToast(getString(R.string.table_update_failed_check_connection));
                        hideProgressDialog();
                    }
                    updateTimeTable();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 更新数据库
     * @param newSubjects 新数据
     */
    private void updateDataBase(List<MySubject> newSubjects){

        newSubjects = Util.mergeSubject(newSubjects);
        subjects = LitePal.findAll(MySubject.class);
        List<MySubject> delete = new ArrayList<>();

        // 遍历新列表,替换重复项
        for (MySubject subject : newSubjects){
            subject.setUsrId(usrId);
            subject.setXnxq(info.getXnxq());
            subject.setType("JWTS");
            if (subjects.contains(subject)){
                Log.d(TAG, "updateDataBase: 替换" + subject.getName());
                int index = subjects.indexOf(subject);
                if (!subjects.get(index).getType().equals("SELF")){
                    subject.setObjectId(subjects.get(index).getObjectId());
                    subject._save();
                    delete.add(subject);
                } else {
                    if(subject.getName().equals(subject.getRoom()) || subject.getInfo().equals("周")){
                        subjects.get(index).setRoom(subject.getRoom());
                        subjects.get(index).setInfo(subject.getInfo());
                        subjects.get(index).save();
                    }
                }
            }else {
                subject.save();
                Log.d(TAG, "updateDataBase: 保存" + subject.getName());
            }
        }

        // 删除原列表中重复部分
        subjects.removeAll(delete);

        // 遍历原列表中剩余部分,若非自定义项,直接移除
        for (final MySubject subject : subjects){
            if (!subject.getType().equals("SELF")){
                Log.d(TAG, "updateDataBase: 删除" + subject.getName());
                subject.delete();
            }
        }

        subjects = LitePal.findAll(MySubject.class);

        makeToast(getString(R.string.update_succeeded));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (info != null){
            // 刷新界面
            initView();
            subjects = LitePal.findAll(MySubject.class);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateTimeTable();
        }
    }

    //设置menu（右边图标）
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu); //解析menu布局文件到menu
        return true;
    }


    /**
     * 设置toolbar
     */
    private void setToolbar() {
        toolbar.setTitle(getString(R.string.week_format, mTimetableView.curWeek()));   //设置标题
        toolbar.setSubtitleTextColor(Color.WHITE);  //设置副标题字体颜色
        setSupportActionBar(toolbar);   //必须使用

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.week_choose:
                        mWeekView.isShow(!mWeekView.isShowing());
                        break;
                    case R.id.language:
                        selectLanguage();
                        break;
                    case R.id.refresh:
                        // TODO 确定一下dialog的统一大小
                        showRefreshDialog();
                        break;
                    case R.id.setbg:
                        Intent setBgIntent = new Intent(MainActivity.this, SetBgActivity.class);
                        startActivity(setBgIntent);
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.update:
                        Intent updateIntent = new Intent(MainActivity.this, UpdateActivity.class);
                        startActivity(updateIntent);
                        break;
                    case R.id.logout:
                        //  清除用户的同时，清除课表
                        LitePal.deleteAll(User.class);
                        LitePal.deleteAll(MySubject.class);
                        Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        break;
                    case R.id.addCourse:
                        Intent addIntent = new Intent(MainActivity.this, AddCourseActivity.class);
                        MySubject subject = new MySubject();
                        subject.setDay(1);
                        subject.setStart(1);
                        subject.setXnxq(info.getXnxq());
                        subject.setUsrId(usrId);
                        addIntent.putExtra("subject", subject);
                        startActivity(addIntent);
                        break;
                    case R.id.changelog:
                        ChangeLogAdapter adapter = new ChangeLogAdapter(MainActivity.this, R.layout.layout_change_log);
                        DialogPlus dialog = DialogPlus.newDialog(MainActivity.this)
                                .setMargin(0, 0, 0, 0)
                                .setGravity(Gravity.CENTER)
                                .setAdapter(adapter)
                                .setExpanded(true)
                                .create();
                        dialog.show();
                        break;
                    case R.id.search:
                        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(searchIntent);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 语言选择dialog
     */
    private void selectLanguage() {
        Log.d(TAG, "selectLanguage: ");
        if (languageDialog == null) {
            CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
            languageDialog = builder
                    .style(R.style.fillet_dialog)
                    .heightpx(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .widthdp(280)
                    .cancelTouchout(true)
                    .view(R.layout.dialog_locale)
                    .addViewOnclick(R.id.btn_sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            languageDialog.hide();
                            RadioGroup languageGroup = languageDialog.getView()
                                    .findViewById(R.id.language_group);
                            Log.d(TAG, "onClick: LanguageGroup is " + languageGroup);
                            switch (languageGroup.getCheckedRadioButtonId()) {
                                case R.id.btn_default:
                                    LocaleUtil.saveLanguage(LocaleUtil.LOCALE_DEFAULT);
                                    break;
                                case R.id.btn_zh:
                                    LocaleUtil.saveLanguage(LocaleUtil.LOCALE_CHINESE);
                                    break;
                                case R.id.btn_en:
                                    LocaleUtil.saveLanguage(LocaleUtil.LOCALE_ENGLISH);
                                    break;
                            }
                            languageDialog.dismiss();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .addViewOnclick(R.id.btn_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            languageDialog.dismiss();
                        }
                    })
                    .build();
            languageDialog.show();

            // 设置默认选择当前语言
            Map languageMap = new HashMap<String, Integer>();
            RadioGroup languageRadioGroup = languageDialog.getView().findViewById(R.id.language_group);
            languageMap.put(LocaleUtil.LOCALE_DEFAULT, R.id.btn_default);
            languageMap.put(LocaleUtil.LOCALE_CHINESE, R.id.btn_zh);
            languageMap.put(LocaleUtil.LOCALE_ENGLISH, R.id.btn_en);
            String userLanguage = LocaleUtil.getUserLanguage(); // TODO implement LocalUtil
            int userLanguageButtonId = (Integer) languageMap.get(userLanguage);
            Log.d(TAG, "selectLanguage: default button is " + userLanguageButtonId);
            languageRadioGroup.check(userLanguageButtonId);
        } else {
            languageDialog.show();
        }
    }

    /**
     * 显示刷新dialog
     */
    private void showRefreshDialog() {
        if (refreshDialog == null){
            CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
            refreshDialog = builder
                    .style(R.style.fillet_dialog)
                    .heightdp(180)
                    .widthpx(getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(MainActivity.this, 50f))
                    .cancelTouchout(true)
                    .view(R.layout.dialog_select)
                    .text(R.id.show_text, getString(R.string.confirm_refresh_curriculum))
                    .addViewOnclick(R.id.btn_sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refreshDialog.hide();
                            showProgressDialog();
                            BmobQuery<Info> query = new BmobQuery<>();
                            query.findObjects(new FindListener<Info>() {
                                @Override
                                public void done(List<Info> list, BmobException e) {
                                    if (e == null && list.size() > 0){
                                        Info bombInfo = list.get(0);
                                        info = bombInfo.toMyInfo();
                                        LitePal.deleteAll(MyInfo.class);
                                        info.save();
                                        makeToast(getString(R.string.start_fetch_curriculum));
                                        getDataFromJwts();
                                    } else {
                                        if (e!=null){
                                            makeToast(e.getMessage());
                                        } else {
                                            makeToast(getString(R.string.table_update_failed_check_connection));
                                        }
                                    }
                                }
                            });
                        }
                    })
                    .addViewOnclick(R.id.btn_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refreshDialog.dismiss();
                        }
                    })
                    .build();
        }
        refreshDialog.show();
    }

    /**
     * 展示课表项，调用dialogplus，采用list式布局
     * @param scheduleList 课程列表
     */
    private void display(final List<Schedule> scheduleList) {
        if(scheduleList.size() < 1 ){
            return;
        }

        final List<Schedule> list = new ArrayList<>();
        for(Schedule schedule : scheduleList){
            if(schedule.getWeekList().contains(cWeek)){
                list.add(schedule);
            }
        }

        SubjectAdapter adapter = new SubjectAdapter(this,this, R.layout.layout_subject, list);
        scheduleDialog = DialogPlus.newDialog(this)
                .setMargin(110, 0, 110, 0)
                .setGravity(Gravity.CENTER)
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(final DialogPlus dialog, Object item, View view, final int position) {
                        lastest_clicked_schedule = (Schedule)item;
                        LinearLayout layoutEdit = view.findViewById(R.id.layout_edit);
                        if (layoutEdit.getVisibility() == View.GONE){
                            layoutEdit.setVisibility(View.VISIBLE);
                        } else {
                            layoutEdit.setVisibility(View.GONE);
                        }
                    }
                })
                .setExpanded(true)
                .create();
        scheduleDialog.show();
    }

    /**
     * 显示时间
     * 设置侧边栏构建监听，TimeSlideAdapter是控件实现的可显示时间的侧边栏
     */
    private void showTime() {
        String[] times = new String[]{
                "8:00", "9:45", "10:00", "11:45",
                "13:45", "15:30", "15:45", "17:30",
                "18:30", "20:15","20:30","22:15"
        };
        OnSlideBuildAdapter listener= (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(times)
                .setTimeTextColor(Color.BLACK);
        mTimetableView.updateSlideView();
    }

    /**
     * 刷新课表显示
     */
    private void updateTimeTable(){
        Message msg = new Message();
        msg.what = UPDATEVIEW;
        handler.sendMessage(msg);
    }

    /**
     * 显示progress refreshDialog
     */
    private void showProgressDialog(){
        Log.d(TAG, "showProgressDialog: ");
        if (progressDialog == null){
            CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
            progressDialog = builder
                    .style(R.style.fillet_dialog)
                    .heightdp(140)
                    .widthdp(280)
                    .cancelTouchout(true)
                    .view(R.layout.dialog_progress)
                    .build();
            progressDialog.show();
        } else {
            progressDialog.show();
        }
    }

    /**
     * 隐藏progress refreshDialog
     */
    private void hideProgressDialog(){
        if (progressDialog != null){
            progressDialog.hide();
        }
    }

    /**
     * 显示验证码 refreshDialog,供用户输入验证码,刷新课表时调用
     * @param bitmap1 验证码图片
     */
    private void showCapatchDialog(Bitmap bitmap1){
        Log.d(TAG, "showCapatchDialog: ");
        CustomCaptchaDialog.Builder builder = new CustomCaptchaDialog.Builder(MainActivity.this);
        captchaDialog = builder
                .style(R.style.Dialog)
//                            .heightDimenRes(R.dimen.dilog_identitychange_height)
//                            .heightDimenRes(R.dimen.dilog_identitychange_width)
                .cancelTouchout(false)
                .view(R.layout.dialog_captcha)
                .img(bitmap1)
                .addViewOnclick(R.id.btn_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = captchaDialog.getCaptcha();
                        if (!text.isEmpty() && text.length() == 4){
                            captcha = text.trim();
                            captchaDialog.dismiss();
                            showProgressDialog();
                            getkb();
                        }else {
                            Toast.makeText(MainActivity.this, "验证码格式有误", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addViewOnclick(R.id.captcha, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    bitmap = HttpUtil.getCaptchaImage();
                                    captchaDialog.setCaptcha(bitmap);
                                    captchaDialog.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                })
                .build();
        captchaDialog.show();
        captchaDialog.show();
    }

    /**
     * 弹出Toast
     * @param text 要显示的内容
     */
    private void makeToast(String text){
        Message msg = new Message();
        msg.what = TOAST;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    /**
     * 检查更新
     */
    private void chechForUpdate() {
        BmobQuery<Info> query = new BmobQuery<>();
        query.findObjects(new FindListener<Info>() {
            @Override
            public void done(List<Info> list, BmobException e) {
                if (e == null && list.size() > 0){
                    Info bombInfo = list.get(0);
                    try {
                        if(Util.compareVersion(bombInfo.getLatestVersion(), getVersionName().trim())){
                            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                            String version = preferences.getString("version",null);
                            if (version == null || !version.equals(bombInfo.getLatestVersion())){
                                showUpdateDialog(bombInfo);
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 显示更新dialog
     */
    private void showUpdateDialog(Info bmobInfo){
        if (updateDialog == null){
            CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
            String changelog = "";
            if (bmobInfo.getReserved1() != null){
                changelog = changelog + bmobInfo.getReserved1();
                Log.d(TAG, "showUpdateDialog:");
            }
            updateDialog = builder
                    .style(R.style.fillet_dialog)
                    .heightdp(220)
                    .widthpx(getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(MainActivity.this, 50f))
                    .cancelTouchout(true)
                    .view(R.layout.dialog_select)
                    .text(R.id.show_text, "检测到新版本,是否更新?\n\n " + changelog)
                    .addViewOnclick(R.id.btn_sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateDialog.hide();
                            Intent updateIntent = new Intent(MainActivity.this, UpdateActivity.class);
                            startActivity(updateIntent);
                        }
                    })
                    .addViewOnclick(R.id.btn_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateDialog.hide();
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("version", info.getLatestVersion());
                            editor.apply();
                        }
                    })
                    .build();
            updateDialog.show();
        }else {
            updateDialog.show();
        }
    }

    /**
     * 用于处理课表dialog中删除和修改按钮的点击事件
     * @param view 点击的View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete:
                Log.d(TAG, "onClick: 点击了删除按钮");
                List<MySubject> todelete = LitePal.where("name = ? and day = ? and start = ?",
                        lastest_clicked_schedule.getName(),
                        String.valueOf(lastest_clicked_schedule.getDay()),
                        String.valueOf(lastest_clicked_schedule.getStart())).find(MySubject.class);
                final MySubject subject = todelete.get(0);
                CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
                deleteDialog = builder
                        .style(R.style.fillet_dialog)
                        .heightdp(180)
                        .widthpx(getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(MainActivity.this, 50f))
                        .cancelTouchout(true)
                        .view(R.layout.dialog_select)
                        .text(R.id.show_text, getString(R.string.delete_confirm) + subject.getName())
                        .addViewOnclick(R.id.btn_sure, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.hide();
                                subject.delete();
                                subjects.remove(subject);
                                updateTimeTable();
                                makeToast(getString(R.string.delete_succeeded));
                            }
                        })
                        .addViewOnclick(R.id.btn_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.hide();
                            }
                        })
                        .build();
                deleteDialog.show();
                break;
            case R.id.edit:
                scheduleDialog.dismiss();
                Log.d(TAG, "onClick: 点击了编辑按钮");
                List<MySubject> todelete1 = LitePal.where("name = ? and day = ? and start = ?",
                        lastest_clicked_schedule.getName(),
                        String.valueOf(lastest_clicked_schedule.getDay()),
                        String.valueOf(lastest_clicked_schedule.getStart())).find(MySubject.class);
                MySubject subject1 = todelete1.get(0);
                Log.d(TAG, "onClick: is saved=" + subject1.isSaved());
                Intent addIntent = new Intent(MainActivity.this, AddCourseActivity.class);
                addIntent.putExtra("subject", subject1);
                startActivity(addIntent);
                break;
        }

    }

    @SuppressLint("HandlerLeak")
    public class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATEVIEW:
                    mTimetableView.source(subjects)
                            .curWeek(info.getStartTime())  // 设置课表开始时间
                            .updateView();
                    cWeek = mTimetableView.curWeek();
                    mScrollView.setWeek(cWeek);
                    //设置周次选择属性
                    mWeekView.source(subjects)
                            .itemCount(info.getWeekNum())
                            .curWeek(cWeek)
                            .showView();
                    hideProgressDialog();
                    break;
                case CAPTCHAVIEW:
                    // 加载验证码
                    Bitmap bitmap = (Bitmap) msg.obj;
                    hideProgressDialog();
                    showCapatchDialog(bitmap);
                    break;
                case TOAST:
                    String text = (String) msg.obj;
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                    break;
                case CHANGE_WEEK:
                    cWeek = msg.arg1;
                    break;
            }
        }
    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        return packInfo.versionName;
    }


    @Override
    protected void onDestroy() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        if (refreshDialog != null){
            refreshDialog.dismiss();
        }
        if (deleteDialog != null){
            deleteDialog.dismiss();
        }
        if (updateDialog != null){
            updateDialog.dismiss();
        }
        if (captchaDialog != null){
            captchaDialog.dismiss();
        }
        if (scheduleDialog != null){
            scheduleDialog.dismiss();
        }

        super.onDestroy();
    }

    public void setCWeek(int week){
        cWeek = week;
    }
}
