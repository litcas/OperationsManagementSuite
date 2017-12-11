package com.rengu.operationsoanagementsuite.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class ComponentEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private Date createTime = new Date();
    @Column(nullable = false)
    private String name;
    private String version;
    private String description;
    private boolean latest;
    @OneToMany
    private List<UserEntity> userEntities;
    @OneToMany
    private List<ComponentFileEntity> componentFileEntities;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }

    public List<ComponentFileEntity> getComponentFileEntities() {
        return componentFileEntities;
    }

    public void setComponentFileEntities(List<ComponentFileEntity> componentFileEntities) {
        this.componentFileEntities = componentFileEntities;
    }
}
