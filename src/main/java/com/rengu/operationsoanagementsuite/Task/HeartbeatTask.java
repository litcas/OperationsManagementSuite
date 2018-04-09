package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Entity.HeartbeatEntity;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Service.UDPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;

@Component
public class HeartbeatTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UDPService udpService;
    private final ApplicationConfiguration applicationConfiguration;

    @Autowired
    public HeartbeatTask(UDPService udpService, ApplicationConfiguration applicationConfiguration) {
        this.udpService = udpService;
        this.applicationConfiguration = applicationConfiguration;
    }

    // 心跳广播服务器ip地址
    @Scheduled(fixedRate = 5000)
    public void heartbeatBroadcast() throws IOException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getBroadcast() != null) {
                    udpService.sendHeartbeatBroadcastMessage(interfaceAddress.getBroadcast(), applicationConfiguration.getHearbeatSendPort(), interfaceAddress);
                }
            }
        }
    }

    // 检查设备在线状态
    @Scheduled(fixedRate = 5000)
    public void heartbeatMonitor() {
        Iterator<HeartbeatEntity> heartbeatEntityIterator = DeviceService.onlineHeartbeats.iterator();
        while (heartbeatEntityIterator.hasNext()) {
            HeartbeatEntity heartbeatEntity = heartbeatEntityIterator.next();
            heartbeatEntity.setCount(heartbeatEntity.getCount() - 1);
            if (heartbeatEntity.getCount() == 0) {
                heartbeatEntityIterator.remove();
                logger.info("<" + heartbeatEntity.getInetAddress().getHostAddress() + ">--->已断开服务器连接。");
            }
        }
    }

    @Async
    public void HeartbeatHandler() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(applicationConfiguration.getHearbeatReceivePort());
        DatagramPacket datagramPacket = new DatagramPacket(new byte[512], 512);
        logger.info("启动客户端心跳监听线程，监听端口：" + applicationConfiguration.getHearbeatReceivePort());
        while (!datagramSocket.isClosed()) {
            datagramSocket.receive(datagramPacket);
            // 解析心跳报文信息
            byte bytes[] = datagramPacket.getData();
            int pointer = 0;
            String codeType = new String(bytes, pointer, 4).trim();
            pointer = pointer + 4;
            String CPUInfo = new String(bytes, pointer, 64).trim();
            pointer = pointer + 64;
            String CPUClock = new String(bytes, pointer, 6).trim();
            pointer = pointer + 6;
            String CPUUtilization = new String(bytes, pointer, 4).trim();
            pointer = pointer + 4;
            int RAMSize = Integer.parseInt(new String(bytes, pointer, 6).trim());
            pointer = pointer + 6;
            int freeRAMSize = Integer.parseInt(new String(bytes, pointer, 6).trim());
            pointer = pointer + 6;
            HeartbeatEntity heartbeatEntity = new HeartbeatEntity(datagramPacket.getAddress());
            heartbeatEntity.setCPUInfo(CPUInfo);
            heartbeatEntity.setCPUClock(CPUClock);
            heartbeatEntity.setCPUUtilization(CPUUtilization);
            heartbeatEntity.setRAMSize(RAMSize);
            heartbeatEntity.setFreeRAMSize(freeRAMSize);
            int index = DeviceService.onlineHeartbeats.indexOf(heartbeatEntity);
            if (index == -1) {
                // 新发现的设备(向列表中添加)
                DeviceService.onlineHeartbeats.add(heartbeatEntity);
                logger.info("<" + heartbeatEntity.getInetAddress().getHostAddress() + ">--->已连线服务器。");
            } else {
                // 已在线的设备
                DeviceService.onlineHeartbeats.get(DeviceService.onlineHeartbeats.indexOf(heartbeatEntity)).setCount(applicationConfiguration.getDeviceLogoutDelay());
            }
        }
    }
}
