package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPUtils {

    // 服务器报文
    public static final String SEND_BROADCAST = "S101";
    public static final String SEND_SCANDEVICE = "S102";
    // 客户端报文
    public static final String RECEIVE_SCAN_RESULT = "C102";
    // 在线设备
    public static List<DeviceRealInfoEntity> onlineDevices = new ArrayList<>();

    public static String getServerIpMessage(InterfaceAddress interfaceAddress) {
        return (SEND_BROADCAST + interfaceAddress.getAddress().toString()).replace("/", "");
    }

    public static String getScanDeviceMessage(String id, DeployPlanDetailEntity deployPlanDetailEntity) {
        String codeType = getString(SEND_SCANDEVICE, ServerConfiguration.UDP_CODE_SIZE);
        String requestId = getString(id, 37);
        String deviceId = getString(deployPlanDetailEntity.getDeviceEntity().getId(), 37);
        String componentId = getString(deployPlanDetailEntity.getComponentEntity().getId(), 37);
        String extension = getString("exe", 10);
        String deployPath = getString(deployPlanDetailEntity.getDeployPath(), 256);
        return codeType + requestId + deviceId + componentId + extension + deployPath;
    }

    // UDP发送消息
    public static void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    // UDP发送消息
    public static void sandMessage(String ip, int port, String message) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        UDPUtils.sandMessage(inetAddress, port, message);
    }

    private static String getString(String string, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.setLength(size);
        return stringBuilder.toString();
    }
}