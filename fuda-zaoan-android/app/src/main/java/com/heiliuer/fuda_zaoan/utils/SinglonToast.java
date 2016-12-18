package com.heiliuer.fuda_zaoan.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class SinglonToast {
    private Toast toast;

    private Context context;

    public SinglonToast(Context context) {
        this.context = context;
    }

    public void showSinglonToast(int resIdText, boolean isLongDuration) {

        showSinglonToast(context.getString(resIdText), isLongDuration);
    }

    public void showSinglonToast(String text, boolean isLongDuration) {
        int duration = isLongDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }
}
