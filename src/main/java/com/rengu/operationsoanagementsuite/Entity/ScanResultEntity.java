package com.rengu.operationsoanagementsuite.Entity;

import java.io.Serializable;
import java.util.List;

public class ScanResultEntity implements Serializable {
    private String id;
    private String deviceId;
    private String componentId;
    private List<ComponentDetailEntity> originalScanResultList;
    private List<ComponentDetailEntity> correctComponentFiles;
    private List<ComponentDetailEntity> modifyedComponentFiles;
    private List<ComponentDetailEntity> unknownFiles;
    private boolean hasCorrectComponentFiles;
    private boolean hasModifyedComponentFiles;
    private boolean hasUnknownFiles;
    private boolean hasMissingFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<ComponentDetailEntity> getOriginalScanResultList() {
        return originalScanResultList;
    }

    public void setOriginalScanResultList(List<ComponentDetailEntity> originalScanResultList) {
        this.originalScanResultList = originalScanResultList;
    }

    public List<ComponentDetailEntity> getCorrectComponentFiles() {
        return correctComponentFiles;
    }

    public void setCorrectComponentFiles(List<ComponentDetailEntity> correctComponentFiles) {
        this.correctComponentFiles = correctComponentFiles;
    }

    public List<ComponentDetailEntity> getModifyedComponentFiles() {
        return modifyedComponentFiles;
    }

    public void setModifyedComponentFiles(List<ComponentDetailEntity> modifyedComponentFiles) {
        this.modifyedComponentFiles = modifyedComponentFiles;
    }

    public List<ComponentDetailEntity> getUnknownFiles() {
        return unknownFiles;
    }

    public void setUnknownFiles(List<ComponentDetailEntity> unknownFiles) {
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
