package com.rengu.operationsoanagementsuite.Configuration;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rengu.config")
public class ServerConfiguration {
    // todo 修改为从配置文件获取属性值
    private String defultUserRole = "USER";
    private String defultAdminRole = "ADMIN";
    private String defultUserName = "admin";
    private String defultPassword = "admin";
    // 默认设置为系统缓存目录
    private String libraryPath = FileUtils.getTempDirectoryPath();

    public String getDefultUserRole() {
        return defultUserRole;
    }

    public void setDefultUserRole(String defultUserRole) {
        this.defultUserRole = defultUserRole;
    }

    public String getDefultAdminRole() {
        return defultAdminRole;
    }

    public void setDefultAdminRole(String defultAdminRole) {
        this.defultAdminRole = defultAdminRole;
    }

    public String getDefultUserName() {
        return defultUserName;
    }

    public void setDefultUserName(String defultUserName) {
        this.defultUserName = defultUserName;
    }

    public String getDefultPassword() {
        return defultPassword;
    }

    public void setDefultPassword(String defultPassword) {
        this.defultPassword = defultPassword;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }
}