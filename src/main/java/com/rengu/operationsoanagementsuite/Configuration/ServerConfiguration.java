package com.rengu.operationsoanagementsuite.Configuration;

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

    public String getDefultUserRole() {
        return defultUserRole;
    }

    public String getDefultAdminRole() {
        return defultAdminRole;
    }

    public String getDefultUserName() {
        return defultUserName;
    }

    public String getDefultPassword() {
        return defultPassword;
    }
}