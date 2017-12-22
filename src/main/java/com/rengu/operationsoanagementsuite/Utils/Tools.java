package com.rengu.operationsoanagementsuite.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rengu.operationsoanagementsuite.Network.UDPMessage;
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

    // 查询操作系统
    public static String getPlatformName(int platformCode) {
        for (UDPMessage.PLATFORM platform : UDPMessage.PLATFORM.values()) {
            if (platformCode == platform.getPlatformCode()) {
                return platform.getPlatformName();
            }
        }
        return null;
    }
}