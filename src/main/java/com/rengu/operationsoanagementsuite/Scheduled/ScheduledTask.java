package com.rengu.operationsoanagementsuite.Scheduled;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Network.UDPService;
import com.rengu.operationsoanagementsuite.Utils.UDPMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Component
public class ScheduledTask {

    @Scheduled(fixedRate = 5000)
    public void reportServerInfo() throws IOException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getBroadcast() != null) {
                    UDPService.sandMessage(interfaceAddress.getBroadcast(), ServerConfiguration.UDP_SEND_PORT, UDPMessage.getServerIpMessage(interfaceAddress));
                }
            }
        }
    }
}