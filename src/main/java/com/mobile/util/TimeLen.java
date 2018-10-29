package com.mobile.util;

import java.util.Calendar;

/**
 * date: 2018/10/26
 * author: fu jia xing  161250025
 */
public class TimeLen {
    int year;
    int month;
    int day;

    public TimeLen(int y, int month, int day) {
        this.year = y;
        this.month = month;
        this.day = day;
    }

    public TimeLen(String s) {
        String[] strs = s.split("\\.");
        if (strs.length >= 1) {
            this.day = Integer.parseInt(strs[strs.length - 1]);
        }
        if (strs.length >= 2) {
            this.month = Integer.parseInt(strs[strs.length - 2]);
        }
        if (strs.length == 3) {
            this.year = Integer.parseInt(strs[strs.length - 3]);
        }

    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }



    @Override
    public String toString() {
        return this.year + "." + this.month + "." + this.day;
    }

    public String readFormat() {
        String res = "";
        if (year > 0)
            res += year + "年";
        if (month > 0)
            res += month + "月";
        if (day > 0)
            res += day + "天";
        return res;
    }
}
