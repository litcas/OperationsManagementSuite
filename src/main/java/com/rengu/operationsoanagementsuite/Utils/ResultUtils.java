package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import org.springframework.http.HttpStatus;

public class ResultUtils {

    // 定义返回类型
    public static final String HTTPRESPONSE = "HTTP";
    public static final String ERROR = "ERROR";

    // 创建ResultEntity
    private static ResultEntity resultBuilder(int code, String message, String type, String username, Object object) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(message);
        resultEntity.setType(type);
        resultEntity.setUsername(username);
        resultEntity.setData(object);
        return resultEntity;
    }

    public static ResultEntity init(HttpStatus httpStatus, String type, UserEntity loginUser, Object object) {
        return resultBuilder(httpStatus.value(), httpStatus.getReasonPhrase(), type, loginUser.getUsername(), object);
    }

    public static ResultEntity init(HttpStatus httpStatus, String type, Object object) {
        return resultBuilder(httpStatus.value(), httpStatus.getReasonPhrase(), type, "", object);
    }
}