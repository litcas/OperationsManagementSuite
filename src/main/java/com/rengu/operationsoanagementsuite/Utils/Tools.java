package com.rengu.operationsoanagementsuite.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Tools {

    // 序列化对象到Json字符串
    public static String getJsonString(Object object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    // 序列化对象到Json文件中
    public static void getJsonFile(Object object, File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, object);
    }

    // 反序列化Json字符串
    public static <T> T getJsonObject(String jsonString, Class<T> classType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, classType);
    }

    // 读取Json文件
    public static <T> T getJsonObject(File jsonFile, Class<T> classType) throws IOException {
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