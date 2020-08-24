package com.example.hitschedule.util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
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
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;

import static com.example.hitschedule.util.Constant.ACCONUT_ERROR;
import static com.example.hitschedule.util.Constant.CAPTCHA_ERROR;
import static com.example.hitschedule.util.Constant.LOGIN_SUCCESS;

/**
 * HTTP网络请求封装工具类，用于登录，课表请求等
 */
public class HttpUtil {

    private static String TAG = HttpUtil.class.getName();
    private static List<Cookie> cookieStore = new ArrayList<>();


    /**
     * 登录hit vpn
     * @param usrId
     * @param pwd
     * @return
     * @throws IOException
     */
    public static int vpn_login(String usrId, String pwd) throws IOException {
        String url = "https://vpn.hit.edu.cn/dana-na/auth/url_default/login.cgi";
        OkHttpClient client = getHttpClient();

        FormBody vpn_data = new FormBody.Builder()
                .add("tz_offset","540")
                .add("username", usrId)
                .add("password", pwd)
                .add("realm", "学生")
                .add("btnSubmit", "登录")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(vpn_data)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        Response prior = response.priorResponse();
        String html = response.body().string();
        if (prior.header("location").contains("p=user")){
            //  https://vpn.hit.edu.cn/dana-na/auth/url_default/welcome.cgi?p=user-confirm&id=
            // 此时表示账号已登录，此时需要重新登录
            Log.d(TAG, "vpn_login: 已登录，重新登录");
            vpn_relogin(html);
        }else if(prior.header("location").contains("p=f")){
            // https://vpn.hit.edu.cn/dana-na/auth/url_default/welcome.cgi?p=failed
            Log.d(TAG, "vpn_login: 账号或密码错误");
            return ACCONUT_ERROR;
        }else if(prior.header("location").contains("index")){
            Log.d(TAG, "vpn_login: 登录成功");
        }

        return LOGIN_SUCCESS;
    }

    /**
     * 当vpn已登录时，用于重新登录
     * @param html 含上次会话token的网页
     * @return
     * @throws IOException
     */
    private static int vpn_relogin(String html) throws IOException {

        String url = "https://vpn.hit.edu.cn/dana-na/auth/url_default/login.cgi";
        String regex = "<input id=\"DSIDFormDataStr\" type=\"hidden\" name=\"FormDataStr\" value=\"([^ ]+)\">";  // 判断是否已经登录的正则
        String relogin_token = "";
        // 获取已登录的token
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(html);
        if(m.find()){
            relogin_token = m.group(1);
            Log.d(TAG, "vpn_relogin: FormDataStr= " + relogin_token);
        }

        Log.d(TAG, "vpn_relogin: ");

        FormBody relogin_data = new FormBody.Builder()
                .add("btnContinue","继续会话")
                .add("FormDataStr", relogin_token)
                .build();

        OkHttpClient client = getHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(relogin_data)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        return LOGIN_SUCCESS;
    }

    /**
     * 在使用vpn登录jwts请求验证码之前，先请求一下该网址，将Cookie送过去再说
     */
    public static void set_cookie_before_login() throws IOException {
        OkHttpClient client = getHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn,SSO=U+")
                .build();
        Call call = client.newCall(request);
        call.execute();
    }

    /**
     * vpn登录之前，获取验证码
     * @return
     * @throws IOException // SocketTimeoutException
     */
    public static Bitmap getCaptchaImage() throws IOException{
        set_cookie_before_login();

        String url = "https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn+captchaImage";

        OkHttpClient client = getHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        ResponseBody body = response.body();
        if(response.isSuccessful()){
            //获取流
            InputStream in = body != null ? body.byteStream() : null;
            //转化为bitmap
            return BitmapFactory.decodeStream(in);
        }else {
            Log.d(TAG, "getCaptchaImage: failed");
        }

        return null; // TODO 改
    }

