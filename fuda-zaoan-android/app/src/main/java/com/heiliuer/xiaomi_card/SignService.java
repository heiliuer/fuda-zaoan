package com.heiliuer.xiaomi_card;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public interface SignService {
    String KEY_LOGIN = "登录";
    String KEY_loginOut = "登出";
    String KEY_getSessionCookiesRequest = "预登录";
    String KEY_sign = "签到";

    @GET("App/?UUID=7856c84d")
    Call<ResponseBody> getSessionCookiesRequest();

    @GET("App/Default?Logout=true")
    Call<Result> loginOut();

    @FormUrlEncoded
    @POST("Login/")
    Call<Result> login(@Field("UserID") String UserID, @Field("UserKey") String UserKey, @Field("Force") boolean Force);

    @FormUrlEncoded
    @POST("App/Account/User/Sign/")
    Call<Result> sign(@Field("Second") String Second,
                      @Field("Wrong") String Wrong);
}
