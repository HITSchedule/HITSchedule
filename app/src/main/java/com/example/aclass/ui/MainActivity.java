package com.example.aclass.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aclass.R;
import com.example.aclass.adapter.ChangeLogAdapter;
import com.example.aclass.adapter.SimpleAdapter;
import com.example.aclass.database.BmobInfo;
import com.example.aclass.database.Info;
import com.example.aclass.database.MySubject;
import com.example.aclass.database.User;
import com.example.aclass.util.HttpUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
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
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import okhttp3.FormBody;


public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getName();

    private boolean isLogin = false;  // 是否已经登录

    private String regex = "<input id=\"DSIDFormDataStr\" type=\"hidden\" name=\"FormDataStr\" value=\"([^ ]+)\">";  // 判断是否已经登录的正则

    private String relogin_token = "";

    private HttpUtil httpUtil;

    private String stuId;
    private String pwd;

    private LinearLayout layout;
    private TimetableView mTimetableView;
    private WeekView weekView;
    private Toolbar toolbar;

    private int cWeek = 1;

    private android.support.v4.app.DialogFragment dialogFragment;

    private List<MySubject> mySubjects = new ArrayList<>();


    //uiHandler在主线程中创建，所以自动绑定主线程
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            List<Info> infos = LitePal.findAll(Info.class);
            switch (msg.what){
                case 0:
                    List<MySubject> subjects = LitePal.findAll(MySubject.class);

                    Log.d(TAG, "handleMessage: info" + infos.get(0).getKbxq());
                    mTimetableView.source(subjects)
                            .curWeek(infos.get(0).getKbxq())  // 设置课表开始时间
                            .showView();

                    if (infos.get(0).getWeekCount() == null){
                        infos.get(0).setWeekCount("19");
                    }

                    //设置周次选择属性
                    weekView.source(subjects)
                            .itemCount(Integer.valueOf(infos.get(0).getWeekCount()))
                            .showView();

                    if(dialogFragment != null){
                        dialogFragment.dismiss();
                    }else {
                        Log.d(TAG, "handleMessage: no progress");
                    }
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "d2ad693a0277f5fc81c6dc84a91ca08f");

        // 允许在主线程发起网络请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        httpUtil = new HttpUtil();

        // 检查账户是否已登录
        checkLogin();

        initView();

        mySubjects = LitePal.findAll(MySubject.class);

        if(mySubjects != null && mySubjects.size() > 0){
            Message msg = new Message();
            msg.what = 0;
            msg.obj = mySubjects;
            uiHandler.sendMessage(msg);
        }else {
            dialogFragment = new CircleDialog.Builder()
                    .setProgressText("加载中...")
                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                    .show(getSupportFragmentManager());
            loadData(true);
        }
    }

    private void checkLogin() {
        List<User> users = LitePal.findAll(User.class);
        if(users.size() > 0){
            stuId = users.get(0).getStuId();
            pwd = users.get(0).getPwd();
        } else {
            Toast.makeText(MainActivity.this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 加载数据，先请求bmob， 再根据情况时候initData
     * @param init 是否执行init data
     */
    private void loadData(final boolean init){
        BmobQuery<BmobInfo> query = new BmobQuery<>();
        query.findObjects(new FindListener<BmobInfo>() {
            @Override
            public void done(List<BmobInfo> list, BmobException e) {
                if (e == null && list.size() > 0){
                    list.get(0).save("");
                    if(init){
                        initData();
                    }

                } else {
                    if(dialogFragment != null){
                        dialogFragment.dismiss();
                        Toast.makeText(MainActivity.this, "获取课表失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d(TAG, "handleMessage: no progress");
                    }
                    Log.d(TAG, "done: 加载错误" + e.toString());
                }
            }
        });
    }

    /**
     * 初始化数据,访问vpn + jwts获取数据
     */
    private void initData() {

        // 先清空
        mySubjects = LitePal.findAll(MySubject.class);

//        for(MySubject subject : subjects){
//            Log.d(TAG, "initData: " + subject);
//            if(!subject.getInfo().startsWith("#")){
//                subject.delete();
//            }
//        }

        // 登录vpn的表单
        final FormBody vpn_data = new FormBody.Builder()
                .add("tz_offset","480")
                .add("username", stuId)
                .add("password", pwd)
                .add("realm", "学生")
                .add("btnSubmit", "登录")
                .build();

        // 登录jwts的表单
        final FormBody jwts_data = new FormBody.Builder()
                .add("usercode", stuId)
                .add("password", pwd)
                .add("code","")
                .build();

        // 查看周课表的表单 暂时没用
        final FormBody kb_data = new FormBody.Builder()
                .add("xnxq", "2017-20183")
                .add("zc", "2")
                .build();

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        httpUtil.jwts_post(jwts_data);
                        String cookie = "JSESSIONID=" + httpUtil.JSESSIONID + "; clwz_blc_pst=" + httpUtil.clwz_blc_pst + ";";
                        httpUtil.kb_get("http://jwts.hit.edu.cn/kbcx/queryGrkb", cookie, uiHandler);
                    } catch (Exception e) {
                        if (e.getClass().equals(SocketTimeoutException.class)){
                            Log.d(TAG, "onCreate: jwts连接失败");
                            // 尝试走vpn
                            try {
                                String s = httpUtil.vpn_post( vpn_data);
                                // 查看vpn是否已登录，已登录就重新登录
                                Pattern p = Pattern.compile(regex);
                                Matcher m = p.matcher(s);
                                if(m.find()){
                                    relogin_token = m.group(1);
                                    Log.d(TAG, "run: FormDataStr= " + relogin_token);
                                    FormBody relogin_data = new FormBody.Builder()
                                            .add("btnContinue","继续会话")
                                            .add("FormDataStr", relogin_token)
                                            .build();
                                    httpUtil.vpn_reLogin(relogin_data);
                                }
                                httpUtil.vpn_jwts_post(jwts_data);
                                String cookie = "DSID=" + httpUtil.DSID;
                                httpUtil.kb_get("https://vpn.hit.edu.cn/kbcx/,DanaInfo=jwts.hit.edu.cn+queryGrkb", cookie, uiHandler);
//                        kb_post("https://vpn.hit.edu.cn/kbcx/,DanaInfo=jwts.hit.edu.cn+queryXszkb", kb_data);

                            } catch (Exception e1) {
                                if(dialogFragment != null){
                                    dialogFragment.dismiss();
                                    Toast.makeText(MainActivity.this, "获取课表失败，请检查网络", Toast.LENGTH_SHORT).show();
                                }else {
                                    Log.d(TAG, "handleMessage: no progress");
                                }
                                Log.d(TAG, "run vpn请求出问题: " + e1.getMessage());
                            }
                        }
                    }


                }
            }).start();

        } catch (Exception e) {
            if(dialogFragment != null){
                dialogFragment.dismiss();
                Toast.makeText(MainActivity.this, "获取课表失败，请检查网络", Toast.LENGTH_SHORT).show();
            }else {
                Log.d(TAG, "handleMessage: no progress");
            }
            Log.d(TAG, "run: error" + e.getMessage());
        }
    }

    /**
     * 初始化 各个控件
     */
    private void initView() {

        mTimetableView = findViewById(R.id.id_timetableView);
        weekView=findViewById(R.id.id_weekview);
        toolbar = findViewById(R.id.toolbar);
        layout = findViewById(R.id.layout);

        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String bg_path = preferences.getString("bgPath",null);

        if (bg_path != null){
            File file = new File(bg_path);
            Drawable drawable = null;
            if (file.exists()){
                drawable = Drawable.createFromPath(bg_path);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.setBackground(drawable);
                }
            }

        }




        /**
         * getRealMetrics - 屏幕的原始尺寸，即包含状态栏。
         * version >= 4.2.2
         */
        // 获取屏幕尺寸，做适配用
        DisplayMetrics metrics =new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        int height = metrics.heightPixels;


