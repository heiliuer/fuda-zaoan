package com.heiliuer.xiaomi_card;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public class SignMain {


    public static LinkedHashMap<String, Result> sign(String UserID, String UserKey) {


        OkHttpClient client = getOkHttpClient();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://zao.fzu4.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SignService signService = retrofit.create(SignService.class);

        LinkedHashMap<String, Result> results = new LinkedHashMap<>();

        Call<ResponseBody> call = signService.getSessionCookiesRequest();

        try {
            Response<ResponseBody> response = call.execute();
//            ResponseBody body = response.body();
            if (response.code() == 200) {
                results.put(SignService.KEY_getSessionCookiesRequest, Result.getSuccessResult("成功"));
            } else {
                results.put(SignService.KEY_getSessionCookiesRequest, Result.getErrorResult("response code:" + response.raw().code()));
            }

        } catch (IOException e) {
            e.printStackTrace();
            results.put(SignService.KEY_getSessionCookiesRequest, Result.getErrorResult("请求异常"));
        }

        if (login(UserID, UserKey, signService, results).isSuccess()) {
            if (sign(signService, results).isSuccess()) {
                loginOut(signService, results);
            }
        }

        return results;

    }

    private static Result loginOut(SignService signService, LinkedHashMap<String, Result> results) {
        Result rs;
        try {
            Call<Result> rsCall = signService.loginOut();
            Response<Result>
                    rsResponse = rsCall.execute();
            rs = getResult(rsResponse);
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }
        results.put(SignService.KEY_loginOut, rs);
        return rs;
    }

    private static Result sign(SignService signService, LinkedHashMap<String, Result> results) {
        Result rs;
        try {
            Call<Result> rsCall = signService.sign("2.6", "1");
            Response<Result> rsResponse = rsCall.execute();
            rs = getResult(rsResponse);
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }
        results.put(SignService.KEY_sign, rs);
        return rs;
    }

    private static Result login(String UserID, String UserKey, SignService signService, LinkedHashMap<String, Result> results) {
        Result rs;
        try {
            Call<Result> rsCall = signService.login(UserID, UserKey, true);
            Response<Result> rsResponse = rsCall.execute();
            rs = getResult(rsResponse);
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }
        results.put(SignService.KEY_LOGIN, rs);
        return rs;
    }

    private static Result getResult(Response<Result> rsResponse) {
        Result rs;
        if (rsResponse.isSuccessful()) {
            rs = rsResponse.body();
        } else {
            ResponseBody tmpBody = rsResponse.raw().body();
            String string;
            try {
                string = tmpBody.string();
            } catch (IOException e) {
                e.printStackTrace();
                string = "未知错误";
            }
            rs = Result.getErrorResult(tmpBody == null ? ("response code:" + rsResponse.raw().code()) : string);
        }
        return rs;
    }

    private static OkHttpClient getOkHttpClient() {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }
}
