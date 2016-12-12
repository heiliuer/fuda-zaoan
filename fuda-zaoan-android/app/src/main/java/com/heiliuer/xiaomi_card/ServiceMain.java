package com.heiliuer.xiaomi_card;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ServiceMain extends Service {

    public static ServiceMain SERVICE_MAIN_RUNNING;
    private ScreenLockChecker screenLockChecker;
    private SinglonToast singlonToast;
    private ActivityManager activityManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singlonToast = new SinglonToast(this);
        registerScreenOnReceiver();
        SERVICE_MAIN_RUNNING = this;

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    private void registerScreenOnReceiver() {
        screenLockChecker = new ScreenLockChecker(this);
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
    }


    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isAutoClickInLocked = ApplicationMy.sharedPreferences.getBoolean(Constants.SHARE_KEY_IS_AUTO_CLICK_IN_LOCKED, true);
            if (isAutoClickInLocked && Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
        SERVICE_MAIN_RUNNING = null;
    }
}
