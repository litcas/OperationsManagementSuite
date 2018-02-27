package com.rengu.operationsoanagementsuite.Utils;

import org.springframework.http.HttpStatus;

import java.security.Principal;

public class ResultUtils {

    // 创建ResultEntity
    public static ResultEntity resultBuilder(String username, int code, String message, Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setUsername(username);
        resultEntity.setCode(code);
        resultEntity.setMessage(message);
        resultEntity.setData(data);
        return resultEntity;
    }

    // 创建ResultEntity
    public static ResultEntity resultBuilder(UserEntity userEntity, HttpStatus httpStatus, Object data) {
        String username = userEntity == null ? "" : userEntity.getUsername();
        int code = httpStatus.value();
        String message = httpStatus.getReasonPhrase();
        return resultBuilder(username, code, message, data);
    }

    // 创建ResultEntity
    public static ResultEntity resultBuilder(Principal principal, HttpStatus httpStatus, Throwable throwable) {
        String username = principal == null ? null : principal.getName();
        int code = httpStatus.value();
        String message = httpStatus.getReasonPhrase();
        String data = throwable == null ? null : throwable.getMessage();
        return resultBuilder(username, code, message, data);
    }
}
