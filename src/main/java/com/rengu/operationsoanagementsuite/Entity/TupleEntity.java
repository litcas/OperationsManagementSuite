package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class TupleEntity {

    private int sendCount;
    private List<DeployFileEntity> errorFileList;
    private List<DeployFileEntity> completedFileList;

    public TupleEntity(int sendCount, List<DeployFileEntity> errorFileList, List<DeployFileEntity> completedFileList) {
        this.sendCount = sendCount;
        this.errorFileList = errorFileList;
        this.completedFileList = completedFileList;
    }

    public int getSendCount() {
        return sendCount;
    }

    public List<DeployFileEntity> getErrorFileList() {
        return errorFileList;
    }

    public List<DeployFileEntity> getCompletedFileList() {
        return completedFileList;
    }
}
