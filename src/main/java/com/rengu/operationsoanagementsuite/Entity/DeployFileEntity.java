package com.rengu.operationsoanagementsuite.Entity;

import java.io.File;
import java.io.Serializable;

public class DeployFileEntity implements Serializable {
    private String destPath;
    private File componentFile;

    public DeployFileEntity(String destPath, File componentFile) {
        this.destPath = destPath;
        this.componentFile = componentFile;
    }

    public String getDestPath() {
        return destPath;
    }

    public File getComponentFile() {
        return componentFile;
    }
}
