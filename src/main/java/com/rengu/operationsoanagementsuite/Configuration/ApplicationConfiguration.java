package com.rengu.operationsoanagementsuite.Configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    // Device 通信端口
    public static final int deviceUDPPort = 3087;
    public static final int deviceTCPPort = 3088;

    private String defultUsername = "admin";
    private String defultPassword = "admin";
    private String componentLibraryName = "lib";
    private String jsonFileName = "export.json";
    private String compressFileName = "ExportComponent.zip";
    private int hearbeatSendPort = 3086;
    private int hearbeatReceivePort = 6004;
    private int tcpReceivePort = 6005;
    private int deviceLogoutDelay = 5;

    // 组件部署参数
    private int socketTimeout = 2000;
    private int maxWaitTimes = 10;
    private int maxRetryTimes = 5;

    // 不可修改项-自动从运行环境获取
    private String componentLibraryPath = "";

    public String getDefultUsername() {
        return defultUsername;
    }

    public String getDefultPassword() {
        return defultPassword;
    }

    public String getComponentLibraryName() {
        return componentLibraryName;
    }

    public String getJsonFileName() {
        return jsonFileName;
    }

    public String getCompressFileName() {
        return compressFileName;
    }

    public int getHearbeatSendPort() {
        return hearbeatSendPort;
    }

    public int getHearbeatReceivePort() {
        return hearbeatReceivePort;
    }

    public int getTcpReceivePort() {
        return tcpReceivePort;
    }

    public int getDeviceLogoutDelay() {
        return deviceLogoutDelay;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getMaxWaitTimes() {
        return maxWaitTimes;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public String getComponentLibraryPath() {
        return componentLibraryPath;
    }

    public void setComponentLibraryPath(String componentLibraryPath) {
        this.componentLibraryPath = componentLibraryPath;
    }
}