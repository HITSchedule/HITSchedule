package com.example.hitschedule.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查相关权限基类，适用于android 6.0 运行时权限
 * 使用方法：
 * 1.需要运行时权限的activity继承此类
 * 2.覆写三个回调方法
 */
public abstract class BaseCheckPermissionActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CODE_PERMISSON = 88; //权限请求码

    private boolean isNeedCheckPermission = true; //判断是否需要检测，防止无限弹框申请权限

    /**
     * 获取需要进行检测的权限数组
     */
    protected abstract String[] getNeedPermissions();

    /**
     * 权限授权成功
     */
    protected abstract void permissionGrantedSuccess();

    /**
     * 权限授权失败
     */
    protected abstract void permissionGrantedFail();

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheckPermission) {
            checkAllNeedPermissions();
        }
    }

    /**
     * 检查所有权限，无权限则开始申请相关权限
     */
    protected void checkAllNeedPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        List<String> needRequestPermissonList = getDeniedPermissions(getNeedPermissions());
        if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {

            ActivityCompat.requestPermissions(this, needRequestPermissonList.toArray(
                    new String[needRequestPermissonList.size()]), REQUEST_CODE_PERMISSON);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {

                needRequestPermissonList.add(permission);

            }

        }
        return needRequestPermissonList;
    }

    /**
     * 所有权限是否都已授权
     *
     * @return
     */
    protected boolean isGrantedAllPermission() {
        List<String> needRequestPermissonList = getDeniedPermissions(getNeedPermissions());
        return needRequestPermissonList.size() == 0;
    }

    /**
     * 权限授权结果回调
     *
     * @param requestCode
     * @param permissions
     * @param paramArrayOfInt
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] paramArrayOfInt) {
        if (requestCode == REQUEST_CODE_PERMISSON) {
            //如果存在未授权的权限，弹出对话框提醒用户再次开启
            if (!verifyPermissions(paramArrayOfInt)) {
                showTipsDialog();
                isNeedCheckPermission = false;
            }
        } else {
            permissionGrantedSuccess();
        }
    }

    /**
     * 检测权限授权结果
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示对话框
     */
    protected void showTipsDialog() {
        new AlertDialog.Builder(this).setTitle("提示信息").setMessage("VPN启动缺少必要"
                + "权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionGrantedFail();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                }).setCancelable(false).show();
    }


    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}