package com.heiliuer.fuda_zaoan.api.handler;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.heiliuer.fuda_zaoan.SignRecord;
import com.heiliuer.fuda_zaoan.SignUtils;
import com.heiliuer.fuda_zaoan.api.anotation.Handler;
import com.heiliuer.fuda_zaoan.api.anotation.Handlers;
import com.heiliuer.fuda_zaoan.app.MyApplication;
import com.heiliuer.fuda_zaoan.dao.SignRecordDao;
import com.heiliuer.fuda_zaoan.db.DbHandler;
import com.heiliuer.fuda_zaoan.server.Result;
import com.heiliuer.fuda_zaoan.server.request.SignRests;
import com.heiliuer.fuda_zaoan.server.request.SignService;
import com.heiliuer.fuda_zaoan.service.ServiceMain;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.AbstractDaoSession;

import static com.heiliuer.fuda_zaoan.Constants.SHARE_KEY_BOOL_AUTO_SIGN;
import static com.heiliuer.fuda_zaoan.server.request.SignService.getVIEWSTATE;

/**
 * Created by Administrator on 2016/12/18 0018.
 */

@Handlers
public class SignHanders extends HandersBase {
    private static final String TAG = SignHanders.class.getName();

    private final DbHandler dbHandler;


    public SignHanders() {
        dbHandler = MyApplication.getDbHandler();
    }

    @Handler("/datas")
    public WebResourceResponse records(WebResourceRequest request) {

//        year=2016&month=12
        Map<String, String> queryMap = getQueryMap(request.getUrl().getQuery());

        String year = queryMap.get("year");
        String month = queryMap.get("month");

        String dateKey = year + "-" + month;


        AbstractDaoSession daoSession = dbHandler.getSoftwareDao().getSession();
        List<SignRecord> signRecords = daoSession.queryBuilder(SignRecord.class).where(SignRecordDao.Properties.SignDate.like("%" + dateKey + "%")).list();

        HandlerResult result = new HandlerResult().setStatus(0);

        result.setData(ImmutableMap.of("records", signRecords));

        try {
            return getResponseFromResult(request,result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "handlerLogin: failed");
        return null;
    }


    @Handler("/sign")
    public WebResourceResponse sign(WebResourceRequest request) {

//        userid=22&userkey=ttt
        Map<String, String> queryMap = getQueryMap(request.getUrl().getQuery());

        String date = queryMap.get("date");

        HandlerResult result = new HandlerResult();
        Result viewstateRs = getVIEWSTATE();

        SignRecord signRecord;
        String dateStr = SignUtils.getDateStr();
        AbstractDaoSession daoSession = dbHandler.getSoftwareDao().getSession();
        List<SignRecord> signRecords = daoSession.queryBuilder(SignRecord.class).where(SignRecordDao.Properties.SignDate.eq(dateStr)).list();
        boolean recordExsist = signRecords.size() > 0;
        if (recordExsist) {
            signRecord = signRecords.get(0);
        } else {
            signRecord = new SignRecord();
            long curTime = System.currentTimeMillis();
            signRecord.setCreateTime(curTime);
            signRecord.setSignDate(dateStr);
            signRecord.setSignTime(curTime);
            daoSession.insert(signRecord);
        }

        LinkedHashMap<String, Result> results = new LinkedHashMap<>();

        signRecord.setSuccess(false);

        if (viewstateRs.isSuccess()) {

            String viewState = (String) viewstateRs.get_extra();
            Result signRs = SignService.sign(viewState);
            results.put(SignRests.KEY_getVIEWSTATE, signRs);

            if (signRs.isSuccess()) {
                result.setStatus(0).setMsg("success");
                signRecord.setSuccess(true);
//                Result loginOutRs = loginOut();
//
//                if (loginOutRs.isSuccess()) {
//
//                } else {
//                    result.setStatus(-1).setMsg(loginOutRs.getMsg());
//                }

            } else {
                result.setStatus(-1).setMsg(signRs.getMsg());
            }
        } else {
            result.setStatus(-1).setMsg(viewstateRs.getMsg());
        }

        signRecord.setLogJson(new Gson().toJson(results));

        daoSession.update(signRecord);


        try {
            return getResponseFromResult(request,result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "handlerLogin: failed");
        return null;
    }


    @Handler("/setConfig")
    public WebResourceResponse setConfig(WebResourceRequest request) {
        Map<String, String> queryMap = getQueryMap(request.getUrl().getQuery());

        String name = queryMap.get("name");
        String value = queryMap.get("value");

        if ("autoSign".equals(name)) {
            boolean autoSign = "1#true".indexOf(value.toLowerCase()) != -1;
            if (autoSign) {
                ServiceMain.startTheServiceIfSwitchOn(MyApplication.getAppContext());
            } else {
                ServiceMain.stopTheService(MyApplication.getAppContext());
            }
            MyApplication.editShare.putBoolean(SHARE_KEY_BOOL_AUTO_SIGN, autoSign);
            MyApplication.editShare.commit();

        }


        try {
            return getResponseFromResult(request,new HandlerResult().setStatus(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "handlerLogin: failed");
        return null;
    }

    @Handler("/getConfigs")
    public WebResourceResponse getConfigs(WebResourceRequest request) {
        HandlerResult handlerResult = new HandlerResult();

        Map<String, Object> data = Maps.newHashMap();
        data.put("autoSign", MyApplication.sharedPreferences.getBoolean(SHARE_KEY_BOOL_AUTO_SIGN, true));

        handlerResult.setData(data);
        try {
            return getResponseFromResult(request,handlerResult.setStatus(0));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.i(TAG, "handlerLogin: failed");
        return null;
    }
}
