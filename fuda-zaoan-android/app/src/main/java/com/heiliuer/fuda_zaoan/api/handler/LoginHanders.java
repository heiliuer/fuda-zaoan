package com.heiliuer.fuda_zaoan.api.handler;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.heiliuer.fuda_zaoan.Constants;
import com.heiliuer.fuda_zaoan.app.MyApplication;
import com.heiliuer.fuda_zaoan.server.Result;
import com.heiliuer.fuda_zaoan.api.anotation.Handler;
import com.heiliuer.fuda_zaoan.api.anotation.Handlers;
import com.heiliuer.fuda_zaoan.server.request.SignService;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/18 0018.
 */

@Handlers
public class LoginHanders extends HandersBase {
    private static final String TAG = LoginHanders.class.getName();


    @Handler("/login")
    public WebResourceResponse handlerLogin(WebResourceRequest request) {

//        userid=22&userkey=ttt
        Map<String, String> queryMap = getQueryMap(request.getUrl().getQuery());

        String userid = queryMap.get("userid");
        String userkey = queryMap.get("userkey");

        HandlerResult result = new HandlerResult();


        Result rs = SignService.getSessionCookiesResult();

        if (rs.isSuccess()) {
            Result loginRs = SignService.login(userid, userkey);

            if (loginRs.isSuccess()) {
                result.setStatus(0).setMsg("success");
            } else {
                result.setStatus(-1).setMsg(loginRs.getMsg());
            }

        } else {
            result.setStatus(-1).setMsg(rs.getMsg());
        }

        MyApplication.editShare.putString(Constants.SHARE_KEY_STR_USER_KEY, userkey);
        MyApplication.editShare.putString(Constants.SHARE_KEY_STR_USER_ID, userid);
        MyApplication.editShare.commit();

        try {
            return getResponseFromResult(request,result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "handlerLogin: failed");
        return null;
    }


}
