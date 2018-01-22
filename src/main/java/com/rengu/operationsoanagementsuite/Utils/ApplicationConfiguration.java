package com.rengu.operationsoanagementsuite.Utils;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    private String defultUsername = "admin";
    private String defultPassword = "admin";
    private String componentLibraryName = "CL";
    private String separator = "-";
    private String jsonFileName = "export.json";
    private String compressFileName = "ExportComponent.zip";
    // 不可修改项-自动从运行环境获取
    private String componentLibraryPath = "";

    public String getDefultUsername() {
        return defultUsername;
    }

    public void setDefultUsername(String defultUsername) {
        this.defultUsername = defultUsername;
    }

    public String getDefultPassword() {
        return defultPassword;
    }

    public void setDefultPassword(String defultPassword) {
        this.defultPassword = defultPassword;
    }

    public String getComponentLibraryName() {
        return componentLibraryName;
    }

    public void setComponentLibraryName(String componentLibraryName) {
        this.componentLibraryName = componentLibraryName;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getJsonFileName() {
        return jsonFileName;
    }

    public void setJsonFileName(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    public String getCompressFileName() {
        return compressFileName;
    }

    public void setCompressFileName(String compressFileName) {
        this.compressFileName = compressFileName;
    }

    public String getComponentLibraryPath() {
        return componentLibraryPath;
    }

    public void setComponentLibraryPath(String componentLibraryPath) {
        this.componentLibraryPath = componentLibraryPath;
    }
}