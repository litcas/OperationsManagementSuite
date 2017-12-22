package com.rengu.operationsoanagementsuite.Utils;

import java.util.Objects;

public class DeviceRealInfoEntity {
    private String ip;
    private String platform;
    private int count;

    public DeviceRealInfoEntity(String ip, String platform, int count) {
        this.ip = ip;
        this.platform = platform;
        this.count = count;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
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

        return Objects.hash(ip, platform, count);
    }
}
