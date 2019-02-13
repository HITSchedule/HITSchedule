package com.example.hitschedule.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hitschedule.R;
import com.example.hitschedule.adapter.ChangeLogAdapter;
import com.example.hitschedule.adapter.SubjectAdapter;
import com.example.hitschedule.database.MyInfo;
import com.example.hitschedule.database.Info;
import com.example.hitschedule.database.MySubject;
import com.example.hitschedule.database.Subject;
import com.example.hitschedule.database.User;
import com.example.hitschedule.dialog.CustomCaptchaDialog;
import com.example.hitschedule.dialog.CustomDialog;
import com.example.hitschedule.util.DensityUtil;
import com.example.hitschedule.util.HtmlUtil;
import com.example.hitschedule.util.HttpUtil;
import com.example.hitschedule.util.ScreenUtil;
import com.example.hitschedule.util.Util;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.hitschedule.util.Constant.CAPTCHA_ERROR;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = getClass().getName();

    private Toolbar toolbar;

    // 课表控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;

    // 个人基本信息
    private String usrId;
    private String pwd;

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

    private Dialog progressDialog;
    private Dialog refreshDialog;
    private Dialog deleteDialog;
    private Dialog updateDialog;
    private CustomCaptchaDialog captchaDialog;
    private DialogPlus scheduleDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "d2ad693a0277f5fc81c6dc84a91ca08f");
        checkForLogin();
        chechForUpdate();
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
            Toast.makeText(MainActivity.this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
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
        mWeekView=findViewById(R.id.weekView);
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
                .showView();

        mTimetableView
//                .curWeek("2018-09-03 00:00:00")
//                .curTerm("大三下学期")
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
                .alpha(0.3f, 0.1f, 0.8f)
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
                                "当前为:周" + day  + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        toolbar.setTitle("第" + curWeek + "周");
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
//        LitePal.deleteAll(MyInfo.class);
//        LitePal.deleteAll(MySubject.class);
        final List<MyInfo> infos = LitePal.findAll(MyInfo.class);
        Log.d(TAG, "initData: info size=" + infos.size());
        if (infos.size() > 0){
            info = infos.get(0);
            Log.d(TAG, "initData: " + info.getXnxq());
            subjects = LitePal.where("usrId = ? and xnxq = ?",
                    usrId, info.getXnxq()).find(MySubject.class);
            for (MySubject subject : subjects){
                Log.d(TAG, "initData: " + subject.getName() + subject.getUsrId());
            }
            Log.d(TAG, "initData: subject size=" + subjects.size());
            if (subjects.size() > 0){
                updateTimeTable();
            } else {
                Log.d(TAG, "initData: get from bmob");
                showProgressDialog();
                getDataFromBmob();
            }

        } else {
            Log.d(TAG, "initData: dialog");
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
                            getDataFromBmob();
                        }
                    } else {
                        makeToast("获取信息失败,请检查网络后重试");
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
                    makeToast("获取课表失败,请检查网络连接");
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
                        makeToast("验证码错误");
                        Message msg = new Message();
                        msg.what = CAPTCHAVIEW;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                        return;
                    }

                    if (info == null){
                        makeToast("info is null");
                        return;
                    }

                    String html = HttpUtil.vpn_kb_post(info.getXnxq());
