package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;

@Service
public class UDPService {

    // 服务器地址广播报文代码
    private static final String HEARBEAT_CODE = "S101";

    public void sendScanDeviceMessage(String ip, int port, String id, DeployPlanDetailEntity deployPlanDetailEntity) throws IOException {
        String message = getScanDeviceMessage(id, deployPlanDetailEntity);
        sandMessage(ip, port, message);
    }

    public void sendServerIpMessage(InetAddress inetAddress, int port, InterfaceAddress interfaceAddress) throws IOException {
        String message = getServerIpMessage(interfaceAddress);
        sandMessage(inetAddress, port, message);
    }

    // UDP发送消息
    private void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    // UDP发送消息
    private void sandMessage(String ip, int port, String message) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        sandMessage(inetAddress, port, message);
    }

    // 生成广播报文
    private String getServerIpMessage(InterfaceAddress interfaceAddress) {
        return (HEARBEAT_CODE + interfaceAddress.getAddress().toString()).replace("/", "");
    }

    // 生成扫描报文
    private String getScanDeviceMessage(String id, DeployPlanDetailEntity deployPlanDetailEntity, String... extensions) {
        String codeType = Tools.getString("S102", 4);
        String requestId = Tools.getString(id, 37);
        String deviceId = Tools.getString(deployPlanDetailEntity.getDeviceEntity().getId(), 37);
        String componentId = Tools.getString(deployPlanDetailEntity.getComponentEntity().getId(), 37);
        String extension = Tools.getString("", 128);
        if (extensions.length != 0) {
            for (int i = 0; i < extensions.length; i++) {
                if (i == extensions.length) {
                    extension = extension + extensions[i];
                } else {
                    extension = extension + extensions[i] + ",";
                }
            }
        }
        String deployPath = Tools.getString(deployPlanDetailEntity.getDeployPath(), 256);
        return codeType + requestId + deviceId + componentId + extension + deployPath;
    }
}