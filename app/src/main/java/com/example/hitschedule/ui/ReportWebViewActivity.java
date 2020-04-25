package com.example.hitschedule.ui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.hitschedule.R;

public class ReportWebViewActivity extends BaseActivity {
    private String pwd, usrId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        pwd = getIntent().getStringExtra("pwd");
        usrId = getIntent().getStringExtra("usrId");

        Toolbar toolbar = findViewById(R.id.empty_toobar);
        WebView webView = findViewById(R.id.webview);

        toolbar.setTitle(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // 允许 js alert 创建对话框. 这段js存储在了assets/report_redirect.html中了.
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(ReportWebViewActivity.this);
                //b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            private boolean firstHomePage = true;
            private boolean firstReportPage = true;
            @Override
            public void onPageFinished(final WebView view, String url) {
                // 如果是学工系统主页, 则判断是否有未读消息, 若没有, 则跳转到每日上报.
                // 每日上报有固定开放时间, 所以使用js模拟点击每日上报按钮.
                final String js = "function report_redirect(){ $.ajax({ url : \"/zhxy-xgzs/xg_mobile/xsHome/getWdxx\", type : \"POST\", async : false, dataType : \"json\", contentType : \"application/json\", success:function(result){ nomessage = true; if(result.isSuccess){ if(result.module.length0){ var items =result.module; for(var i=0;i<items.length;i++){ if(items[i].sfqzyd==\"1\"){ nomessage = false; break; } } } if (nomessage) { mrsb(); } } }, error : function(){ weui.topTips(\"获取新闻通知信息详情失败\"); } }); }";
                if (firstHomePage && url.contains("/xg_mobile/xsHome")) {
                    firstHomePage = false;
                    // post request
                    //view.loadUrl("file:///android_asset/report_redirect.html");
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl("javascript:"+js);
                            view.loadUrl("javascript:report_redirect()");
                        }
                    });
                }
                // 如果是登录页面, 则自动填充用户名和密码并登录
                if (url.contains("ids.hit.edu.cn/authserver/login?service=")) {
                    view.loadUrl("javascript:usernameInput=document.getElementById(\"mobileUsername\");"
                            + "passwordInput=document.getElementById(\"mobilePassword\");"
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
                            + "document.getElementById(\"rememberMe\").checked = true;"
                            // 如果不需要验证码, 就自动点击登录
                            + "if(document.getElementById(\"cpatchaDiv\").style.display==\"none\")" +
                                    "{document.getElementById(\"load\").click()}");
                }
                // 如果是每日上报页面, 则点击新增按钮
                if (firstReportPage && url.endsWith("/zhxy-xgzs/xg_mobile/xs/yqxx")) {
                    firstReportPage = false;
                    view.loadUrl("javascript:add()");
                }
            }
        });

        webView.loadUrl(url);
    }
}
