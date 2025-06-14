package com.SimpleMQ.Util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateOperation {

    static SimpleDateFormat timeingFormatter
            = new SimpleDateFormat("HHmmss_SSS");
    static SimpleDateFormat dateFormatter
            = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat standardFormatter
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String GetLocalDate()
    {
        Date now = new Date();
        return dateFormatter.format(now);
    }

    public static String GetLocalTime()
    {
        Date now = new Date();
        return timeingFormatter.format(now);
    }

    public static String GetStandardDateTime()
    {
        Date now = new Date();
        return standardFormatter.format(now);
    }
}
