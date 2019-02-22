package com.example.hitschedule.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.hitschedule.R;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simulateDayNight(/* DAY */ 0);
        Element timetableView = new Element();
        timetableView.setTitle("TimetableView");
        timetableView.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zfman/TimetableView")));

        Element dialogplus = new Element();
        dialogplus.setTitle("Dialogplus");
        dialogplus.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/orhanobut/dialogplus")));

        Element android_about_page = new Element();
        android_about_page.setTitle("AboutPage");
        android_about_page.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/medyo/android-about-page")));

        Element webpage = new Element();
        webpage.setTitle("软件官网");
        webpage.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://hitschedule.bmob.site/")));

        Element help = new Element();
        help.setTitle("点此查看使用说明");
        help.setIntent(new Intent(AboutActivity.this, HelpActivity.class));

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("本软件仅供哈工大本科生进行课表相关操作使用，项目已开源。如果您在使用的过程中发现任何bug，或对本软件的发展有建议，请联系yuxiang.wei.cs@gmail.com。感谢您的支持。")
                .setImage(R.drawable.bg_black)
                .addItem(help)
                .addGitHub("Yuxiang-Wei/HITSchedule")
                .addEmail("Yuxiang.Wei.CS@gmail.com")
                .addItem(webpage)
                .addGroup("Thanks for")
                .addItem(timetableView)
                .addItem(dialogplus)
                .addItem(android_about_page)

                .create();

        setContentView(aboutPage);

    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);

//        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
//        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
//        copyRightsElement.setIconNightTint(android.R.color.white);
//        copyRightsElement.setGravity(Gravity.CENTER);
//        copyRightsElement.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
//            }
//        });
        return copyRightsElement;
    }

    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
