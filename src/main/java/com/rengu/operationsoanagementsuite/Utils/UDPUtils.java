package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPUtils {

    public static List<DeviceRealInfoEntity> onlineDevices = new ArrayList<>();

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
        if (datagramSocket.isConnected()) {
            datagramSocket.send(datagramPacket);
            datagramSocket.close();
        }
        datagramSocket.close();
    }
}