package com.rengu.operationsoanagementsuite.Entity;

import java.io.Serializable;
import java.util.List;

public class DeployStatusEntity implements Serializable {
    private String ip;
    private boolean deploying = true;
    private double progress = 0;
    private double transferRate = 0;
    private List<DeployLogDetailEntity> errorFileList;
    private List<DeployLogDetailEntity> completedFileList;

    public DeployStatusEntity() {
    }

    public DeployStatusEntity(String ip) {
        this.ip = ip;
        this.deploying = true;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isDeploying() {
        return deploying;
    }

    public void setDeploying(boolean deploying) {
        this.deploying = deploying;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getTransferRate() {
        return transferRate;
    }

    public void setTransferRate(double transferRate) {
        this.transferRate = transferRate;
    }

    public List<DeployLogDetailEntity> getErrorFileList() {
        return errorFileList;
    }

    public void setErrorFileList(List<DeployLogDetailEntity> errorFileList) {
        this.errorFileList = errorFileList;
    }

    public List<DeployLogDetailEntity> getCompletedFileList() {
        return completedFileList;
    }

    public void setCompletedFileList(List<DeployLogDetailEntity> completedFileList) {
        this.completedFileList = completedFileList;
    }
}