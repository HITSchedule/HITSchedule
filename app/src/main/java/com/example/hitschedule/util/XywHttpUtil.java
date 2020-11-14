package com.example.hitschedule.util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;

import static com.example.hitschedule.util.Constant.ACCONUT_ERROR;
import static com.example.hitschedule.util.Constant.CAPTCHA_ERROR;
import static com.example.hitschedule.util.Constant.LOGIN_SUCCESS;

/**
 * HTTP网络请求封装工具类，用于登录，课表请求等
 */
public class XywHttpUtil {

    private static String TAG = XywHttpUtil.class.getName();
    private static List<Cookie> cookieStore = new ArrayList<>();

    private static String init_url = "";
    private static String captcha_url = "";
    private static String login_url = "";
    private static String query_url = "";


    public static void set_url(String usrId) {
        if (usrId.toUpperCase().contains("S") || usrId.toUpperCase().contains("B")){
            init_url = "http://gcourse.hit.edu.cn/";
            captcha_url = "http://gcourse.hit.edu.cn/captchaImage";
            login_url = "http://gcourse.hit.edu.cn/loginLdap";
            query_url = "http://gcourse.hit.edu.cn/kbgl/queryxskbxsy";
        } else {
            init_url = "http://jwts.hit.edu.cn/";
            captcha_url = "http://jwts.hit.edu.cn/captchaImage";
            login_url = "http://jwts.hit.edu.cn/loginLdap";
            query_url = "http://jwts.hit.edu.cn/kbcx/queryGrkb";
        }
    }

    /**
     * 在请求验证码之前，先请求一下该网址，将Cookie送过去再说
     */
    public static void set_cookie_before_login() throws IOException {
        OkHttpClient client = getHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(init_url)
                .build();
        Call call = client.newCall(request);
        call.execute();
    }

    /**
     * 获取验证码
     * @return
     * @throws IOException // SocketTimeoutException
     */
    public static Bitmap getCaptchaImage() throws IOException{
        set_cookie_before_login();
        OkHttpClient client = getHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(captcha_url)
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
     * 校园网登录jwts
     * @param usrId 学号
     * @param pwd 密码
     * @param captcha 验证码
     * @return 返回登录结果
     * @throws IOException
     */
    public static int jwc_login(String usrId, String pwd, String captcha) throws IOException {

        OkHttpClient client = getHttpClient();

        FormBody jwts_data = new FormBody.Builder()
                .add("usercode", usrId)
                .add("password", pwd)
                .add("code", captcha)
                .build();
        Request request = new Request.Builder()
                .url(login_url)
                .post(jwts_data)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        Response prior = response.priorResponse();


        if (prior != null && prior.header("location").equals("/")){
            // TODO 判断什么时候获取课表失败
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
    public static String kb_get(String xnxq) throws IOException {
        String url = "http://gcourse.hit.edu.cn/kbgl/queryxskbxsy";
        OkHttpClient client = getHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(query_url)
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

                        for(Cookie cookie : cookies){
                            if (cookie.name().contains("JSESSIONID")){
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
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
//                .connectionSpecs(Collections.singletonList(spec))
                .build();
        return client;
    }

}
