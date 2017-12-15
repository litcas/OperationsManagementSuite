package com.rengu.operationsoanagementsuite.Configuration;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rengu.config")
public class ServerConfiguration {
    // todo 修改为从配置文件获取属性值
    private String defultUserRole = "ROLE_USER";
    private String defultAdminRole = "ROLE_ADMIN";
    private String defultUserName = "admin";
    private String defultPassword = "admin";
    // 默认设置为系统缓存目录
    private String libraryPath = FileUtils.getTempDirectoryPath();
    // 组件实体存放目录名称
    private String libraryFolderName = "ComponentLibraries";
    // 组件名称与版本号分隔符
    private String nameSeparator = "-";
    // 导出部分字段修改，会导致无法导入
    // 组件导出压缩包文件名
    private String exportFileName = "exportcomponent";
    // 组件导出描述文件名称（xxx.json）
    private String exportDescriptionFileName = "description";
    // 组件导出实体文件夹名称
    private String exportComponentFileName = "componentFiles";

    public String getDefultUserRole() {
        return defultUserRole;
    }

    public void setDefultUserRole(String defultUserRole) {
        this.defultUserRole = defultUserRole;
    }

    public String getDefultAdminRole() {
        return defultAdminRole;
    }

    public void setDefultAdminRole(String defultAdminRole) {
        this.defultAdminRole = defultAdminRole;
    }

    public String getDefultUserName() {
        return defultUserName;
    }

    public void setDefultUserName(String defultUserName) {
        this.defultUserName = defultUserName;
    }

    public String getDefultPassword() {
        return defultPassword;
    }

    public void setDefultPassword(String defultPassword) {
        this.defultPassword = defultPassword;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    public String getLibraryFolderName() {
        return libraryFolderName;
    }

    public void setLibraryFolderName(String libraryFolderName) {
        this.libraryFolderName = libraryFolderName;
    }

    public String getNameSeparator() {
        return nameSeparator;
    }

    public void setNameSeparator(String nameSeparator) {
        this.nameSeparator = nameSeparator;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public String getExportDescriptionFileName() {
        return exportDescriptionFileName;
    }

    public void setExportDescriptionFileName(String exportDescriptionFileName) {
        this.exportDescriptionFileName = exportDescriptionFileName;
    }

    public String getExportComponentFileName() {
        return exportComponentFileName;
    }

    public void setExportComponentFileName(String exportComponentFileName) {
        this.exportComponentFileName = exportComponentFileName;
    }
}