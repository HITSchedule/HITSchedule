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

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.Bmob;

import static com.example.hitschedule.util.Constant.ACCONUT_ERROR;


public class LoginActivity extends AppCompatActivity {

    private String TAG = getClass().getName();

    private EditText et_stu_id;
    private EditText et_pwd;

    private String usrId;
    private String pwd;

    private Button login;

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
                    progress_dialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
}
