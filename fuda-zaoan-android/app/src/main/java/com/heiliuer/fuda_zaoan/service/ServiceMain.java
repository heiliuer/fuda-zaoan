package com.heiliuer.fuda_zaoan.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.heiliuer.fuda_zaoan.Constants;
import com.heiliuer.fuda_zaoan.app.MyApplication;
import com.heiliuer.fuda_zaoan.receiver.AlarmReceiver;
import com.heiliuer.fuda_zaoan.utils.SinglonToast;

import java.util.Calendar;

public class ServiceMain extends Service {

    private static ServiceMain serviceMain;

    public static ServiceMain getServiceMain() {
        return serviceMain;
    }

    private SinglonToast singlonToast;
    private AlarmManager alarmManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singlonToast = new SinglonToast(this);

        serviceMain = this;

        // Get the Alarm Service.
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        setAlarm();
    }

    public Calendar getAlarmCalendar() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);

        if (cal.getTimeInMillis() - System.currentTimeMillis() > 1000 * 2) {
            long time = cal.getTimeInMillis() + 60 * 60 * 24;
            cal.setTimeInMillis(time);
        }

        return cal;
    }


    public int setAlarm() {

        Intent myIntent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long wkupTime = getAlarmCalendar().getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, wkupTime, 6 * 1000, pendingIntent);

        return 0;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceMain = null;
    }

    public static void startTheServiceIfSwitchOn(Context context) {
        boolean isServiceAutoRun = MyApplication.sharedPreferences.getBoolean(Constants.SHARE_KEY_BOOL_AUTO_SIGN, true);
        if (ServiceMain.getServiceMain() == null && isServiceAutoRun) {
            context.startService(new Intent(context, ServiceMain.class));
        }
    }

    public static void stopTheService(Context context) {
        if (ServiceMain.getServiceMain() != null) {
            context.stopService(new Intent(context, ServiceMain.class));
        }
    }
}
