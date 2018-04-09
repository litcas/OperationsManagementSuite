package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class DeviceDiskEntity {

    private String id;
    private List<DiskInfoEntity> diskInfoEntities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DiskInfoEntity> getDiskInfoEntities() {
        return diskInfoEntities;
    }

    public void setDiskInfoEntities(List<DiskInfoEntity> diskInfoEntities) {
        this.diskInfoEntities = diskInfoEntities;
    }
}
