package com.rengu.operationsoanagementsuite.Aspect;

import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Service.RequestLogService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestLogAspect {

    private final RequestLogService requestLogService;

    @Autowired
    public RequestLogAspect(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }

    @Pointcut(value = "execution ( public * com.rengu.operationsoanagementsuite.Controller..*(..))")
    public void RequestLogPointcut() {
    }

    @AfterReturning(returning = "resultEntity", pointcut = "RequestLogPointcut()")
    public void doAfterReturning(ResultEntity resultEntity) {
        requestLogService.saveRequestLogs(resultEntity);
    }
}