    /**
     * 经vpn登录jwts
     * @param usrId 学号
     * @param pwd 密码
     * @param captcha 验证码
     * @return 返回登录结果
     * @throws IOException
     */
    public static int vpn_jwts_login(String usrId, String pwd, String captcha) throws IOException {

        String url = "https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn+loginLdap";
//        String url = "https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn+login";
        OkHttpClient client = getHttpClient();

        FormBody jwts_data = new FormBody.Builder()
                .add("usercode", usrId)
                .add("password", pwd)
                .add("code", captcha)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(jwts_data)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        Response prior = response.priorResponse();

        if (prior != null && prior.header("location").equals("https://vpn.hit.edu.cn/,DanaInfo=jwts.hit.edu.cn+")){
            //  https://vpn.hit.edu.cn/dana-na/auth/url_default/welcome.cgi?p=user-confirm&id=
            // 此时表示账号已登录，此时需要重新登录
            Log.d(TAG, "vpn_kb_post: 验证码错误");
            return CAPTCHA_ERROR;
        }
        return LOGIN_SUCCESS;
    }

    /**
     * 获取某一学期的课表
     * @return
     * @throws IOException
     */
    public static String vpn_kb_post(String xnxq) throws IOException {
        String url = "https://vpn.hit.edu.cn/kbcx/,DanaInfo=jwts.hit.edu.cn+queryGrkb";
        OkHttpClient client = getHttpClient();

        FormBody kb_data = new FormBody.Builder()
                .add("xnxq", xnxq)
                .build();

        Request request = new Request.Builder()
                .post(kb_data)
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "vpn_kb_post: " + string);
        }else {
            Log.d(TAG, "vpn_kb_post: failed");
        }

        return string;
    }

    /**
     * 获取某一学期的课表
     * @return
     * @throws IOException
     */
    public static String vpn_kb_post_test(String xnxq, String xh) throws IOException {
        String url = "https://vpn.hit.edu.cn/jskbcx/,DanaInfo=jwts.hit.edu.cn+BzrqueryGrkbTy";
        OkHttpClient client = getHttpClient();

        FormBody kb_data = new FormBody.Builder()
                .add("xnxq", xnxq)
                .add("xh", xh)
                .build();

        Request request = new Request.Builder()
                .post(kb_data)
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        String string = response.body().string();
        if(response.isSuccessful()){
            Log.d(TAG, "vpn_kb_post: " + string);
        }else {
            Log.d(TAG, "vpn_kb_post: failed");
        }

        return string;
    }

    public static OkHttpClient getHttpClient(){
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
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

//                        cookieStore.addAll(cookies);
                        for(Cookie cookie : cookies){
                            if (cookie.name().contains("DSID")){
                                Log.d(TAG, "saveFromResponse: cookie size=" + cookies.size());
                                Log.d(TAG, "saveFromResponse: name=" + cookie.name() + ", value=" + cookie.value());
                                cookieStore.clear();
                                cookieStore.addAll(cookies);
                            }
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return cookieStore;
                    }
                })
                .connectionSpecs(Collections.singletonList(spec))
                .build();
        return client;
    }

    /**
     * 从微信平台获取星期x的json格式课表
     * @param usrId 学生学号
     * @param xnxq 学年学期
     * @param day 星期几
     * @return json格式的课表原始数据, 错误时返回 null
     * @throws IOException
     */
    public static String wechatBksKbPost(String usrId, String xnxq, int day) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        final String url = "https://weixin.hit.edu.cn/app/bkskbcx/kbcxapp/getBkskb";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("gxh", usrId);
            jsonBody.put("xqj", String.valueOf(day));
            jsonBody.put("xnxq", xnxq);
        } catch (JSONException e) {
            Log.e(TAG, "wechatKbPost: JSON 操作失败");
            e.printStackTrace();
        }

        final String key = "info";
        final String value = jsonBody.toString();
        Log.d(TAG, "wechatKbPost: value=" + value);

        RequestBody requestBody = new FormBody.Builder()
                .add(key, value)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        String jsonResponse = response.body().string();
        Log.d(TAG, "wechatKbPost: day=" + day + ", jsonResponse=" + jsonResponse);
        return jsonResponse;
    }
}
