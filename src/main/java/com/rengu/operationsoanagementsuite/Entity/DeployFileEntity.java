package com.rengu.operationsoanagementsuite.Entity;

import java.io.Serializable;

public class DeployFileEntity implements Serializable {

    private ComponentEntity componentEntity;
    private ComponentDetailEntity componentDetailEntity;
    private String destPath;

    public DeployFileEntity(ComponentEntity componentEntity, ComponentDetailEntity componentDetailEntity, String destPath) {
        this.componentEntity = componentEntity;
        this.componentDetailEntity = componentDetailEntity;
        this.destPath = destPath;
    }

    public ComponentEntity getComponentEntity() {
        return componentEntity;
    }

    public ComponentDetailEntity getComponentDetailEntity() {
        return componentDetailEntity;
    }

    public String getDestPath() {
        return destPath;
    }
}
