package com.rengu.operationsoanagementsuite.Entity;

import java.net.InetAddress;
import java.util.Objects;

public class HeartbeatEntity {
    private InetAddress inetAddress;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        HeartbeatEntity that = (HeartbeatEntity) object;
        return Objects.equals(inetAddress, that.inetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress);
    }
}
