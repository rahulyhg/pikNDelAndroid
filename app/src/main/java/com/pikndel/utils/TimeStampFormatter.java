package com.pikndel.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class TimeStampFormatter {


    /**
     *
     *
     yyyy-MM-dd 1969-12-31
     yyyy-MM-dd 1970-01-01
     yyyy-MM-dd HH:mm 1969-12-31 16:00
     yyyy-MM-dd HH:mm 1970-01-01 00:00
     yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
     yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
     yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
     yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000
     *
     */
    public static String getValueFromTS(String timeStamp, String dateFormat) {
        try {
            if (timeStamp.length() > 0) {
                DateFormat formatter1 = new SimpleDateFormat(dateFormat, Locale.US);
                LogUtils.infoLog("TimeStampFormatter", "_____dateFormat_____" + dateFormat);
                LogUtils.infoLog("TimeStampFormatter", "_____getValueFromTS_____" + formatter1.format(new Date((Long.valueOf(timeStamp)))));
                return formatter1.format(new Date((Long.valueOf(timeStamp))));
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getTimeStamp(String date, String dateFormat) {
        try{
            if (!TextUtils.isEmpty(date)){
                DateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
                Date newDate = (Date)formatter.parse(date);
                LogUtils.infoLog("TimeStampFormatter", "_____dateFormat_____" + dateFormat);
                LogUtils.infoLog("TimeStampFormatter", "_____getTimeStamp_____" + newDate.getTime());
                return newDate.getTime();
            }else {
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static String changeDateTimeFormat(String date, String oldFormat, String newFormat) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat, Locale.US);
        Date newDate;
        String str;
        try {
            newDate = inputFormat.parse(date);
            str = outputFormat.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        LogUtils.infoLog("TimeStampFormatter", "_____oldFormat_____" + oldFormat);
        LogUtils.infoLog("TimeStampFormatter", "_____newFormat_____" + newFormat);
        LogUtils.infoLog("TimeStampFormatter", "_____date_____" + date);
        LogUtils.infoLog("TimeStampFormatter", "_____changeDateFormat_____" + str);

        return str;
    }

    public static String getDateSuffix( int day) {
        switch (day) {
            case 1: case 21: case 31:
                return ("st");

            case 2: case 22:
                return ("nd");

            case 3: case 23:
                return ("rd");

            default:
                return ("th");
        }
    }

  /*  private static String getDate(int date){
            switch (date){
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    return
        }

    }*/

    public static long getNumberOfDays(String startDate, String endDate){
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
        try {
            Date date1 = myFormat.parse(startDate);
            Date date2 = myFormat.parse(endDate);
            long diff = date2.getTime() - date1.getTime();
            LogUtils.infoLog("TimeStampFormatter", "_____getNumberOfDays_____" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getNumbersOfDays(String currentDate, String startDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date1 = simpleDateFormat.parse(currentDate);
            Date date2 = simpleDateFormat.parse(startDate);
            long days = printDifference(date1, date2);

            if (days>0){
                if (days == 1) {
                    return String.valueOf(days) + " day until";
                }else {
                    return String.valueOf(days) + " days until";
                }
            }else if (days == 0){
                return "today is";
            }else if (days == -1){
                return " yesterday was";
            }else {
                long day = -1*days;
                return String.valueOf(day)+" days ago";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static long printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;

    }
}
