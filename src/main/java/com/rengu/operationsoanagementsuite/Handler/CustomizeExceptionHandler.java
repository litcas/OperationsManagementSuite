package com.rengu.operationsoanagementsuite.Handler;

import com.rengu.operationsoanagementsuite.Entity.ResponseEntity;
import com.rengu.operationsoanagementsuite.Utils.ResponseUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomizeExceptionHandler {
    // 异常处理方法
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity customizeExceptionHandler(RuntimeException runtimeException) {
        return ResponseUtils.error(runtimeException);
    }
}