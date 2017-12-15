package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import org.springframework.http.HttpStatus;

public class ResultUtils {

    // 定义返回类型
    public static final String HTTPRESPONSE = "HTTP";
    public static final String ERROR = "ERROR";

    // 创建ResultEntity
    public static ResultEntity resultBuilder(HttpStatus httpStatus, String type, UserEntity loginUser, Object object) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(httpStatus.value());
        resultEntity.setMessage(httpStatus.getReasonPhrase());
        resultEntity.setType(type);
        resultEntity.setUsername(loginUser == null ? null : loginUser.getUsername());
        resultEntity.setData(object);
        return resultEntity;
    }
}