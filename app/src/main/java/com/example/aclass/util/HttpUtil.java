package com.example.aclass.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.aclass.database.MySubject;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;

public class HttpUtil {

    private String TAG = getClass().getName();

    public boolean isLogin = false;  // 是否已经登录

    public String DSID = "";  // 通过vpn访问需要的cookie 值
    public String JSESSIONID = ""; //通过校内网访问需要的cookie
    public String clwz_blc_pst = ""; //通过校内网访问需要的cookie
    public String DSLaunchURL = "";
    public String DSFirstAccess = "";
    public String DSLastAccess = "";


    /**
     * 登录vpn 进行post请求
     * @param body
     * @return
     * @throws Exception
     */
    public String vpn_post(RequestBody body) throws Exception {

        String url = "https://vpn.hit.edu.cn/dana-na/auth/url_default/login.cgi";
        OkHttpClient client = getClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "vpn_post: " + string);
        }else {
            Log.d(TAG, "vpn_post: failed");
        }
        return string;
    }

    /**
     * vpn已登录，重新登录
     * @param body
     * @return
     * @throws Exception
     */
    public String vpn_reLogin(RequestBody body) throws Exception {

        String url = "https://vpn.hit.edu.cn/dana-na/auth/url_default/login.cgi";

        OkHttpClient client = getClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "vpn_relogin: " + string);
        }else {
            Log.d(TAG, "vpn_relogin: failed");
        }
        return string;
    }

    /**
     * vpn登录之前，获取验证码
     * @param url
     * @param cookie
     * @return
     * @throws Exception
     */
    public Bitmap getCaptchaImage(String url, String cookie) throws Exception{

        OkHttpClient client = getHttpClient();

        Request request = new Request.Builder()
                .addHeader("Cookie",cookie)
                .get()
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        ResponseBody body = response.body();
        if(response.isSuccessful()){
            //获取流
            InputStream in = body.byteStream();
            //转化为bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }else {
            Log.d(TAG, "kb_get: failed");
        }

        return null; // TODO 改
    }
    /**
     * vpn登录jwts
     * @param body
     * @return
     * @throws Exception
     */
    public String vpn_jwts_post(RequestBody body) throws Exception {

        String url = "https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn+loginLdap";

        OkHttpClient client = getClient();
        Request request = new Request.Builder()
                .addHeader("Cookie","DSID=" + DSID + "; DSLaunchURL=" + DSLaunchURL)
                .addHeader("Referer","https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn+loginLdapQian")
                .url(url)
                .post(body)
                .build();

        Log.d(TAG, "vpn_jwts_post: DSID:" + DSID);
        Call call = client.newCall(request);

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "vpn_jwts_post: " + string);
        }else {
            Log.d(TAG, "vpn_jwts_post: failed");
        }
        return string;
    }

    /**
     * 校园网登录jwts
     * @param body
     * @return
     * @throws Exception
     */
    public String jwts_post(RequestBody body) throws Exception {
        String url = "http://jwts.hit.edu.cn/loginLdap";

        OkHttpClient client = getHttpClient();

        // 先get拿到cookie值

        Request request = new Request.Builder()
                .addHeader("Cookie","JSESSIONID=" + JSESSIONID + "; clwz_blc_pst=" + clwz_blc_pst + ";")
                .url(url)
                .post(body)
                .build();

        Request request1 = new Request.Builder()
                .url("http://jwts.hit.edu.cn/")
                .get()
                .build();

        Call call1 = client.newCall(request1);
        Call call = client.newCall(request);

        Response response1 = call1.execute();

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "jwts_post: " + string);
        }else {
            Log.d(TAG, "jwts_post: failed");
        }
        return string;
    }


    /**
     * 获取周课表
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public String kb_post(String url, RequestBody body, Handler uiHandler) throws Exception {
        OkHttpClient client = getClient();
        Request request = new Request.Builder()
                .addHeader("Cookie","DSID=" + DSID)
                .post(body)
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "kb_post: " + string);
        }else {
            Log.d(TAG, "kb_post: failed");
        }

        HtmlUtil util = new HtmlUtil(string);
        List<MySubject> mySubjects = util.getzhkb();

        Message msg = new Message();
        msg.what = 0;
        msg.obj = mySubjects;
        uiHandler.sendMessage(msg);
        return string;
    }


    /**
     * 在使用vpn登录jwts请求验证码之前，先请求一下该网址，将Cookie送过去再说
     */
    public void get_cookie_before_login(){
        OkHttpClient client = getHttpClient();
        long x = Integer.valueOf(DSFirstAccess);
        x = x + 1;
        String cookie = "DSSignInURL=/; DSID=" +DSID + "; DSFirstAccess=" + DSFirstAccess + "; DSLastAccess=" + x;

        Request request = new Request.Builder()
                .addHeader("Cookie",cookie)
                .get()
                .url("https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn,SSO=U+")
                .build();

        Call call = client.newCall(request);

        Response response = null;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            if(response.isSuccessful()){

            }else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总课表
     * @param url
     * @return
     * @throws Exception
     */
    public String kb_get(String url, String cookie, Handler uiHandler) throws Exception {
        OkHttpClient client = getHttpClient();
        if(cookie.contains("DSID")) {
            client = getClient();
        }

        Request request = new Request.Builder()
                .addHeader("Cookie",cookie)
                .get()
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "kb_get: " + string);
        }else {
            Log.d(TAG, "kb_get: failed");
        }

        HtmlUtil util = new HtmlUtil(string);
        List<MySubject> subjects = util.getzkb();
        List<MySubject> mySubjects = LitePal.findAll(MySubject.class);
