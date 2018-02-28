package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class TupleEntity {

    private int sendCount;
    private List<DeployFileEntity> errorFileList;

    public TupleEntity(int sendCount, List<DeployFileEntity> errorFileList) {
        this.sendCount = sendCount;
        this.errorFileList = errorFileList;
    }

    public int getSendCount() {
        return sendCount;
    }

    public List<DeployFileEntity> getErrorFileList() {
        return errorFileList;
    }
}
