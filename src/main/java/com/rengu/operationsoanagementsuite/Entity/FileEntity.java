package com.rengu.operationsoanagementsuite.Entity;

public class FileEntity {
    private String filePath;
    private String md5;

    public FileEntity() {
    }

    public FileEntity(String filePath, String md5) {
        this.filePath = filePath;
        this.md5 = md5;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
