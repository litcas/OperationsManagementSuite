package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import org.springframework.http.HttpStatus;

public class ResultUtils {

    // 定义返回类型
    public static final String HTTPRESPONSE = "HTTP";
    public static final String UDP = "UDP";
    public static final String ERROR = "ERROR";

    // 创建ResultEntity
    private static ResultEntity resultBuilder(HttpStatus httpStatus, String type, String username, Object object) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(httpStatus.value());
        resultEntity.setMessage(httpStatus.getReasonPhrase());
        resultEntity.setType(type);
        resultEntity.setUsername(username);
        resultEntity.setData(object);
        return resultEntity;
    }

    public static ResultEntity resultBuilder(HttpStatus httpStatus, String type, UserEntity loginUser, Object object) {
        String username = loginUser == null ? "" : loginUser.getUsername();
        return resultBuilder(httpStatus, type, username, object);
    }
}