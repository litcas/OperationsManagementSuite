package com.rengu.operationsoanagementsuite.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonUtils {
    // 写入Json文件
    public static void writeJsonFile(Object object, File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, object);
    }

    // 读取Json文件
    public static <T> T readJsonFile(File jsonFile, Class<T> classType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonFile, classType);
    }
}
