package com.rengu.operationsoanagementsuite.Utils;

import java.text.DecimalFormat;

public class FormatUtils {

    public static final String doubleFormatPattern = "#.00";

    // 浮点型格式化
    public static double doubleFormater(double number, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return Double.parseDouble(decimalFormat.format(number));
    }
}
