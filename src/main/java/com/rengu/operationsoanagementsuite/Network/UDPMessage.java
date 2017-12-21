package com.rengu.operationsoanagementsuite.Network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPMessage {
    public static String getServerInfoMessage() throws UnknownHostException {
        short Id = (short) 0xB102;
        String Ip = InetAddress.getLocalHost().getHostAddress();
        return Id + Ip;
    }
}
