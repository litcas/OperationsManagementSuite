package com.rengu.operationsoanagementsuite.Network;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Thread.UDPHandlerThread;

import java.io.IOException;
import java.net.*;

public class UDPServer {

    // UDP发送消息
    public static void sandMessage(InetAddress inetAddress, int port, String message) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), socketAddress);
        datagramSocket.send(datagramPacket);
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