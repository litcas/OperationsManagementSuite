package com.rengu.operationsoanagementsuite.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Tools {
    // 计算文件的MD5值
    public static String getFileMD5(File file) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(file));
    }

    // 序列化对象到Json文件中
    public static void writeJsonFile(Object object, File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, object);
    }

    // 读取Json文件
    public static <T> T readJsonFile(File jsonFile, Class<T> classType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonFile, classType);
    }

    // 生成指定长度的字符串
    public static String getString(String string, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.setLength(size);
        return stringBuilder.toString();
    }
}