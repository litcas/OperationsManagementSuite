package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class DeploymentDesignSnapshotEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String name;
    private String description;
    @Transient
    private double progress;
    @ManyToOne
    private ProjectEntity projectEntity;
    @OneToMany(cascade = CascadeType.ALL)
    private List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshots;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DeploymentDesignSnapshotEntity that = (DeploymentDesignSnapshotEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public List<DeploymentDesignSnapshotDetailEntity> getDeploymentDesignSnapshots() {
        return deploymentDesignSnapshots;
    }

    public void setDeploymentDesignSnapshots(List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshots) {
        this.deploymentDesignSnapshots = deploymentDesignSnapshots;
    }
}
