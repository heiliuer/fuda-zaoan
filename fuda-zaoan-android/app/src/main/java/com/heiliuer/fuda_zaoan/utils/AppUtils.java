package com.heiliuer.fuda_zaoan.utils;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.heiliuer.fuda_zaoan.Constants;
import com.heiliuer.fuda_zaoan.SignRecord;
import com.heiliuer.fuda_zaoan.SignUtils;
import com.heiliuer.fuda_zaoan.app.MyApplication;
import com.heiliuer.fuda_zaoan.dao.SignRecordDao;
import com.heiliuer.fuda_zaoan.server.Result;
import com.heiliuer.fuda_zaoan.server.request.SignRests;
import com.heiliuer.fuda_zaoan.server.request.SignService;

import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.dao.AbstractDaoSession;

/**
 * Created by Administrator on 2016/12/19 0019.
 */

public class AppUtils {

    static String TAG = "AppUtils";

    private static int retryTimes;


    public static void autoSigned(int retryTimes, final long retryDura) {
        final String userkey = MyApplication.sharedPreferences.getString(Constants.SHARE_KEY_STR_USER_KEY, "");
        final String userid = MyApplication.sharedPreferences.getString(Constants.SHARE_KEY_STR_USER_ID, "");

        AppUtils.retryTimes = retryTimes;
        Log.i(TAG, "onReceive: ###auto sign" + String.format("userkey:%s,userid:%s", userkey, userid));


        if (!Strings.isNullOrEmpty(userkey) && !Strings.isNullOrEmpty(userid)) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    while (AppUtils.retryTimes > 0) {
                        LinkedHashMap<String, Result> results = SignService.directSignForResult(userkey, userid);

                        SignRecord signRecord;
                        String dateStr = SignUtils.getDateStr();
                        AbstractDaoSession daoSession = MyApplication.getDbHandler().getSoftwareDao().getSession();
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

                        Result result = results.get(SignRests.KEY_sign);

                        signRecord.setSuccess(result != null && result.isSuccess());

                        signRecord.setLogJson(new Gson().toJson(results));

                        signRecord.setAutoSigned(true);

                        daoSession.update(signRecord);

                        if (signRecord.getSuccess()) {
                            AppUtils.retryTimes = 0;
                        } else {
                            AppUtils.retryTimes--;
                            if (AppUtils.retryTimes > 0) {
                                try {
                                    Thread.sleep(retryDura);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                }
            };
        } else {
            Log.i(TAG, "autoSigned: userid or userkey is empty");
        }
    }
}
