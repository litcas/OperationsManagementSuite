package com.rengu.operationsoanagementsuite.Entity;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUploadFormEntity {
    private List<MultipartFile> multipartFiles;

    public List<MultipartFile> getMultipartFiles() {
        return multipartFiles;
    }

    public void setMultipartFiles(List<MultipartFile> multipartFiles) {
        this.multipartFiles = multipartFiles;
    }
}
