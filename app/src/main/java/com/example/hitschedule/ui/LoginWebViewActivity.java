package com.example.hitschedule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.hitschedule.R;

public class LoginWebViewActivity extends BaseActivity {
    private String pwd, usrId;
    private WebView webView;
    private Intent result = new Intent();
    
    private final String TAG = "LoginWebViewActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_webview);

        String title = getString(R.string.login_title);
        String url = "https://ids.hit.edu.cn/authserver/logout";
        pwd = getIntent().getStringExtra("pwd");
        usrId = getIntent().getStringExtra("usrId");

        Toolbar toolbar = findViewById(R.id.empty_toobar);
        // 若点击返回按钮, 则回到登录界面
        toolbar.setNavigationIcon(R.drawable.btn_left);
        toolbar.setNavigationContentDescription("更改用户名和密码");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });
        webView = findViewById(R.id.webview);

        toolbar.setTitle(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            private boolean firstLogin = true;

            @Override
            public void onPageFinished(final WebView view, String url) {
                Log.d(LoginWebViewActivity.this.TAG, "onPageFinished: ");
                // 如果是登录页面
                if (firstLogin && url.contains("ids.hit.edu.cn/authserver/login")) {
                    firstLogin = false;
                    // 填入用户名和密码, 并禁止修改.
                    view.loadUrl("javascript:usernameInput=document.getElementById(\"mobileUsername\");"
                            + "passwordInput=document.getElementById(\"mobilePassword\");" +
                            "usernameInput.value = \"1190501001\";"
//                            "passwordInput.disabled = true;" +
//                            "usernameInput.value = \"" +
//                            usrId.replace("\"", "\\\"")
//                                    .replace("\'", "\\\'")
//                                    .replace("\\", "\\\\")
//                            + "\";"
//                            + "passwordInput.value = \"" +
//                            pwd.replace("\"", "\\\"")
//                                    .replace("\'", "\\\'")
//                                    .replace("\\", "\\\\")
//                            + "\";"
                    );

//                    if (firstLogin) {
//                        firstLogin = false;
//                        view.loadUrl("javascript:if(document.getElementById(\"cpatchaDiv\").style.display==\"none\")" +
//                                "{document.getElementById(\"load\").click()}");
//                    }
                }
            }

            @Override
            public void onPageStarted(final WebView view, String url, Bitmap bitmap) {
                Log.d(TAG, "onPageStarted: ");
                if (url.contains("ids.hit.edu.cn/authserver/index.do")) {
//                    Intent mainIntent = new Intent(LoginWebViewActivity.this, MainActivity.class);
//                    mainIntent.putExtra("type", "init");
//                    startActivity(mainIntent);
//                    LoginWebViewActivity.this.finish();
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
