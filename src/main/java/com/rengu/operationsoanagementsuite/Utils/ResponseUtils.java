package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;

public class ResponseUtils {

    // 定义返回类型
    public static final String HTTPRESPONSE = "HTTP";

    // 请求成功，没有发生错误
    public static ResponseEntity ok(String responseType, UserEntity userEntity, Object object) {
        ResponseEntity responseEntity = new ResponseEntity(ResponseCodeEnum.OK);
        responseEntity.setUsername(userEntity.getUsername());
        responseEntity.setResponseType(responseType);
        responseEntity.setData(object);
        return responseEntity;
    }
}