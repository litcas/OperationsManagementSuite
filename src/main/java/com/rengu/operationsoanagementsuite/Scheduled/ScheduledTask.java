package com.rengu.operationsoanagementsuite.Scheduled;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Network.UDPMessage;
import com.rengu.operationsoanagementsuite.Network.UDPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;

@Component
public class ScheduledTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedRate = 5000)
    public void reportServerInfo() throws IOException {
        InetAddress inetAddress = InetAddress.getByName("255.255.255.255");
        UDPServer.sandMessage(inetAddress, ServerConfiguration.UDP_PORT, UDPMessage.getServerInfoMessage());
    }
}