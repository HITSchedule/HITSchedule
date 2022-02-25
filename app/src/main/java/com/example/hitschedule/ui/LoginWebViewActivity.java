package com.example.hitschedule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.Headers;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.hitschedule.R;

import java.util.HashMap;
import java.util.Map;

public class LoginWebViewActivity extends BaseActivity {
    private String pwd, usrId;
    private WebView webView;
    
    private final String TAG = "LoginWebViewActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_webview);

        String title = getString(R.string.login_title);
        String url = "https://ids.hit.edu.cn/authserver/logout?service=https%3A%2F%2Fids.hit.edu.cn%2Fauthserver%2Flogin";
        pwd = getIntent().getStringExtra("pwd");
        usrId = getIntent().getStringExtra("usrId");

        Toolbar toolbar = findViewById(R.id.empty_toobar);
        // 若点击返回按钮, 则回到登录界面
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationContentDescription("更改用户名和密码");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });
        webView = findViewById(R.id.webview);
        WebView.setWebContentsDebuggingEnabled(true);
        toolbar.setTitle(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            private boolean firstLogin = true;

            @Override
            public void onPageFinished(final WebView view, String url) {
                Log.d(LoginWebViewActivity.this.TAG, "onPageFinished: ");
                // 如果是登录页面, 则自动填充用户名和密码并登录
                if (url.contains("ids.hit.edu.cn/authserver/login")) {
                    String jsUrl = "javascript:usernameInput=document.getElementById(\"username\");"
                            + "passwordInput=document.getElementById(\"password\");"
                            + "usernameInput.readOnly = true;"
                            + "passwordInput.readOnly = true;"
                            + "usernameInput.value = \"" +
                            usrId.replace("\"", "\\\"")
                                    .replace("\'", "\\\'")
                                    .replace("\\", "\\\\")
                            + "\";"
                            + "passwordInput.value = \"" +
                            pwd.replace("\"", "\\\"")
                                    .replace("\'", "\\\'")
                                    .replace("\\", "\\\\")
                            + "\";"
                            // 保持一周登录
                            + "document.getElementById(\"rememberMe\").checked = true;";
                    if (firstLogin) {
                        firstLogin = false;
                        // 如果不需要验证码, 就自动点击登录
                        jsUrl += "if(document.getElementById(\"captchaDiv\").classList.contains(\"hide\"))"
                                +"{"
                                +"document.getElementById(\"login_submit\").click();"
                                +"}";
                    }
                    view.loadUrl(jsUrl);
                }
            }


            @Override
            public void onPageStarted(final WebView view, String url, Bitmap bitmap) {
                Log.d(TAG, "onPageStarted: ");
                // 如果是这个URL, 证明登录成功
                if (url.contains("ids.hit.edu.cn/personalInfo")) {
//                    Intent mainIntent = new Intent(LoginWebViewActivity.this, MainActivity.class);
//                    mainIntent.putExtra("type", "init");
//                    startActivity(mainIntent);
//                    LoginWebViewActivity.this.finish();
                    // 添加 返回给 LoginActivity 的值
                    Intent result = new Intent();
                    result.putExtra("success", true);
                    setResult(RESULT_OK, result);
                    LoginWebViewActivity.this.finish();
                }
            }
        });

        webView.loadUrl(url);
    }

    private void startLoginActivity() {
        Log.d(TAG, "startLoginActivity: ");
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("usrId", usrId);
        loginIntent.putExtra("pwd", pwd);
        startActivity(loginIntent);
        finish();
    }
}
