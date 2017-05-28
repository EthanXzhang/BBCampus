package com.lyzz.bbcampus.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/4/19.
 */

public class  TimeStampTrans {
    public static String dataTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss",
                Locale.getDefault());
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            times = String.valueOf(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }
    public static String datetoString(Date date)
    {
        long l=date.getTime();
        return String.valueOf(l);
    }
}
