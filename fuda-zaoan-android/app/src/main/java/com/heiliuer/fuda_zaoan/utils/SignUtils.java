package com.heiliuer.fuda_zaoan;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/18 0018.
 */

public class SignUtils {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String getDateStr(long timemill) {
        Date date = new Date();
        date.setTime(timemill);
        return DATE_FORMAT.format(date);
    }

    public static String getDateStr(Date date) {
        return getDateStr(date.getTime());
    }

    public static String getDateStr() {
        return getDateStr(System.currentTimeMillis());
    }
}
