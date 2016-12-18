package com.heiliuer.fuda_zaoan.server.request;

import android.support.annotation.NonNull;

import com.heiliuer.fuda_zaoan.Constants;
import com.heiliuer.fuda_zaoan.server.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public class SignService {


    private static SignRests signRests;

    public static LinkedHashMap<String, Result> directSignForResult(String UserID, String UserKey) {

        LinkedHashMap<String, Result> results = new LinkedHashMap<>();


        Result result = getSessionCookiesResult();
        results.put(SignRests.KEY_getSessionCookiesRequest, result);

        if (result.isSuccess()) {
            Result loginRs = login(UserID, UserKey);
            results.put(SignRests.KEY_LOGIN, loginRs);

            if (loginRs.isSuccess()) {

                Result viewstateRs = getVIEWSTATE();
                results.put(SignRests.KEY_getVIEWSTATE, viewstateRs);

                if (viewstateRs.isSuccess()) {
                    String viewState = (String) viewstateRs.get_extra();

                    Result signRs = sign(viewState);
                    results.put(SignRests.KEY_sign, signRs);

                    if (signRs.isSuccess()) {

                        Result loginOutRs = loginOut();
                        results.put(SignRests.KEY_loginOut, loginOutRs);
                    }
                }
            }
        }

        return results;

    }

    @NonNull
    public static Result getSessionCookiesResult() {
        Call<ResponseBody> call = getSignRests().getSessionCookiesRequest();

        Result result;
        try {
            Response<ResponseBody> response = call.execute();
//            ResponseBody body = response.body();
            if (response.code() == 200) {
                result = Result.getSuccessResult("成功");
            } else {
                result = Result.getErrorResult("response code:" + response.raw().code());
            }

        } catch (IOException e) {
            e.printStackTrace();
            result = Result.getErrorResult("请求异常");
        }
        return result;
    }

    public static SignRests getSignRests() {
        if (signRests == null) {
            OkHttpClient client = getOkHttpClient();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://zao.fzu4.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            signRests = retrofit.create(SignRests.class);
        }
        return signRests;
    }

    public static Result loginOut() {
        Result rs;
        try {
            Call<Result> rsCall = getSignRests().loginOut();
            Response<Result>
                    rsResponse = rsCall.execute();
            rs = getResult(rsResponse);
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }

        return rs;
    }

    public static Result getVIEWSTATE() {
        Result rs;
        try {
            Call<ResponseBody> rsCall = getSignRests().getVIEWSTATE();
            Response<ResponseBody> rsResponse = rsCall.execute();
            Document doc = Jsoup.parse(rsResponse.body().string());

            Elements viewState = doc.select("[name=\"__VIEWSTATE\"]");
            String val = viewState.val();
            rs = new Result(true, "success");
            rs.set_extra(val);
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }
        return rs;
    }

    public static Result sign(String viewState) {
        Result rs;
        try {
            Call<ResponseBody> rsCall = getSignRests().sign("2.6", "1", viewState);
            Response<ResponseBody> rsResponse = rsCall.execute();
            rs = new Result(true, "success");
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }
        return rs;
    }

    public static Result login(String UserID, String UserKey) {
        Result rs;
        try {
            Call<Result> rsCall = getSignRests().login(UserID, UserKey, true);
            Response<Result> rsResponse = rsCall.execute();
            rs = getResult(rsResponse);
        } catch (IOException e) {
            e.printStackTrace();
            rs = Result.getErrorResult("请求异常");
        }

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
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("User-Agent", Constants.WEIXIN_USER_AGENT)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
        return new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(interceptor).addInterceptor(headerInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }
}
