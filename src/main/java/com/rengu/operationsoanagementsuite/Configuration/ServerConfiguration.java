package com.rengu.operationsoanagementsuite.Configuration;

import org.springframework.stereotype.Component;

@Component
public class ServerConfiguration {
    // 用户相关
    public static final String USER_ROLE_NAME = "ROLE_USER";
    public static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    public static final String SEPARATOR = "-";
    public static final String COMPONENT_LIBRARY_NAME = "ComponentLibraries";
    public static final String EXPORT_COMPONENT_FILE_NAME = "exportcomponent";
    public static final String EXPORT_COMPONENT_INFO_NAME = "component.json";
    public static final String EXPORT_ENTITY_FILE_NAME = "entityFile";
    // UDP 配置
    public static final int TCP_RECEIVE_PORT = 6005;
    public static final int UDP_BROADCAST_SEND_PORT = 3086;
    public static final int UDP_SEND_PORT = 3087;
    public static final int UDP_HEARBEAT_RECEIVE_PORT = 6004;
    public static final int UDP_CODE_SIZE = 4;
    public static final int UDP_BUFFER_SIZE = 32;

    // todo 修改可配置项为从配置文件读取属性
    // 用户相关
    private String defultUsername = "admin";
    private String defultPassword = "admin";
    // 组件相关
    private String componentLibraryPath;

    public String getDefultUsername() {
        return defultUsername;
    }

    public void setDefultUsername(String defultUsername) {
        this.defultUsername = defultUsername;
    }

    public String getDefultPassword() {
        return defultPassword;
    }

    public void setDefultPassword(String defultPassword) {
        this.defultPassword = defultPassword;
    }

    public String getComponentLibraryPath() {
        return componentLibraryPath;
    }

    public void setComponentLibraryPath(String componentLibraryPath) {
        this.componentLibraryPath = componentLibraryPath;
    }
}