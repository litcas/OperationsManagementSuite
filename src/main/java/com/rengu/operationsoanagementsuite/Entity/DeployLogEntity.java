package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class DeployLogEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String ip;
    private String path;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;
    private long time;
    private double transferRate;
    private String state;
    @OneToOne
    private ComponentEntity componentEntity;
    @OneToMany(cascade = CascadeType.ALL)
    private List<DeployLogDetailEntity> errorFileList;
    @OneToMany(cascade = CascadeType.ALL)
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getTransferRate() {
        return transferRate;
    }

    public void setTransferRate(double transferRate) {
        this.transferRate = transferRate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ComponentEntity getComponentEntity() {
        return componentEntity;
    }

    public void setComponentEntity(ComponentEntity componentEntity) {
        this.componentEntity = componentEntity;
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