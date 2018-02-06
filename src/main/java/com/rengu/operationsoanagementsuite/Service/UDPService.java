package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Utils.Utils;
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
        String codeType = Utils.getString("S102", 4);
        id = Utils.getString(id, 37);
        deviceId = Utils.getString(deviceId, 37);
        componentId = Utils.getString(componentId, 37);
        path = Utils.getString(path, 256);
        sandMessage(ip, port, codeType + id + deviceId + componentId + path);
    }

    public void sendScanDeviceOrderMessage(String id, String ip, int port, String deviceId, String componentId, String path, String[] extensions) throws IOException {
        String codeType = Utils.getString("S103", 4);
        id = Utils.getString(id, 37);
        deviceId = Utils.getString(deviceId, 37);
        componentId = Utils.getString(componentId, 37);
        path = Utils.getString(path, 256);
        String extension = Utils.getString(Arrays.toString(extensions).replace("[", "").replace("]", "").replaceAll("\\s*", ""), 128);
        sandMessage(ip, port, codeType + id + deviceId + componentId + extension + path);
    }
}