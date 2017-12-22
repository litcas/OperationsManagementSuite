package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SystemService {

    @Autowired
    private ServerConfiguration serverConfiguration;

    // 返回系统信息
    public Properties getSystemInfo() {
        return System.getProperties();
    }

    // 返回服务器配置信息
    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }
}
