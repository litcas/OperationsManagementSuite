package com.rengu.operationsoanagementsuite.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

    public static final String doubleFormatPattern = "#.00";

    // 浮点型格式化
    public static double doubleFormater(double number, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return Double.parseDouble(decimalFormat.format(number));
    }

    // 时间型格式化
    public static Date dateFormat(long date, String pattern) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.parse(simpleDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
