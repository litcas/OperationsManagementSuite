package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class DeviceScanResultEntity {
    private String requestId;
    private String deviceId;
    private String componentId;
    private List<FileEntity> fileEntityList;
    private List<ComponentFileEntity> correctComponentFiles;
    private List<ComponentFileEntity> modifyedComponentFiles;
    private List<FileEntity> unknownFiles;

    public DeviceScanResultEntity() {
    }

    public DeviceScanResultEntity(String requestId, String deviceId, String componentId) {
        this.requestId = requestId;
        this.deviceId = deviceId;
        this.componentId = componentId;
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

    public List<FileEntity> getFileEntityList() {
        return fileEntityList;
    }

    public void setFileEntityList(List<FileEntity> fileEntityList) {
        this.fileEntityList = fileEntityList;
    }

    public List<ComponentFileEntity> getCorrectComponentFiles() {
        return correctComponentFiles;
    }

    public void setCorrectComponentFiles(List<ComponentFileEntity> correctComponentFiles) {
        this.correctComponentFiles = correctComponentFiles;
    }

    public List<ComponentFileEntity> getModifyedComponentFiles() {
        return modifyedComponentFiles;
    }

    public void setModifyedComponentFiles(List<ComponentFileEntity> modifyedComponentFiles) {
        this.modifyedComponentFiles = modifyedComponentFiles;
    }

    public List<FileEntity> getUnknownFiles() {
        return unknownFiles;
    }

    public void setUnknownFiles(List<FileEntity> unknownFiles) {
        this.unknownFiles = unknownFiles;
    }
}
