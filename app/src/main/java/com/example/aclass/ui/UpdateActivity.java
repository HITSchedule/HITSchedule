package com.example.aclass.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aclass.R;
import com.example.aclass.database.BmobInfo;
import com.example.aclass.util.Util;
import com.mylhyl.circledialog.CircleDialog;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;


public class UpdateActivity extends AppCompatActivity {

    private String TAG = getClass().getName();

    private TextView tv_version;

    private Button update;

    private CircleDialog.Builder builder;
    private DialogFragment dialogFragment;

    private String newVersion;
    private String oldVersion;

    private String base_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Bmob.initialize(this, "d2ad693a0277f5fc81c6dc84a91ca08f");

        tv_version = findViewById(R.id.version);
        update = findViewById(R.id.update);
        builder = new CircleDialog.Builder();
        base_path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

        try {
            oldVersion = getVersionName().trim();
        } catch (Exception e) {
            oldVersion = "1.0.0";
            Log.d(TAG, "handleMessage: 获取旧版本失败" + e.getMessage());
        }

        tv_version.setText("v" + oldVersion);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<BmobInfo> query = new BmobQuery<>();
                query.findObjects(new FindListener<BmobInfo>() {
                    @Override
                    public void done(List<BmobInfo> list, BmobException e) {
                        if (e == null && list.size() > 0){
                            list.get(0).save("");
                            newVersion = list.get(0).getVersion().trim();

                            Log.d(TAG, "handleMessage: 新版本" + newVersion + " 旧版本 " + oldVersion);
                            String name = "v" + newVersion.replace("\\.", "_") + ".apk";
                            if(new Util().compareVersion(newVersion, oldVersion)){
                                File file = new File(base_path, name);
                                Toast.makeText(UpdateActivity.this, "检查到新版本", Toast.LENGTH_SHORT).show();
                                if (file.exists()){
//                                    Log.d(TAG, "done: 删除文件" + file.delete());
                                    install(file);
                                    Log.d(TAG, "done: 文件已存在" + file.getAbsolutePath());
                                } else {
                                    BmobFile bmobFile = new BmobFile(name,"",list.get(0).getDownloadUrl());
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
        });




    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }

    private void downloadFile(final BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        final File saveFile = new File(base_path, file.getFilename());

        builder.setCancelable(false).setCanceledOnTouchOutside(false)
//                .configDialog(Color.CYAN)
                .setTitle("下载")
                .setProgressText("已经下载")
                .setNegative("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        file.cancel();
                    }
                })
                .show(getSupportFragmentManager());


        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                Log.d(TAG, "onStart: 开始下载");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    Log.d(TAG,"下载成功,保存路径:"+savePath);
                    dialogFragment.dismiss();
                    builder = new CircleDialog.Builder();
                    dialogFragment = builder
                            .setTitle("下载完成")
                            .setProgress(100,100)
                            .setPositive("安装", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    install(saveFile);
                                }
                            })
                            .show(getSupportFragmentManager());
                }else{
                    Log.d(TAG,"下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(final Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
                dialogFragment = builder.setProgress(100, value).create();
            }

        });
    }

    private void install(File file){
        
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判断是否是AndroidN以及更高的版本

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {

            Log.d(TAG, "install: 安卓7.0");
            Uri contentUri = FileProvider.getUriForFile(UpdateActivity.this,"com.example.aclass.fileProvider",file);

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
