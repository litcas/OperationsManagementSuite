package com.rengu.operationsoanagementsuite.Entity;

public class DiskInfoEntity {

    private String name;
    private int size;
    private int usedSize;

    public DiskInfoEntity() {
    }

    public DiskInfoEntity(String name, int size, int usedSize) {
        this.name = name;
        this.size = size;
        this.usedSize = usedSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(int usedSize) {
        this.usedSize = usedSize;
    }
}