//        List<MySubject> toDelete = new ArrayList<>();

        Log.d(TAG, "kb_get: " + subjects.size());
        Log.d(TAG, "kb_get: " + mySubjects.size());
        // 删除本地与最新加载的想重复的
        for (MySubject subject : mySubjects){
            if(subjects.contains(subject)){
                subject.delete();
                Log.d(TAG, "kb_get: 删除" + subject);
            }
        }
        Log.d(TAG, "kb_get: " + subjects.size());
        Log.d(TAG, "kb_get: " + mySubjects.size());
        //  保存最新的
        for (MySubject subject : subjects){
            subject.save();
            Log.d(TAG, "kb_get: 存储" + subject);
        }

        mySubjects = LitePal.findAll(MySubject.class);
        Log.d(TAG, "kb_get: " + mySubjects.size());

        String s = util.getStartTime();
        Log.d(TAG, "kb_get: 耗时 " + s);
        Message msg = new Message();
        msg.what = 0;
        msg.obj = subjects;
        uiHandler.sendMessage(msg);
        return string;
    }


    /**
     * 获取某一学期的课表
     * @param url
     * @return
     * @throws Exception
     */
    public String kb_post(String url, RequestBody body ,String cookie, Handler uiHandler) throws Exception {
        OkHttpClient client = getHttpClient();
        if(cookie.contains("DSID")) {
            client = getClient();
        }

        Request request = new Request.Builder()
                .addHeader("Cookie",cookie)
                .post(body)
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "kb_post: " + string);
        }else {
            Log.d(TAG, "kb_post: failed");
        }

        HtmlUtil util = new HtmlUtil(string);
        List<MySubject> subjects = util.getzkb();
        List<MySubject> mySubjects = LitePal.findAll(MySubject.class);
//        List<MySubject> toDelete = new ArrayList<>();

        Log.d(TAG, "kb_post: " + subjects.size());
        Log.d(TAG, "kb_post: " + mySubjects.size());
        // 删除本地与最新加载的想重复的
        for (MySubject subject : mySubjects){
            if(subjects.contains(subject)){
                subject.delete();
                Log.d(TAG, "kb_post: 删除" + subject);
            }
        }
        Log.d(TAG, "kb_post: " + subjects.size());
        Log.d(TAG, "kb_post: " + mySubjects.size());
        //  保存最新的
        for (MySubject subject : subjects){
            subject.save();
            Log.d(TAG, "kb_post: 存储" + subject);
        }

        mySubjects = LitePal.findAll(MySubject.class);
        Log.d(TAG, "kb_post: " + mySubjects.size());

        String s = util.getStartTime();
        Log.d(TAG, "kb_post: 耗时 " + s);
        Message msg = new Message();
        msg.what = 0;
        msg.obj = subjects;
        uiHandler.sendMessage(msg);
        return string;
    }


    /**
     * 获取一个新的OkHttpClient
     * @return
     */
    private OkHttpClient getClient(){

        final OkHttpClient client;
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
                        for(Cookie cookie:cookies){
                            if (cookie.name().equals("DSID")){
                                DSID = cookie.value();
                                isLogin = false;
                            }
                            if(cookie.name().equals("lastRealm") && ! isLogin){
                                isLogin = true;
                            }
                            if(cookie.name().equals("JSESSIONID")){
                                JSESSIONID = cookie.value();
                            }
                            if(cookie.name().equals("DSLaunchURL")){
                                DSLaunchURL = cookie.value();
                            }
                            if(cookie.name().equals("DSFirstAccess")){
                                DSFirstAccess = cookie.value();
                            }
                            if(cookie.name().equals("DSLastAccess")){
                                DSLastAccess = cookie.value();
                            }
                            if (cookie.name().equals("clwz_blc_pst")){
                                clwz_blc_pst = cookie.value();
                            }
                            Log.d(TAG, "saveFromResponse: cookie Name:"+cookie.name() + ":" + cookie.value());
                        }
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




    private OkHttpClient getHttpClient(){

        final OkHttpClient client;
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
                        for(Cookie cookie:cookies){
                            if(cookie.name().equals("lastRealm") && ! isLogin){
                                isLogin = true;
                            }
                            if(cookie.name().equals("JSESSIONID")){
                                JSESSIONID = cookie.value();
                            }
                            if(cookie.name().equals("DSLaunchURL")){
                                DSLaunchURL = cookie.value();
                            }
                            if(cookie.name().equals("DSFirstAccess")){
                                DSFirstAccess = cookie.value();
                            }
                            if(cookie.name().equals("DSLastAccess")){
                                DSLastAccess = cookie.value();
                            }
                            if (cookie.name().equals("clwz_blc_pst")){
                                clwz_blc_pst = cookie.value();
                            }
                            Log.d(TAG, "saveFromResponse: cookie Name:"+cookie.name() + ":" + cookie.value());
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        return client;

    }
}
