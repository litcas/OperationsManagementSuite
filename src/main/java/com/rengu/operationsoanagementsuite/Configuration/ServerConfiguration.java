package com.rengu.operationsoanagementsuite.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rengu.config")
public class ServerConfiguration {
    // todo 修改为从配置文件获取属性值
    private String defultRole = "USER";

    public String getDefultRole() {
        return defultRole;
    }
}