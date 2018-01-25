package com.rengu.operationsoanagementsuite.Service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;

@Service
public class UDPService {

    public void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    public void sendHeartbeatBroadcastMessage(InetAddress inetAddress, int port, InterfaceAddress interfaceAddress) throws IOException {
        String message = ("S101" + interfaceAddress.getAddress().toString()).replace("/", "");
        sandMessage(inetAddress, port, message);
    }
}
