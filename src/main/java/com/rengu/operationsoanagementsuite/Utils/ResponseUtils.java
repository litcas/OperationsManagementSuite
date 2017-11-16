package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.ResponseEntity;

public class ResponseUtils {
    public static ResponseEntity ok(String username, Object object) {
        ResponseEntity responseEntity = new ResponseEntity(StateCodeEnum.OK);
        responseEntity.setUsername(username);
        responseEntity.setData(object);
        return responseEntity;
    }

    public static ResponseEntity error(Exception exception) {
        ResponseEntity responseEntity = new ResponseEntity(StateCodeEnum.InternalServerError);
        responseEntity.setData(exception);
        return responseEntity;
    }
}