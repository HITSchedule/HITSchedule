package com.example.hitschedule.util;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 屏幕相关的工具类.
 */
public class ScreenUtil {

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 获取系统状态栏的高度
     *
     * @param activity activity
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int statusBarHeight = DensityUtil.dp2px(activity, 25);
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取真实屏幕的高度
     * @param manager
     * @return
     */
    public static int getScreenHeight(WindowManager manager){

        // getRealMetrics - 屏幕的原始尺寸，即包含状态栏。
        // version >= 4.2.2
        // 获取屏幕尺寸，做适配用
        DisplayMetrics metrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            manager.getDefaultDisplay().getRealMetrics(metrics);
        }
        return metrics.heightPixels;
    }
}