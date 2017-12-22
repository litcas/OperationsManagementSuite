package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Thread.UDPHandlerThread;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPSTools {

    public static List<DeviceRealInfoEntity> onlineDevices = new ArrayList<>();

    // UDP发送消息
    public static void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
        System.out.println("目标地址：" + inetAddress.getHostAddress() + ":" + port + "-->" + "发送消息：" + message);
        datagramSocket.close();
    }

    public static void receiveMessage(int port) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(port);
        byte[] bytes = new byte[ServerConfiguration.UDP_BUFFER_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while (true) {
            datagramSocket.receive(datagramPacket);
            // 启动处理线程
            new UDPHandlerThread(datagramPacket).run();
        }
    }
}