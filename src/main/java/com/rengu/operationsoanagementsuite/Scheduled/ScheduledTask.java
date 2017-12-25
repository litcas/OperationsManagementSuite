package com.rengu.operationsoanagementsuite.Scheduled;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;
import com.rengu.operationsoanagementsuite.Utils.UDPTools;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;

@Component
public class ScheduledTask {

    // 广播服务器ip地址
    @Scheduled(fixedRate = 5000)
    public void reportServerInfo() throws IOException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getBroadcast() != null) {
                    UDPTools.sandMessage(interfaceAddress.getBroadcast(), ServerConfiguration.UDP_SEND_PORT, UDPTools.getServerIpMessage(interfaceAddress));
                }
            }
        }
    }

    // 检查设备在线状态
    @Scheduled(fixedRate = 5000)
    public void devicesOnlineMonitor() {
        Iterator<DeviceRealInfoEntity> deviceRealInfoEntityIterator = UDPTools.onlineDevices.iterator();
        while (deviceRealInfoEntityIterator.hasNext()) {
            DeviceRealInfoEntity deviceRealInfoEntity = deviceRealInfoEntityIterator.next();
            deviceRealInfoEntity.setCount(deviceRealInfoEntity.getCount() - 1);
            if (deviceRealInfoEntity.getCount() == 0) {
                deviceRealInfoEntityIterator.remove();
            }
        }
    }
}