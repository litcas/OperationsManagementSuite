package com.rengu.operationsoanagementsuite.Utils;

import java.net.InterfaceAddress;

public class UDPMessage {

    public enum PLATFORM {
        WINDOWS(1, "Windows"),
        LINUX(2, "Linux"),
        NEOKYLIN(3, "NeoKylin"),
        VXWORKS(4, "VxWorks"),
        LAMDPRO(5, "LamDpro");

        private int platformCode;
        private String platformName;

        PLATFORM(int platformCode, String platformName) {
            this.platformCode = platformCode;
            this.platformName = platformName;
        }

        public int getPlatformCode() {
            return platformCode;
        }

        public String getPlatformName() {
            return platformName;
        }
    }

    // 客户端报文
    public static final String RECEIVEHEARBEAT = "C101";
    // 服务器报文
    private static final String SEND_BROADCAST = "S101";

    public static String getServerIpMessage(InterfaceAddress interfaceAddress) {
        return SEND_BROADCAST + interfaceAddress.getAddress().toString();
    }
}
