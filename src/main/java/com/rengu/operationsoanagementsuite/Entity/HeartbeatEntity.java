package com.rengu.operationsoanagementsuite.Entity;

import java.net.InetAddress;
import java.util.Objects;

public class HeartbeatEntity {

    private InetAddress inetAddress;
    private String CPUInfo;
    private String CPUClock;
    private String CPUUtilization;
    private int RAMSize;
    private int freeRAMSize;
    private int count;

    public HeartbeatEntity(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        this.count = 3;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public String getCPUInfo() {
        return CPUInfo;
    }

    public void setCPUInfo(String CPUInfo) {
        this.CPUInfo = CPUInfo;
    }

    public String getCPUClock() {
        return CPUClock;
    }

    public void setCPUClock(String CPUClock) {
        this.CPUClock = CPUClock;
    }

    public String getCPUUtilization() {
        return CPUUtilization;
    }

    public void setCPUUtilization(String CPUUtilization) {
        this.CPUUtilization = CPUUtilization;
    }

    public int getRAMSize() {
        return RAMSize;
    }

    public void setRAMSize(int RAMSize) {
        this.RAMSize = RAMSize;
    }

    public int getFreeRAMSize() {
        return freeRAMSize;
    }

    public void setFreeRAMSize(int freeRAMSize) {
        this.freeRAMSize = freeRAMSize;
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
        HeartbeatEntity that = (HeartbeatEntity) o;
        return Objects.equals(inetAddress, that.inetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress);
    }

    @Override
    public String toString() {
        return "HeartbeatEntity{" +
                "inetAddress=" + inetAddress +
                ", CPUInfo='" + CPUInfo + '\'' +
                ", CPUClock='" + CPUClock + '\'' +
                ", CPUUtilization='" + CPUUtilization + '\'' +
                ", RAMSize=" + RAMSize +
                ", freeRAMSize=" + freeRAMSize +
                ", count=" + count +
                '}';
    }
}
