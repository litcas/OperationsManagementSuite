package com.rengu.operationsoanagementsuite.Service;

import org.springframework.stereotype.Service;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

@Service
public class SystemService {

    // 返回系统信息
    public Properties getSystemInfo() {
        return System.getProperties();
    }

    // 网络信息
    public List<NetworkEntity> getNetworks() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        List<NetworkEntity> networkEntityList = new ArrayList<>();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            // 建立网卡信息
            NetworkEntity networkEntity = new NetworkEntity();
            networkEntity.setName(networkInterface.getDisplayName());
            networkEntity.setIndex(networkInterface.getIndex());
            networkEntity.setVirtual(networkInterface.isVirtual());
            List<InetAddress> inetAddresses = new ArrayList<>();
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress inetAddress = new InetAddress();
                inetAddress.setIPv4Address(interfaceAddress.getAddress() == null ? null : interfaceAddress.getAddress().getHostAddress());
                inetAddress.setBroadcastAddress(interfaceAddress.getBroadcast() == null ? null : interfaceAddress.getBroadcast().getHostAddress());
                inetAddresses.add(inetAddress);
            }
            networkEntity.setInetAddresses(inetAddresses);
            networkEntityList.add(networkEntity);
        }
        return networkEntityList;
    }

    private class InetAddress {
        private String IPv4Address;
        private String broadcastAddress;

        public String getIPv4Address() {
            return IPv4Address;
        }

        void setIPv4Address(String IPv4Address) {
            this.IPv4Address = IPv4Address;
        }

        public String getBroadcastAddress() {
            return broadcastAddress;
        }

        void setBroadcastAddress(String broadcastAddress) {
            this.broadcastAddress = broadcastAddress;
        }
    }

    private class NetworkEntity {
        private String name;
        private int index;
        private List<InetAddress> inetAddresses;
        private boolean virtual = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        void setIndex(int index) {
            this.index = index;
        }

        public List<InetAddress> getInetAddresses() {
            return inetAddresses;
        }

        void setInetAddresses(List<InetAddress> inetAddresses) {
            this.inetAddresses = inetAddresses;
        }

        public boolean isVirtual() {
            return virtual;
        }

        void setVirtual(boolean virtual) {
            this.virtual = virtual;
        }
    }
}
