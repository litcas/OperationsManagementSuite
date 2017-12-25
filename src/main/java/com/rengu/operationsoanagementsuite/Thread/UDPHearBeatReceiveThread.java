package com.rengu.operationsoanagementsuite.Thread;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;
import com.rengu.operationsoanagementsuite.Utils.UDPSTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPHearBeatReceiveThread implements Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(ServerConfiguration.UDP_HEARBEAT_RECEIVE_PORT);
            byte[] bytes = new byte[ServerConfiguration.UDP_BUFFER_SIZE];
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
            while (true) {
                datagramSocket.receive(datagramPacket);
                // 心跳处理代码
                byte[] data = datagramPacket.getData();
                String ip = (data[0] & 0xff) + "." + (data[1] & 0xff) + "." + (data[2] & 0xff) + "." + (data[3] & 0xff);
                DeviceRealInfoEntity deviceRealInfoEntity = new DeviceRealInfoEntity(ip, 3);
                int index = UDPSTools.onlineDevices.indexOf(deviceRealInfoEntity);
                if (index == -1) {
                    // 不存在直接添加到数组
                    UDPSTools.onlineDevices.add(deviceRealInfoEntity);
                } else {
                    // 已存在更新计数器
                    UDPSTools.onlineDevices.get(index).setCount(3);
                }
                System.out.println("当前设备数量-udp：" + UDPSTools.onlineDevices.size() + "台");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
