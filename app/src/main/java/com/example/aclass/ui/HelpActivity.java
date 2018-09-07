package com.example.aclass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.aclass.R;

import cn.bmob.v3.b.V;

public class HelpActivity extends AppCompatActivity {

    private TextView title1;

    private TextView text11;
    private TextView text12;
    private TextView text13;
    private TextView text14;

    private TextView title2;

    private TextView title2_1;

    private TextView text2_11;
    private TextView text2_12;
    private TextView text2_13;

    private TextView title2_2;

    private TextView text2_21;
    private TextView text2_22;
    private TextView text2_23;

    private TextView title3;

    private TextView text31;
    private TextView text32;
    private TextView text33;

    private TextView title4;

    private TextView text41;
    private TextView text42;
    private TextView text43;

    private TextView title5;

    private TextView text51;
    private TextView text52;


    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("功能说明");   //设置标题

        title1 = findViewById(R.id.title1);
        text11 = findViewById(R.id.text11);
        text12 = findViewById(R.id.text12);
        text13 = findViewById(R.id.text13);
        text14 = findViewById(R.id.text14);
        title2 = findViewById(R.id.title2);
        title2_1 = findViewById(R.id.title2_1);
        text2_11 = findViewById(R.id.text2_11);
        text2_12 = findViewById(R.id.text2_12);
        text2_13 = findViewById(R.id.text2_13);
        title2_2 = findViewById(R.id.title2_2);
        text2_21 = findViewById(R.id.text2_21);
        text2_22 = findViewById(R.id.text2_22);
        text2_23 = findViewById(R.id.text2_23);
        title3 = findViewById(R.id.title3);
        text31 = findViewById(R.id.text31);
        text32 = findViewById(R.id.text32);
        text33 = findViewById(R.id.text33);
        title4 = findViewById(R.id.title4);
        text41 = findViewById(R.id.text41);
        text42 = findViewById(R.id.text42);
        text43 = findViewById(R.id.text43);

        title5 = findViewById(R.id.title5);
        text51 = findViewById(R.id.text51);
        text52 = findViewById(R.id.text52);


//        text1.setVisibility(View.GONE);
//        title2_1.setVisibility(View.GONE);
//        title2_2.setVisibility(View.GONE);
//        text2_1.setVisibility(View.GONE);
//        text2_2.setVisibility(View.GONE);
//        text3.setVisibility(View.GONE);
//        text4.setVisibility(View.GONE);

//        text2_11.setVisibility(View.GONE);
//        text2_12.setVisibility(View.GONE);
//        text2_13.setVisibility(View.GONE);
//        text2_21.setVisibility(View.GONE);
//        text2_22.setVisibility(View.GONE);
//        text2_23.setVisibility(View.GONE);


        title1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text11.getVisibility() == View.GONE){
                    text11.setVisibility(View.VISIBLE);
                    text12.setVisibility(View.VISIBLE);
                    text13.setVisibility(View.VISIBLE);
                    text14.setVisibility(View.VISIBLE);
                } else {
                    text11.setVisibility(View.GONE);
                    text12.setVisibility(View.GONE);
                    text13.setVisibility(View.GONE);
                    text14.setVisibility(View.GONE);
                }
            }
        });

        title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title2_1.getVisibility() == View.GONE){
                    title2_1.setVisibility(View.VISIBLE);
                    title2_2.setVisibility(View.VISIBLE);
//                    text2_1.setVisibility(View.VISIBLE);
//                    text2_2.setVisibility(View.VISIBLE);
                } else {
                    title2_1.setVisibility(View.GONE);
                    title2_2.setVisibility(View.GONE);
                    text2_11.setVisibility(View.GONE);
                    text2_12.setVisibility(View.GONE);
                    text2_13.setVisibility(View.GONE);
                    text2_21.setVisibility(View.GONE);
                    text2_22.setVisibility(View.GONE);
                    text2_23.setVisibility(View.GONE);
                }
            }
        });

        title2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text2_11.getVisibility() == View.GONE){
                    text2_11.setVisibility(View.VISIBLE);
                    text2_12.setVisibility(View.VISIBLE);
                    text2_13.setVisibility(View.VISIBLE);
                } else {
                    text2_11.setVisibility(View.GONE);
                    text2_12.setVisibility(View.GONE);
                    text2_13.setVisibility(View.GONE);
                }
            }
        });

        title2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text2_21.getVisibility() == View.GONE){
                    text2_21.setVisibility(View.VISIBLE);
                    text2_22.setVisibility(View.VISIBLE);
                    text2_23.setVisibility(View.VISIBLE);
                } else {
                    text2_21.setVisibility(View.GONE);
                    text2_22.setVisibility(View.GONE);
                    text2_23.setVisibility(View.GONE);
                }
            }
        });

        title3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text31.getVisibility() == View.GONE){
                    text31.setVisibility(View.VISIBLE);
                    text32.setVisibility(View.VISIBLE);
                    text33.setVisibility(View.VISIBLE);
                } else {
                    text31.setVisibility(View.GONE);
                    text32.setVisibility(View.GONE);
                    text33.setVisibility(View.GONE);
                }
            }
        });

        title4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text41.getVisibility() == View.GONE){
                    text41.setVisibility(View.VISIBLE);
                    text42.setVisibility(View.VISIBLE);
                    text43.setVisibility(View.VISIBLE);
                } else {
                    text41.setVisibility(View.GONE);
                    text42.setVisibility(View.GONE);
                    text43.setVisibility(View.GONE);
                }
            }
        });

        title5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text51.getVisibility() == View.GONE){
                    text51.setVisibility(View.VISIBLE);
                    text52.setVisibility(View.VISIBLE);
                } else {
                    text51.setVisibility(View.GONE);
                    text52.setVisibility(View.GONE);
                }
            }
        });


    }
}
