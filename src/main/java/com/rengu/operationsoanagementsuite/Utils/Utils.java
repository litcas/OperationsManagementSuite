package com.rengu.operationsoanagementsuite.Utils;

public class Utils {

    public static final int UNKNOWN_PLATFORM = 0;
    public static final int WINDOWS_PLATFORM = 1;
    public static final int MACOS_PLATFORM = 2;

    // 生成指定长度的字符串
    public static String getString(String string, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.setLength(length);
        return stringBuilder.toString();
    }

    public static int getPlatform() {
        String osName = System.getProperty("os.name");
        // Windows平台
        if (osName.contains("windows")) {
            return WINDOWS_PLATFORM;
        }
        // macOS平台
        if (osName.equals("Mac OS X")) {
            return MACOS_PLATFORM;
        }
        // 未知平台
        return UNKNOWN_PLATFORM;
    }
}
