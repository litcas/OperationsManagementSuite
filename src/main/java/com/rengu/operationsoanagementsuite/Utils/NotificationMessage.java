package com.rengu.operationsoanagementsuite.Utils;

public class NotificationMessage {
    // 用户相关
    public static final String USER_NOT_FOUND = "参数解析异常：user不存在.";
    public static final String USER_ID_NOT_FOUND = "参数解析异常：user.id不存在.";
    public static final String USER_NAME_NOT_FOUND = "参数解析异常：user.username不存在.";
    public static final String USER_PASSWORD_NOT_FOUND = "参数解析异常：user.password不存在.";

    // 角色相关
    public static final String ROLE_EXISTS = "参数解析异常：role已存在.";
    public static final String ROLE_NOT_FOUND = "参数解析异常：role不存在.";
    public static final String ROLE_ID_NOT_FOUND = "参数解析异常：role.id不存在.";
    public static final String ROLE_NAME_NOT_FOUND = "参数解析异常：role.name不存在.";

    // 组件相关
    public static final String COMPONENT_EXISTS = "参数解析异常：component已存在.";
    public static final String COMPONENT_VERSION_EXISTS = "参数解析异常：component.version已存在.";
    public static final String COMPONENT_NOT_FOUND = "参数解析异常：component不存在.";
    public static final String COMPONENT_ID_NOT_FOUND = "参数解析异常：component.id不存在.";
    public static final String COMPONENT_NAME_NOT_FOUND = "参数解析异常：component.name不存在.";
    public static final String COMPONENT_UPLOAD_FILE_NOT_FOUND = "参数解析异常：component.uploadfile不存在.";
    public static final String COMPONENT_UPLOAD_FILE_PATH_NOT_FOUND = "参数解析异常：component.uploadfilepath不存在.";

    // 工程相关
    public static final String PROJECT_EXISTS = "参数解析异常：project已存在.";
    public static final String PROJECT_NOT_FOUND = "参数解析异常：project不存在.";
    public static final String PROJECT_ID_NOT_FOUND = "参数解析异常：project.id不存在.";
    public static final String PROJECT_NAME_NOT_FOUND = "参数解析异常：project.name不存在.";

    // 部署设计相关
    public static final String DEPLOY_PLAN_EXISTS = "参数解析异常：deployplan已存在.";
    public static final String DEPLOY_PLAN_NOT_FOUND = "参数解析异常：deployplan不存在.";
    public static final String DEPLOY_PLAN_ID_NOT_FOUND = "参数解析异常：deployplan.id不存在.";
    public static final String DEPLOY_PLAN_NAME_NOT_FOUND = "参数解析异常：deployplan.name不存在.";
    public static final String DEPLOY_PLAN_DEPLOY_PATH_NOT_FOUND = "参数解析异常：deployplan.deploypath不存在.";

    // 设备相关
    public static final String DEVICE_EXISTS = "参数解析异常：device已存在.";
    public static final String DEVICE_IP_EXISTS = "参数解析异常：device已存在.";
    public static final String DEVICE_NOT_FOUND = "参数解析异常：device不存在.";
    public static final String DEVICE_ID_NOT_FOUND = "参数解析异常：device.id不存在.";
    public static final String DEVICE_IP_NOT_FOUND = "参数解析异常：device.ip不存在.";
    public static final String DEVICE_NAME_NOT_FOUND = "参数解析异常：device.name不存在.";

    public static String userDeleteMessage(String userId) {
        return "删除id为" + userId + "的用户成功。";
    }

    public static String projectDeleteMessage(String projectId) {
        return "删除id为" + projectId + "的工程成功。";
    }

    public static String deviceDeleteMessage(String deviceId) {
        return "删除id为" + deviceId + "的设备成功。";
    }

    public static String deployplanDeleteMessage(String deployplanId) {
        return "删除id为" + deployplanId + "的部署设计成功。";
    }
}
