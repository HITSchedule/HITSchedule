package com.example.hitschedule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.hitschedule.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        LinearLayout search_car = findViewById(R.id.search_car);
        LinearLayout search_date = findViewById(R.id.search_date);
        LinearLayout search_empty = findViewById(R.id.search_emptyclass);

        search_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent carIntent = new Intent(SearchActivity.this, WebViewActivity.class);
                carIntent.putExtra("title", "校车查询");
                carIntent.putExtra("url", "https://weixin.hit.edu.cn/app/xccx/xccxappbc");
                startActivity(carIntent);
            }
        });

        search_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dateIntent = new Intent(SearchActivity.this, WebViewActivity.class);
                dateIntent.putExtra("title", "校历查询");
                dateIntent.putExtra("url", "https://weixin.hit.edu.cn/app/xlxq/xlxqapp");
                startActivity(dateIntent);
            }
        });

        search_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emptyIntent = new Intent(SearchActivity.this, WebViewActivity.class);
                emptyIntent.putExtra("title", "空教室查询");
                emptyIntent.putExtra("url", "https://weixin.hit.edu.cn/app/kxjscx/kxjscxapp");
                startActivity(emptyIntent);
            }
        });
    }
}
