package com.rengu.operationsoanagementsuite.Entity;

import java.net.InetAddress;
import java.util.Objects;

public class DeviceRealInfoEntity {
    private InetAddress inetAddress;
    private int port;
    private int count;

    public DeviceRealInfoEntity(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceRealInfoEntity that = (DeviceRealInfoEntity) o;
        return port == that.port &&
                Objects.equals(inetAddress, that.inetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress, port);
    }
}