//        int itemHeight = height == 2560 ? 152 : 115;

        int itemHeight = height * 1380 / (1920 * 12);

        Log.d(TAG, "initView: 屏幕大小: height" + height);


        mTimetableView
                .curWeek("2018-09-03 00:00:00")
                .curTerm("大三下学期")
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
                .alpha(0.3f, 0.1f, 0.8f)
                .itemHeight(itemHeight)  // 设置每一项的高度，适配屏幕
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        //TODO  考虑要不要这么做
                        if(weekView.isShowing()){
                            weekView.isShow(false);
                        }
                        display(scheduleList);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(MainActivity.this,
                                "长按:周" + day  + ",第" + start + "节",
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
//                        Toast.makeText(MainActivity.this,
//                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
//                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                        intent.putExtra("start", start);
                        intent.putExtra("day", day);
                        Log.d(TAG, "onFlaglayoutClick: " + day);
                        Log.d(TAG, "onFlaglayoutClick: " + start);
                        startActivity(intent);
                    }
                })
                .isShowNotCurWeek(false)
                .showView();

        cWeek = mTimetableView.curWeek();

        //设置周次选择属性
        weekView.curWeek(mTimetableView.curWeek())
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
//                        onWeekLeftLayoutClicked();
                        //TODO  做一个显示的栏
                        Toast.makeText(MainActivity.this, "待开发", Toast.LENGTH_SHORT).show();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        // 在周次初始化之后再初始化toolbar
        setToolbar();

        showTime();
    }

    /**
     * 展示课表项，调用dialogplus，采用list式布局
     * @param scheduleList
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

        SimpleAdapter adapter = new SimpleAdapter(this, R.layout.layout_subject, list);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setMargin(110, 0, 110, 0)
                .setGravity(Gravity.CENTER)
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(final DialogPlus dialog, Object item, View view, final int position) {
                        Log.d(TAG, "onItemClick: ");
                        new CircleDialog.Builder()
                                .setTitle("删除课表")
                                .setText("是否删除" + list.get(position).getName())
                                .setPositive("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .setNegative("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        LitePal.deleteAll(MySubject.class, "name = ? and day = ? and start = ?",
                                                list.get(position).getName(),
                                                String.valueOf(list.get(position).getDay()),
                                                String.valueOf(list.get(position).getStart()));

                                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        // 刷新界面
                                        List<MySubject> mySubjects = LitePal.findAll(MySubject.class);

                                        if(mySubjects != null && mySubjects.size() > 0){
                                            Message msg = new Message();
                                            msg.what = 0;
                                            msg.obj = mySubjects;
                                            uiHandler.sendMessage(msg);
                                        }else {
                                            dialogFragment = new CircleDialog.Builder()
                                                    .setProgressText("加载中...")
                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                    .show(getSupportFragmentManager());
                                            loadData(true);
                                        }
                                    }
                                })
                                .show(getSupportFragmentManager());
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }




    /**
     * 设置Toolbar，各个按钮的点击功能
     */
    private void setToolbar() {
        toolbar.setTitle("第" + mTimetableView.curWeek() + "周");   //设置标题
        toolbar.setSubtitleTextColor(Color.WHITE);  //设置副标题字体颜色
        setSupportActionBar(toolbar);   //必须使用
        //添加左边图标点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //添加menu项点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.clear_user:
                        //  清除用户的同时，清除课表
                        LitePal.deleteAll(User.class);
                        LitePal.deleteAll(MySubject.class);
                        Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.about:
                        Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.refresh:
                        Log.d(TAG, "onMenuItemClick: 刷新");

                        new CircleDialog.Builder()
                                .setTitle("刷新课表")
                                .setText("是否要重新加载课表？")
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogFragment = new CircleDialog.Builder()
                                                .setProgressText("刷新中...")
                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                .show(getSupportFragmentManager());

                                        loadData(true);
                                    }
                                })
                                .setNegative("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .show(getSupportFragmentManager());


                        break;
                    case R.id.week_choose:
                        weekView.isShow(!weekView.isShowing());
                        break;
                    case R.id.update:
                        Intent intent2 = new Intent(MainActivity.this, UpdateActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.changelog:
                        ChangeLogAdapter adapter = new ChangeLogAdapter(MainActivity.this, R.layout.layout_change_log);
                        DialogPlus dialog = DialogPlus.newDialog(MainActivity.this)
                                .setMargin(0, 0, 0, 0)
                                .setGravity(Gravity.CENTER)
                                .setAdapter(adapter)
                                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                .create();
                        dialog.show();
                        break;
                    case R.id.setbg:
                        Intent intent3 = new Intent(MainActivity.this, SetBgActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;    //返回为true
            }
        });
    }






    //设置menu（右边图标）
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu); //解析menu布局文件到menu
        return true;
    }

    /**
     * 显示时间
     * 设置侧边栏构建监听，TimeSlideAdapter是控件实现的可显示时间的侧边栏
     */
    protected void showTime() {
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
     * 隐藏时间
     * 将侧边栏监听置Null后，会默认使用默认的构建方法，即不显示时间
     * 只修改了侧边栏的属性，所以只更新侧边栏即可（性能高），没有必要更新全部（性能低）
     */
    protected void hideTime() {
        mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
        mTimetableView.updateSlideView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    public static float getFontSize() {
        Configuration mCurConfig = new Configuration();
        Log.w("Font", "getFontSize(), Font size is " + mCurConfig.fontScale);
        return mCurConfig.fontScale;

    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();

        List<MySubject> mySubjects = LitePal.findAll(MySubject.class);

        if(mySubjects != null && mySubjects.size() > 0) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = mySubjects;
            uiHandler.sendMessage(msg);
        }
    }
}
