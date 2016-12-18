package com.heiliuer.fuda_zaoan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heiliuer.fuda_zaoan.service.ServiceMain;

/**
 * Created by Heiliuer on 2016/9/24 0024.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceMain.startTheServiceIfSwitchOn(context);
    }
}
