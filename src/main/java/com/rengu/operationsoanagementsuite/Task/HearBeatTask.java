package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

@Component
public class HearBeatTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 心跳报文接收端口
    private static final int HEARBEAT_RECEIVE_PORT = 6004;
    // 心跳报文发送端口
    private static final int HEARBEAT_SEND_PORT = 3086;
    // 在线设备
    private static List<DeviceRealInfoEntity> onlineDevices = new ArrayList<>();
    @Autowired
    private UDPService udpService;

    // 广播服务器ip地址
    @Scheduled(fixedRate = 5000)
    public void broadcastServerIp() throws IOException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getBroadcast() != null) {
                    udpService.sendServerIpMessage(interfaceAddress.getBroadcast(), HEARBEAT_SEND_PORT, interfaceAddress);
                }
            }
        }
    }

    // 检查设备在线状态
    @Scheduled(fixedRate = 5000)
    public void devicesOnlineMonitor() {
        Iterator<DeviceRealInfoEntity> deviceRealInfoEntityIterator = onlineDevices.iterator();
        while (deviceRealInfoEntityIterator.hasNext()) {
            DeviceRealInfoEntity deviceRealInfoEntity = deviceRealInfoEntityIterator.next();
            deviceRealInfoEntity.setCount(deviceRealInfoEntity.getCount() - 1);
            if (deviceRealInfoEntity.getCount() == 0) {
                deviceRealInfoEntityIterator.remove();
            }
        }
        logger.info("当前设备在线数量 ：" + onlineDevices.size() + "台");
    }

    @Async
    public void receiveHearBeat() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(HEARBEAT_RECEIVE_PORT);
        byte[] bytes = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        logger.info("启动客户端心跳监听线程，监听端口：" + HEARBEAT_RECEIVE_PORT);
        while (true) {
            datagramSocket.receive(datagramPacket);
            // 心跳处理代码
            byte[] data = datagramPacket.getData();
            String ip = (data[0] & 0xff) + "." + (data[1] & 0xff) + "." + (data[2] & 0xff) + "." + (data[3] & 0xff);
            DeviceRealInfoEntity deviceRealInfoEntity = new DeviceRealInfoEntity(ip, 3);
            logger.info("收到心跳报文，来自：" + ip);
            int index = onlineDevices.indexOf(deviceRealInfoEntity);
            if (index == -1) {
                // 不存在直接添加到数组
                onlineDevices.add(deviceRealInfoEntity);
            } else {
                // 已存在更新计数器
                onlineDevices.get(index).setCount(3);
            }
        }
    }
}