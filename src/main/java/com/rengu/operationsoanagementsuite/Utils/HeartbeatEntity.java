package com.rengu.operationsoanagementsuite.Utils;

import java.net.InetAddress;

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
}
