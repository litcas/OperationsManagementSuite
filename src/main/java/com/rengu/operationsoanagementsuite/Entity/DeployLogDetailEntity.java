package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
public class DeployLogDetailEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String destPath;
    @ManyToOne
    private ComponentDetailEntity componentDetailEntity;

    public DeployLogDetailEntity() {
    }

    public DeployLogDetailEntity(String destPath, ComponentDetailEntity componentDetailEntity) {
        this.destPath = destPath;
        this.componentDetailEntity = componentDetailEntity;
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

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public ComponentDetailEntity getComponentDetailEntity() {
        return componentDetailEntity;
    }

    public void setComponentDetailEntity(ComponentDetailEntity componentDetailEntity) {
        this.componentDetailEntity = componentDetailEntity;
    }
}
