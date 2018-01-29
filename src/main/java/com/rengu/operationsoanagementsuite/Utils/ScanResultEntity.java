package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;

import java.util.List;

public class ScanResultEntity {
    private String requestId;
    private String deviceId;
    private String componentId;
    private List<ComponentDetailEntity> componentDetailEntityList;

    public ScanResultEntity(String requestId, String deviceId, String componentId, List<ComponentDetailEntity> componentDetailEntityList) {
        this.requestId = requestId;
        this.deviceId = deviceId;
        this.componentId = componentId;
        this.componentDetailEntityList = componentDetailEntityList;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public List<ComponentDetailEntity> getComponentDetailEntityList() {
        return componentDetailEntityList;
    }

    public void setComponentDetailEntityList(List<ComponentDetailEntity> componentDetailEntityList) {
        this.componentDetailEntityList = componentDetailEntityList;
    }
}
