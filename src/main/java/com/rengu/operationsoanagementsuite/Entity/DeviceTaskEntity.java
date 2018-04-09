package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class DeviceTaskEntity {

    private String id;
    private List<TaskInfoEntity> taskInfoEntities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TaskInfoEntity> getTaskInfoEntities() {
        return taskInfoEntities;
    }

    public void setTaskInfoEntities(List<TaskInfoEntity> taskInfoEntities) {
        this.taskInfoEntities = taskInfoEntities;
    }
}
