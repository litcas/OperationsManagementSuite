package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class DeviceEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String name;
    private String ip;
    private String description;
    private int UDPPort = ApplicationConfiguration.deviceUDPPort;
    private int TCPPort = ApplicationConfiguration.deviceTCPPort;
    private String deployPath;
    @ManyToOne
    private ProjectEntity projectEntity;
    // 设备基本信息
    @Transient
    private String CPUInfo;
    @Transient
    private String CPUClock;
    @Transient
    private String CPUUtilization;
    @Transient
    private int RAMSize;
    @Transient
    private int freeRAMSize;
    // 设备部署信息
    @Transient
    private boolean online = false;
    @Transient
    private boolean virtual = false;
    @Transient
    private double transferRate;
    @Transient
    private double progress;
    @Transient
    private List<DeployLogDetailEntity> errorFileList;
    @Transient
    private List<DeployLogDetailEntity> completedFileList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUDPPort() {
        return UDPPort;
    }

    public void setUDPPort(int UDPPort) {
        this.UDPPort = UDPPort;
    }

    public int getTCPPort() {
        return TCPPort;
    }

    public void setTCPPort(int TCPPort) {
        this.TCPPort = TCPPort;
    }

    public String getDeployPath() {
        return deployPath;
    }

    public void setDeployPath(String deployPath) {
        this.deployPath = deployPath;
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public String getCPUInfo() {
        return CPUInfo;
    }

    public void setCPUInfo(String CPUInfo) {
        this.CPUInfo = CPUInfo;
    }

    public String getCPUClock() {
        return CPUClock;
    }

    public void setCPUClock(String CPUClock) {
        this.CPUClock = CPUClock;
    }

    public String getCPUUtilization() {
        return CPUUtilization;
    }

    public void setCPUUtilization(String CPUUtilization) {
        this.CPUUtilization = CPUUtilization;
    }

    public int getRAMSize() {
        return RAMSize;
    }

    public void setRAMSize(int RAMSize) {
        this.RAMSize = RAMSize;
    }

    public int getFreeRAMSize() {
        return freeRAMSize;
    }

    public void setFreeRAMSize(int freeRAMSize) {
        this.freeRAMSize = freeRAMSize;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public double getTransferRate() {
        return transferRate;
    }

    public void setTransferRate(double transferRate) {
        this.transferRate = transferRate;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DeviceEntity that = (DeviceEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
