package com.rengu.operationsoanagementsuite.Utils;

public class NotificationMessage {

    // 角色相关错误信息
    public static final String ROLE_NOT_FOUND = "role不存在。";
    public static final String ROLE_EXISTS = "role.name已存在。";
    public static final String ROLE_NAME_NOT_FOUND = "role.name不存在。";

    // 用户相关错误信息
    public static final String USER_NOT_FOUND = "user不存在。";
    public static final String USER_EXISTS = "user已存在。";
    public static final String USER_USERNAME_NOT_FOUND = "user.username不存在。";
    public static final String USER_PASSWORD_NOT_FOUND = "user.password不存在。";
    public static final String USER_DETETE = "user已删除。";

    // 工程相关错误信息
    public static final String PROJECT_NOT_FOUND = "project不存在。";
    public static final String PROJECT_EXISTS = "project已存在。";
    public static final String PROJECT_NAME_NOT_FOUND = "project.name不存在。";
    public static final String PROJECT_DELETE = "project已删除。";

    // 组件相关错误信息
    public static final String COMPONENT_EXISTS = "component已存在。";
    public static final String COMPONENT_NOT_FOUND = "component不存在。";
    public static final String COMPONENT_NAME_NOT_FOUND = "component.name不存在。";
    public static final String COMPONENT_VERSION_NOT_FOUND = "component.version不存在。";
    public static final String COMPONENT_DEPLOY_PATH_NOT_FOUND = "component.deploypath不存在。";
    public static final String COMPONENT_FILE_NOT_FOUND = "component.file不存在。";
    public static final String COMPONENT_DELETE = "component已删除。";

    // 部署设计相关错误信息
    public static final String DEPLOYMENT_DESIGN_EXISTS = "deploymentdesign已存在";
    public static final String DEPLOYMENT_DESIGN_NOT_FOUND = "deploymentdesign不存在";
    public static final String DEPLOYMENT_DESIGN_NAME_NOT_FOUND = "deploymentdesign.name不存在";
    public static final String DEPLOYMENT_DESIGN_DELETED = "deploymentdesign已删除";

    // 部署设计详情相关错误信息
    public static final String DEPLOYMENT_DESIGN_DETAIL_NOT_FOUND = "deploymentdesigndetail不存在";
    public static final String DEPLOYMENT_DESIGN_DETAIL_DELETED = "deploymentdesigndetail已删除";
    public static final String DEPLOYMENT_DESIGN_DETAIL_EXISTS = "deploymentdesigndetail已存在";

    // 设备相关错误
    public static final String DEVICE_EXISTS = "device已存在";
    public static final String DEVICE_NOT_FOUND = "device不存在";
    public static final String DEVICE_IP_NOT_FOUND = "device.ip不存在";
    public static final String DEVICE_DEPLOY_PATH_NOT_FOUND = "device.deployPath不存在";
    public static final String DEVICE_DELETED = "device已删除";

    // 组件包相关错误
    public static final String COMPONENT_PACKAGE_COMPONENTIDS_NOT_FOUND = "componentpackage.componentIds不存在";
    public static final String COMPONENT_PACKAGE_EXISTS = "componentpackage已存在";
    public static final String COMPONENT_PACKAGE_NOT_FOUND = "componentpackage不存在";
    public static final String COMPONENT_PACKAGE_DELETED = "componentpackage已删除";

    // 部署设计快照相关错误
    public static final String DEPLOYMENT_DESIGN_SNAPSHOT_NOT_FOUND = "deploymentdesignsnapshot不存在";
    public static final String DEPLOYMENT_DESIGN_SNAPSHOT_EXISTS = "deploymentdesignsnapshot已存在";
    public static final String DEPLOYMENT_DESIGN_SNAPSHOT_DELETED = "deploymentdesignsnapshot已删除";

    // 舱室设计相关错误
    public static final String CABIN_NOT_FOUND = "未发现舱室信息";
    public static final String CABIN_NAME_NOT_FOUND = "未发现舱室名称信息";
    public static final String CABIN_ID_NOT_FOUND = "未发现舱室ID信息";
    public static final String CABIN_POSITION_NOT_FOUND = "未发现舱室位置信息";
    public static final String CABIN_EXISTS = "舱室已存在";
    public static final String CABIN_DELETED = "舱室已删除";

    // 通用错误
    public static final String CACHE_CREAT_FAILED = "缓存文件创建失败。";
    public static final String COMPRESS_FAILED = "文件压缩失败。";
    public static final String PATH_ERROR = "路径确认信息接收异常";
    public static final String DEVICE_DEPLOYING = "该设备正在部署";
}