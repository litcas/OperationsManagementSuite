package com.rengu.operationsoanagementsuite.Service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

@Service
public class UDPService {

    public void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    public void sandMessage(String hostName, int port, String message) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(hostName);
        sandMessage(inetAddress, port, message);
    }

    public void sendHeartbeatBroadcastMessage(InetAddress inetAddress, int port, InterfaceAddress interfaceAddress) throws IOException {
        String message = ("S101" + interfaceAddress.getAddress().toString()).replace("/", "");
        sandMessage(inetAddress, port, message);
    }

    public void sendScanDeviceOrderMessage(String id, String ip, int port, String deviceId, String componentId, String path) throws IOException {
        String codeType = getString("S102", 4);
        id = getString(id, 37);
        deviceId = getString(deviceId, 37);
        componentId = getString(componentId, 37);
        path = getString(path, 256);
        sandMessage(ip, port, codeType + id + deviceId + componentId + path);
    }

    public void sendScanDeviceOrderMessage(String id, String ip, int port, String deviceId, String componentId, String path, String[] extensions) throws IOException {
        String codeType = getString("S103", 4);
        id = getString(id, 37);
        deviceId = getString(deviceId, 37);
        componentId = getString(componentId, 37);
        path = getString(path, 256);
        String extension = getString(Arrays.toString(extensions).replace("[", "").replace("]", "").replaceAll("\\s*", ""), 128);
        sandMessage(ip, port, codeType + id + deviceId + componentId + extension + path);
    }

    // 生成指定长度的字符串
    private String getString(String string, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.setLength(length);
        return stringBuilder.toString();
    }
}