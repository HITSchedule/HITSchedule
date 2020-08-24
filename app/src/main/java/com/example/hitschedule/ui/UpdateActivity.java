package com.example.hitschedule.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hitschedule.R;
import com.example.hitschedule.database.Info;
import com.example.hitschedule.dialog.CustomDialog;
import com.example.hitschedule.util.DensityUtil;
import com.example.hitschedule.util.Util;

import java.io.File;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;


public class UpdateActivity extends BaseActivity {

    private String TAG = getClass().getName();

    private TextView tv_version;
    private TextView website;

    private Button update;

    private Dialog dialog;
    private ProgressBar progressBar;
    private TextView dialog_text;
    private Button sure;
    private Button cancel;


    private String newVersion;
    private String oldVersion;

    private String base_path;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Bmob.initialize(this, "d2ad693a0277f5fc81c6dc84a91ca08f");

        tv_version = findViewById(R.id.version);
        website = findViewById(R.id.website);
        update = findViewById(R.id.update);
        base_path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

        try {
            oldVersion = getVersionName().trim();
        } catch (Exception e) {
            oldVersion = "1.0.0";
            Log.d(TAG, "handleMessage: 获取旧版本失败" + e.getMessage());
        }

        tv_version.setText("v" + oldVersion);

        CustomDialog.Builder builder = new CustomDialog.Builder(UpdateActivity.this);

        dialog = builder
                .style(R.style.fillet_dialog)
                .heightdp(180)
                .widthpx(getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 50f))
                .cancelTouchout(true)
                .view(R.layout.dialog_download)
                .build();

        progressBar = ((CustomDialog) dialog).getView().findViewById(R.id.progress_bar1);
        sure = ((CustomDialog) dialog).getView().findViewById(R.id.btn_sure);
        cancel = ((CustomDialog) dialog).getView().findViewById(R.id.btn_cancel);
        dialog_text = ((CustomDialog) dialog).getView().findViewById(R.id.dialog_text);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryUpdate();
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                intent.setData(Uri.parse("https://hitschedule.github.io/"));//为Intent设置数据
                startActivity(intent);//将Intent传递给Activity
            }
        });
    }

    private void queryUpdate() {
        BmobQuery<Info> query = new BmobQuery<>();
        query.findObjects(new FindListener<Info>() {
            @Override
            public void done(List<Info> list, BmobException e) {
                if (e == null && list.size() > 0){
//                            list.get(0).toMyInfo().save();
                    newVersion = list.get(0).getLatestVersion().trim();

                    Log.d(TAG, "handleMessage: 新版本" + newVersion + " 旧版本 " + oldVersion);
                    String name = "v" + newVersion.replace("\\.", "_") + ".apk";
                    if(Util.compareVersion(newVersion, oldVersion)){
                        File file = new File(base_path, name);
                        Toast.makeText(UpdateActivity.this, "检查到新版本", Toast.LENGTH_SHORT).show();
                        if (file.exists()){
//                                    Log.d(TAG, "done: 删除文件" + file.delete());
                            file.delete();
                            BmobFile bmobFile = new BmobFile(name,"",list.get(0).getApkUrl());
                            downloadFile(bmobFile);
//                            install(file);
                            Log.d(TAG, "done: 文件已存在" + file.getAbsolutePath());
                        } else {
                            BmobFile bmobFile = new BmobFile(name,"",list.get(0).getApkUrl());
                            downloadFile(bmobFile);
                        }
                    } else {
                        Toast.makeText(UpdateActivity.this, "当前版本已经是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "done: 加载错误" + e.toString());
                }
            }
        });
    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        return packInfo.versionName;
    }

    private void downloadFile(final BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        final File saveFile = new File(base_path, file.getFilename());

        dialog_text.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        dialog.show();

        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart: 开始下载");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if(e==null){
                    progressBar.setVisibility(View.GONE);
                    dialog_text.setVisibility(View.VISIBLE);
                    dialog_text.setText("下载完成是否安装");
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            install(saveFile);
                        }
                    });
                    Log.d(TAG,"下载成功,保存路径:"+savePath);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.hide();
                        }
                    });
                }else{
                    Log.d(TAG,"下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(final Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
                progressBar.setProgress(value);
                dialog.show();
            }

        });
    }

    private void install(File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            Log.d(TAG, "install: 安卓7.0");
            Uri contentUri = FileProvider.getUriForFile(UpdateActivity.this,"com.example.hitschedule.fileProvider",file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
            startActivity(intent);
        }else{
            Log.d(TAG, "install: 安卓6.0及以下");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(file.getAbsoluteFile()),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }

}
