package com.rengu.operationsoanagementsuite.Entity;

import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.Objects;

public class HeartbeatEntity {

    private InetAddress inetAddress;
    private String CPUInfo;
    private long RAMSize;
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

    public long getRAMSize() {
        return RAMSize;
    }

    public void setRAMSize(long RAMSize) {
        this.RAMSize = RAMSize;
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
}
