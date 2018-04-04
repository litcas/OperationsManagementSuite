package com.rengu.operationsoanagementsuite.Entity;

public class TaskInfoEntity {
    private String pid;
    private String name;
    private String priority;
    private String description;

    public TaskInfoEntity() {
    }

    public TaskInfoEntity(String pid, String name, String priority, String description) {
        this.pid = pid;
        this.name = name;
        this.priority = priority;
        this.description = description;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
