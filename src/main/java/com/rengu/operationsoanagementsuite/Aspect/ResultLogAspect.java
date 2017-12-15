package com.rengu.operationsoanagementsuite.Aspect;

import com.rengu.operationsoanagementsuite.Service.ResultLogService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResultLogAspect {
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ResultLogService resultLogService;

    @Pointcut(value = "execution ( public * com.rengu.operationsoanagementsuite.Controller..*(..))")
    public void ResultLogAspect() {
    }

    @AfterReturning(returning = "result", pointcut = "ResultLogAspect()")
    public void doAfterReturning(ResultEntity result) {
        resultLogService.saveResultLog(result);
    }
}