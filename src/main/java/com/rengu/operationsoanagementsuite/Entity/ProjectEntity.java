package com.rengu.operationsoanagementsuite.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class ProjectEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @OneToMany
    private List<UserEntity> userEntities;
    @OneToMany
    private List<DeployPlanEntity> deployPlanEntities;

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

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }

    public List<DeployPlanEntity> getDeployPlanEntities() {
        return deployPlanEntities;
    }

    public void setDeployPlanEntities(List<DeployPlanEntity> deployPlanEntities) {
        this.deployPlanEntities = deployPlanEntities;
    }
}
