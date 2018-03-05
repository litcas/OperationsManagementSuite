package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DeployResultEntity implements Serializable {

    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private ComponentEntity componentEntity;
    private List<DeployFileEntity> errorFileList;
    private List<DeployFileEntity> completedFileList;

    public DeployResultEntity(ComponentEntity componentEntity, List<DeployFileEntity> errorFileList, List<DeployFileEntity> completedFileList) {
        this.componentEntity = componentEntity;
        this.errorFileList = errorFileList;
        this.completedFileList = completedFileList;
    }

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

    public ComponentEntity getComponentEntity() {
        return componentEntity;
    }

    public void setComponentEntity(ComponentEntity componentEntity) {
        this.componentEntity = componentEntity;
    }

    public List<DeployFileEntity> getErrorFileList() {
        return errorFileList;
    }

    public void setErrorFileList(List<DeployFileEntity> errorFileList) {
        this.errorFileList = errorFileList;
    }

    public List<DeployFileEntity> getCompletedFileList() {
        return completedFileList;
    }

    public void setCompletedFileList(List<DeployFileEntity> completedFileList) {
        this.completedFileList = completedFileList;
    }
}
