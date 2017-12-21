package com.rengu.operationsoanagementsuite.Scheduled;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Network.UDPMessage;
import com.rengu.operationsoanagementsuite.Network.UDPServer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Component
public class ScheduledTask {

    @Scheduled(fixedRate = 5000)
    public void reportServerInfo() throws IOException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        InetAddress inetAddress = InetAddress.getByName("192.168.0.255");
        UDPServer.sandMessage(inetAddress, ServerConfiguration.UDP_SEND_PORT, UDPMessage.getServerIpMessage());
    }
}