package com.rengu.operationsoanagementsuite.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rengu.config")
public class ServerConfiguration {
    private String defultRole = "USER";

    public String getDefultRole() {
        return defultRole;
    }
}