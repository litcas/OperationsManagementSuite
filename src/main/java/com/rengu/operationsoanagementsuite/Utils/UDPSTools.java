package com.rengu.operationsoanagementsuite.Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPSTools {

    public static List<DeviceRealInfoEntity> onlineDevices = new ArrayList<>();

    // 客户端报文
    public static final String RECEIVEHEARBEAT = "C101";
    // 服务器报文
    private static final String SEND_BROADCAST = "S101";

    public static String getServerIpMessage(InterfaceAddress interfaceAddress) {
        return (SEND_BROADCAST + interfaceAddress.getAddress().toString()).replace("/", "");
    }

    // UDP发送消息
    public static void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
        System.out.println("目标地址：" + inetAddress.getHostAddress() + ":" + port + "-->" + "发送消息：" + message);
        datagramSocket.close();
    }
}