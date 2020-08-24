package com.example.hitschedule.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.hitschedule.R;
import com.example.hitschedule.database.User;
import com.example.hitschedule.dialog.CustomDialog;
import com.example.hitschedule.util.HttpUtil;

import cn.bmob.v3.Bmob;

import static com.example.hitschedule.util.Constant.ACCONUT_ERROR;


public class LoginActivity extends BaseActivity {

    private String TAG = getClass().getName();

    private EditText et_stu_id;
    private EditText et_pwd;

    private String usrId;
    private String pwd;

    private Button login;
    private Button webLogin;

    private final int ERROR = 1;
    private final int SUCCESS = 0;

    private Dialog progress_dialog;

    //uiHandler在主线程中创建，所以自动绑定主线程
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ERROR:
                    progress_dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "用户名或密码无效,请重试", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    User user = new User();
                    user.setUsrId(usrId);
                    user.setPwd(pwd);
                    user.save();
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                        progress_dialog.cancel();
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("type", "init");
                    startActivity(intent);
                    finish();
                    break;
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
        webLogin = findViewById(R.id.web_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usrId = et_stu_id.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                // 验证账号密码是否正确
                if(!usrId.isEmpty() && !pwd.isEmpty()){

                    progress_show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int code = HttpUtil.vpn_login(usrId, pwd);
                                if (code == ACCONUT_ERROR){
                                    Log.d(TAG, "run: 账号或密码错误");
                                    sendMessage(ERROR);
                                }else {
                                    sendMessage(SUCCESS);
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "run: exception" + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        webLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usrId = et_stu_id.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                Intent loginWebViewActivityIntent = new Intent(LoginActivity.this, LoginWebViewActivity.class);
                loginWebViewActivityIntent.putExtra("pwd", pwd);
                loginWebViewActivityIntent.putExtra("usrId", usrId);
                // 启动 ids 网页登录, 并等待回调.
                startActivityForResult(loginWebViewActivityIntent, 1);
                //startActivity(loginWebViewActivityIntent);
            }
        });
    }

    private void sendMessage(int code){
        Message msg = new Message();
        msg.what = code;
        uiHandler.sendMessage(msg);
    }

    private void progress_show(){
        if (progress_dialog == null){
            CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
            progress_dialog = builder
                    .style(R.style.fillet_dialog)
                    .heightdp(150)
                    .widthdp(280)
                    .cancelTouchout(true)
                    .view(R.layout.dialog_progress)
                    .build();
            progress_dialog.show();
        } else {
            progress_dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress_dialog != null){
            progress_dialog.dismiss();
        }
    }

    /**
     * 启动 web login 后的回调函数. 如果 success 字段为 true, 则登陆成功.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (data.getBooleanExtra("success", false)) {
                    sendMessage(SUCCESS);
                }
                break;
        }
    }
}
