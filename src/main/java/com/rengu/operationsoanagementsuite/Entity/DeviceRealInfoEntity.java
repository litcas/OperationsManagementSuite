package com.rengu.operationsoanagementsuite.Entity;

import java.util.Objects;

public class DeviceRealInfoEntity {
    private String ip;
    private int count;

    public DeviceRealInfoEntity(String ip, int count) {
        this.ip = ip;
        this.count = count;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
        return Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ip);
    }
}
