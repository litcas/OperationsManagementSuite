package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

@Service
public class UDPService {

    // 服务器地址广播报文代码
    private static final String HEARBEAT_CODE = "S101";

    public void sendScanDeviceMessage(String ip, int port, String id, DeployPlanDetailEntity deployPlanDetailEntity, String[] extensions) throws IOException {
        String message = getScanDeviceMessage(id, deployPlanDetailEntity, extensions);
        sandMessage(ip, port, message);
        System.out.println(message + "发送至：" + ip + ":" + port);
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
        String codeType = "S102";
        String requestId = Tools.getString(id, 37);
        String deviceId = Tools.getString(deployPlanDetailEntity.getDeviceEntity().getId(), 37);
        String componentId = Tools.getString(deployPlanDetailEntity.getComponentEntity().getId(), 37);
        String extension = null;
        if (extensions != null) {
            codeType = "S103";
            extension = Tools.getString(Arrays.toString(extensions).replace("[", "").replace("]", "").replaceAll("\\s*", ""), 128);
        }
        codeType = Tools.getString(codeType, 4);
        String deployPath = Tools.getString(deployPlanDetailEntity.getDeployPath(), 256);
        if (extension == null) {
            return codeType + requestId + deviceId + componentId + deployPath;
        } else {
            return codeType + requestId + deviceId + componentId + extension + deployPath;
        }
    }
}