package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;
import com.rengu.operationsoanagementsuite.Utils.UDPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Component
public class HearBeatTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    public void receiveHearBeat() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(ServerConfiguration.UDP_HEARBEAT_RECEIVE_PORT);
        byte[] bytes = new byte[ServerConfiguration.UDP_BUFFER_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while (true) {
            datagramSocket.receive(datagramPacket);
            // 心跳处理代码
            byte[] data = datagramPacket.getData();
            String ip = (data[0] & 0xff) + "." + (data[1] & 0xff) + "." + (data[2] & 0xff) + "." + (data[3] & 0xff);
            logger.info("收到心跳报文，来自：" + ip);
            DeviceRealInfoEntity deviceRealInfoEntity = new DeviceRealInfoEntity(ip, 3);
            int index = UDPUtils.onlineDevices.indexOf(deviceRealInfoEntity);
            if (index == -1) {
                // 不存在直接添加到数组
                UDPUtils.onlineDevices.add(deviceRealInfoEntity);
            } else {
                // 已存在更新计数器
                UDPUtils.onlineDevices.get(index).setCount(3);
            }
        }
    }
}
