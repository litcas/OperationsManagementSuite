package com.rengu.operationsoanagementsuite.Network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPMessage {
    // 客户端报文
    public static final String RECEIVEHEARBEAT = "C101";
    // 服务器报文
    private static final String SEND_BROADCAST = "S101";

    public static String getServerIpMessage() throws UnknownHostException {
        return SEND_BROADCAST + InetAddress.getLocalHost().getHostAddress();
    }
}
