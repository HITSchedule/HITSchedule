package com.example.hitschedule.ui;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.hitschedule.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");

        Toolbar toolbar = findViewById(R.id.empty_toobar);
        WebView webView = findViewById(R.id.webview);

        toolbar.setTitle(title);

        webView.loadUrl(url);

        webView.getSettings().setJavaScriptEnabled(true);

    }
}
