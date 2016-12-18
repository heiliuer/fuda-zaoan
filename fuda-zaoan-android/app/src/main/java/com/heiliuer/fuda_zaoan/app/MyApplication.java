package com.heiliuer.fuda_zaoan.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.heiliuer.fuda_zaoan.ThreadExceptionHandler;
import com.heiliuer.fuda_zaoan.db.DbHandler;
import com.heiliuer.fuda_zaoan.ui.ActWebMain;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class MyApplication extends Application implements ThreadExceptionHandler.OnException {

    public static final int TRIGGER_AT_MILLIS = 8 * 1000;//异常重启时间
    public static final String SHARE_NAME_MAIN = "main-config";
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editShare;
    private static DbHandler dbHandler;

    public static DbHandler getDbHandler() {
        return dbHandler;
    }


    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        ThreadExceptionHandler.handlerThreadException(this);
        sharedPreferences = getApplicationContext().getSharedPreferences(SHARE_NAME_MAIN, MODE_PRIVATE);
        editShare = sharedPreferences.edit();
        lastExceptionTime = sharedPreferences.getLong("lastExceptionTime", 0);
        dbHandler = new DbHandler(getApplicationContext());


//        dbHandler.getSoftwareDao().deleteAll();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (editShare != null) {
            editShare.commit();
        }
    }

    private long lastExceptionTime;

    public void saveLastExceptionTime(long value) {
        editShare.putLong("lastExceptionTime", value);
        editShare.commit();
    }

    @Override
    public boolean onException(Thread paramThread, Throwable paramThrowable) {
        Log.d("MyApplication", "lastExceptionTime：" + lastExceptionTime);
        if (System.currentTimeMillis() - lastExceptionTime >= 45 * 1000) {
            //定时重启
            Context context = this.getApplicationContext();
            PendingIntent myActivity = PendingIntent.getActivity(context,
                    192837, new Intent(context, ActWebMain.class),
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager;
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    TRIGGER_AT_MILLIS, myActivity);
            saveLastExceptionTime(System.currentTimeMillis());
            //toast 无效
            //new SinglonToast(context).showSinglonToast(R.string.error, false);
            System.exit(2);
        }
        return false;
    }
}
