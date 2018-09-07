package com.example.aclass.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aclass.R;
import com.example.aclass.database.MySubject;
import com.example.aclass.database.User;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.Bmob;
import okhttp3.Call;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class LoginActivity extends AppCompatActivity {

    private String TAG = getClass().getName();
    private EditText et_stu_id;
    private EditText et_pwd;

    private String stu_id;
    private String pwd;

    private Button login;

    private android.support.v4.app.DialogFragment dialogFragment;

    //uiHandler在主线程中创建，所以自动绑定主线程
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    String s = (String) msg.obj;
                    if(s.contains("用户名")){
                        if(dialogFragment != null){
                            dialogFragment.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, "用户名或密码无效,请重试", Toast.LENGTH_SHORT).show();
                    }else {
                        User user = new User();
                        user.setStuId(stu_id);
                        user.setPwd(pwd);
                        user.save();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bmob.initialize(this, "d2ad693a0277f5fc81c6dc84a91ca08f");

        et_stu_id = findViewById(R.id.stu_id);
        et_pwd = findViewById(R.id.pwd);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stu_id = et_stu_id.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();

                // 验证账号密码是否正确
                if(!stu_id.isEmpty() && !pwd.isEmpty()){
                    // 登录vpn的表单
                    final FormBody vpn_data = new FormBody.Builder()
                            .add("tz_offset","480")
                            .add("username", stu_id)
                            .add("password", pwd)
                            .add("realm", "学生")
                            .add("btnSubmit", "登录")
                            .build();

                    dialogFragment = new CircleDialog.Builder()
                            .setProgressText("登录中...")
                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                            .show(getSupportFragmentManager());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //TODO 找到一个更好的判断账号密码是否正确的方法
                                vpn_post("https://vpn.hit.edu.cn/dana-na/auth/url_default/login.cgi", vpn_data,1);
                                vpn_post("https://vpn.hit.edu.cn/dana-na/auth/url_default/login.cgi", vpn_data, 0);
                            } catch (Exception e) {
                                Log.d(TAG, "run: " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });
    }


    /**
     * 登录vpn 进行post请求
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public String vpn_post(String url, RequestBody body, int flag) throws Exception {

        OkHttpClient client = getClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        Log.d(TAG, "vpn_post: response" + response.headers());
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "vpn_post: " + string);
        }else {
            Log.d(TAG, "vpn_post: failed");
        }

        Message msg = new Message();
        msg.what = flag;
        msg.obj = string;
        uiHandler.sendMessage(msg);
        return string;
    }

    /**
     * 获取一个新的OkHttpClient
     * @return
     */
    private OkHttpClient getClient(){

        OkHttpClient client;
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .connectionSpecs(Collections.singletonList(spec))
                .build();
        return client;

    }
}
