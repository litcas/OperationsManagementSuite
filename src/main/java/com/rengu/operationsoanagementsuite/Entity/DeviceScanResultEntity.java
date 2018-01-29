package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class DeviceScanResultEntity {
    private String requestId;
    private String deviceId;
    private String componentId;
    private List<ComponentFileEntity> scanResult;
    private List<ComponentFileEntity> correctComponentFiles;
    private List<ComponentFileEntity> modifyedComponentFiles;
    private List<ComponentFileEntity> unknownFiles;
    private boolean hasCorrectComponentFiles;
    private boolean hasModifyedComponentFiles;
    private boolean hasUnknownFiles;
    private boolean hasMissingFile;

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

    public List<ComponentFileEntity> getScanResult() {
        return scanResult;
    }

    public void setScanResult(List<ComponentFileEntity> scanResult) {
        this.scanResult = scanResult;
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

    public List<ComponentFileEntity> getUnknownFiles() {
        return unknownFiles;
    }

    public void setUnknownFiles(List<ComponentFileEntity> unknownFiles) {
        this.unknownFiles = unknownFiles;
    }

    public boolean isHasCorrectComponentFiles() {
        return hasCorrectComponentFiles;
    }

    public void setHasCorrectComponentFiles(boolean hasCorrectComponentFiles) {
        this.hasCorrectComponentFiles = hasCorrectComponentFiles;
    }

    public boolean isHasModifyedComponentFiles() {
        return hasModifyedComponentFiles;
    }

    public void setHasModifyedComponentFiles(boolean hasModifyedComponentFiles) {
        this.hasModifyedComponentFiles = hasModifyedComponentFiles;
    }

    public boolean isHasUnknownFiles() {
        return hasUnknownFiles;
    }

    public void setHasUnknownFiles(boolean hasUnknownFiles) {
        this.hasUnknownFiles = hasUnknownFiles;
    }

    public boolean isHasMissingFile() {
        return hasMissingFile;
    }

    public void setHasMissingFile(boolean hasMissingFile) {
        this.hasMissingFile = hasMissingFile;
    }
}
