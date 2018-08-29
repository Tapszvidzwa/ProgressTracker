package com.example.tapiwa.todoapp.Utils;

import android.app.Activity;

import java.util.Calendar;
import java.util.Random;

public class Util {

    Activity activity;
    public String projectName;

    public Util() {
    }

    public Util(Activity context) {
        this.activity = context;
    }

    public String getDateFromMillis(Long millis) {
        if (millis != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int month = calendar.get(calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            return generateDateAsString(dayOfMonth, dayOfWeek, month, year);
        }
        return "";
    }

    private String getDayOfWeekAsString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "";
        }
    }

    private String getMonthAsString(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }

    private String generateDateAsString(int dayOfMonth, int dayOfWeek, int month, int year) {
        StringBuilder builder = new StringBuilder();
        builder.append(getDayOfWeekAsString(dayOfWeek));
        builder.append(" ");
        builder.append(getMonthAsString(month));
        builder.append(" ");
        builder.append(dayOfMonth);
        builder.append(", ");
        builder.append(year);
        return builder.toString();
    }


    public static int getRandomAlphabeticChar() {
        Random rand = new Random();
        return rand.nextInt(26) + 97;
    }

    public static int getRandomDigit() {
        Random rand = new Random();
        return rand.nextInt(10) + 48;
    }

}
