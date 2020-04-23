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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");

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
                b.setTitle("Alert");
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
        // 判断是否有未读消息, 若没有, 则跳转到每日上报
        webView.setWebViewClient(new WebViewClient() {
            private boolean firstLogin = true;
            @Override
            public void onPageFinished(final WebView view, String url) {
                final String js = "function report_redirect(){ $.ajax({ url : \"/zhxy-xgzs/xg_mobile/xsHome/getWdxx\", type : \"POST\", async : false, dataType : \"json\", contentType : \"application/json\", success:function(result){ nomessage = true; if(result.isSuccess){ if(result.module.length0){ var items =result.module; for(var i=0;i<items.length;i++){ if(items[i].sfqzyd==\"1\"){ nomessage = false; break; } } } if (nomessage) { window.location.href=\"/zhxy-xgzs/xg_mobile/xs/yqxx\"; } } }, error : function(){ weui.topTips(\"获取新闻通知信息详情失败\"); } }); }";
                if (firstLogin && url.contains("/xg_mobile/xsHome")) {
                    firstLogin = false;
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
            }
        });

        webView.loadUrl(url);
    }
}
