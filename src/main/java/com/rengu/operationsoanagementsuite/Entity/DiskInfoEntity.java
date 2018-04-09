package com.rengu.operationsoanagementsuite.Entity;

public class DiskInfoEntity {

    private String name;
    private double size;
    private double usedSize;

    public DiskInfoEntity() {
    }

    public DiskInfoEntity(String name, double size, double usedSize) {
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

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(double usedSize) {
        this.usedSize = usedSize;
    }
}
