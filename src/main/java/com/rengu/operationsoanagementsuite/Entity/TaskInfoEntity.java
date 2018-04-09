package com.rengu.operationsoanagementsuite.Entity;

public class TaskInfoEntity {
    private String pid;
    private String name;

    public TaskInfoEntity() {
    }

    public TaskInfoEntity(String pid, String name) {
        this.pid = pid;
        this.name = name;
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
}
