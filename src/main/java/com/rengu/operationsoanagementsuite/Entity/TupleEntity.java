package com.rengu.operationsoanagementsuite.Entity;

import java.util.List;

public class TupleEntity {

    private long sendSize;
    private List<DeployFileEntity> errorFileList;

    public TupleEntity(long sendSize, List<DeployFileEntity> errorFileList) {
        this.sendSize = sendSize;
        this.errorFileList = errorFileList;
    }

    public long getSendSize() {
        return sendSize;
    }

    public List<DeployFileEntity> getErrorFileList() {
        return errorFileList;
    }
}