//                    String html = HttpUtil.vpn_kb_post_test(info.getXnxq(), usrId);
                    if (html != null){
                        // 捕获一下解析异常
                        try{
                            HtmlUtil util = new HtmlUtil(html);
                            List<MySubject> newSubjects = util.getzkb();
                            updateDateBase(newSubjects);
                        }catch (Exception e){
                            makeToast("获取课表失败");
                        }

                    } else {
                        makeToast("获取课表失败,请检查网络连接");
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
    private void updateDateBase(List<MySubject> newSubjects){

        newSubjects = Util.mergeSubject(newSubjects);
        subjects = LitePal.findAll(MySubject.class);
        List<MySubject> delete = new ArrayList<>();

        // 遍历新列表,替换重复项
        for (MySubject subject : newSubjects){
            subject.setUsrId(usrId);
            subject.setXnxq(info.getXnxq());
            subject.setType("JWTS");
            if (subjects.contains(subject)){
                Log.d(TAG, "updateDateBase: 替换" + subject.getName());
                int index = subjects.indexOf(subject);
                if (!subjects.get(index).getType().equals("SELF")){
                    subject.setObjectId(subjects.get(index).getObjectId());
                    subject._save();
                    delete.add(subject);
                }
            }else {
                Log.d(TAG, "updateDateBase: 保存" + subject.getName());
                Log.d(TAG, "updateDateBase: " + subject.save());
            }
        }

        // 删除原列表中重复部分
        subjects.removeAll(delete);

        // 遍历原列表中剩余部分,若非自定义项,直接移除
        for (final MySubject subject : subjects){
            if (!subject.getType().equals("SELF")){
                Log.d(TAG, "updateDateBase: 删除" + subject.getName());
                subject.delete();
                if (subject.getObjectId() != null){
                    Subject s = new Subject();
                    s.setObjectId(subject.getObjectId());
                    s.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                Log.d(TAG, "done: 删除成功" + subject.getName());
                            }
                        }
                    });
                }
            }
        }

        subjects = LitePal.findAll(MySubject.class);

        // 更新Bmob
        for (final MySubject subject : subjects){

            if (subject.getObjectId() == null){
                subject.toSubject().save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            subject.setObjectId(s);
                            subject.save();
                            Log.d(TAG, "updateDateBase done: 保存成功 " + subject.getName() + s);
                        } else {
                            Log.d(TAG, "updateDateBase done: 保存失败 " + subject.getName() + s);
                        }
                    }
                });
            }else {
                subject.toSubject().update(subject.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        Log.d(TAG, "updateDateBase done: 更新成功 " + subject.getName());
                    }
                });
            }
        }
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
        toolbar.setTitle("第" + mTimetableView.curWeek() + "周");   //设置标题
        toolbar.setSubtitleTextColor(Color.WHITE);  //设置副标题字体颜色
        setSupportActionBar(toolbar);   //必须使用

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.week_choose:
                        mWeekView.isShow(!mWeekView.isShowing());
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
                }
                return false;
            }
        });
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
                    .text(R.id.show_text, "是否要刷新课表?")
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
                                        makeToast("开始获取课表");
                                        getDataFromJwts();
                                    } else {
                                        if (e!=null){
                                            makeToast(e.getMessage());
                                        } else {
                                            makeToast("获取信息失败,请检查网络连接");
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
     * @param bitmap 验证码图片
     */
    private void showCapatchDialog(Bitmap bitmap){
        CustomCaptchaDialog.Builder builder = new CustomCaptchaDialog.Builder(MainActivity.this);
        captchaDialog = builder
                .style(R.style.Dialog)
//                            .heightDimenRes(R.dimen.dilog_identitychange_height)
//                            .heightDimenRes(R.dimen.dilog_identitychange_width)
                .cancelTouchout(false)
                .view(R.layout.dialog_captcha)
                .img(bitmap)
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
                .build();
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
                                showUpdateDialog();
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
    private void showUpdateDialog(){
        if (refreshDialog == null){
            CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
            updateDialog = builder
                    .style(R.style.fillet_dialog)
                    .heightdp(180)
                    .widthpx(getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(MainActivity.this, 50f))
                    .cancelTouchout(true)
                    .view(R.layout.dialog_select)
                    .text(R.id.show_text, "检测到新版本,是否更新")
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
                        .text(R.id.show_text, "是否删除" + subject.getName())
                        .addViewOnclick(R.id.btn_sure, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.hide();
                                subject.delete();
                                subject.toSubject().delete(subject.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        subjects = LitePal.findAll(MySubject.class);
                                        makeToast("删除成功");
                                        scheduleDialog.dismiss();
                                        updateTimeTable();
                                    }
                                });
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
    class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATEVIEW:
                    mTimetableView.source(subjects)
                            .curWeek(info.getStartTime())  // 设置课表开始时间
                            .updateView();
                    //设置周次选择属性
                    mWeekView.source(subjects)
                            .itemCount(info.getWeekNum())
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
}
