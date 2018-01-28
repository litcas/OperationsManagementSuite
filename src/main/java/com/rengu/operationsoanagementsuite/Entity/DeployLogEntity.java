package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class DeployLogEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private boolean started = false;
    private boolean finished = false;
    private long size;
    private long finishedSize;
    private double speed = 0;
    private int fileNums;
    private int finishedNums;
    @ManyToOne
    private DeviceEntity deviceEntity;
    @ManyToOne
    private DeployPlanEntity deployPlanEntity;
    @OneToMany
    private List<ComponentEntity> componentEntities;
    @ManyToOne
    private ProjectEntity projectEntity;

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

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getFinishedSize() {
        return finishedSize;
    }

    public void setFinishedSize(long finishedSize) {
        this.finishedSize = finishedSize;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getFileNums() {
        return fileNums;
    }

    public void setFileNums(int fileNums) {
        this.fileNums = fileNums;
    }

    public int getFinishedNums() {
        return finishedNums;
    }

    public void setFinishedNums(int finishedNums) {
        this.finishedNums = finishedNums;
    }

    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    public DeployPlanEntity getDeployPlanEntity() {
        return deployPlanEntity;
    }

    public void setDeployPlanEntity(DeployPlanEntity deployPlanEntity) {
        this.deployPlanEntity = deployPlanEntity;
    }

    public List<ComponentEntity> getComponentEntities() {
        return componentEntities;
    }

    public void setComponentEntities(List<ComponentEntity> componentEntities) {
        this.componentEntities = componentEntities;
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }
}
