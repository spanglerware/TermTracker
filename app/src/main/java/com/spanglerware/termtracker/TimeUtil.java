package com.spanglerware.termtracker;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Scott on 3/16/2016.
 */
public class TimeUtil {

    public static long dateToMillis(Date date) {
        return date.getTime();
    }

    public static Date millisToDate(long millis) {
        return new Date(millis);
    }

    public static String formatDate(long millis) {
        Date date = new Date(millis);

        return formatDate(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        return sdf.format(date);
    }

    public static Date parseDate(String date) {
        ParsePosition index = new ParsePosition(0);
        Date dt = new SimpleDateFormat("dd-MMM-yyyy").parse(date,index);

        return dt;
    }
}
