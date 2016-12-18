package com.heiliuer.fuda_zaoan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heiliuer.fuda_zaoan.Constants;
import com.heiliuer.fuda_zaoan.app.MyApplication;
import com.heiliuer.fuda_zaoan.service.ServiceMain;

import static com.heiliuer.fuda_zaoan.utils.AppUtils.autoSigned;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isServiceAutoRun = MyApplication.sharedPreferences.getBoolean(Constants.SHARE_KEY_BOOL_AUTO_SIGN, true);
        if (isServiceAutoRun) {
            autoSigned(4, 3000);
        }
        ServiceMain.startTheServiceIfSwitchOn(context);
    }


}