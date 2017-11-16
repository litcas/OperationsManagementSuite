package com.rengu.operationsoanagementsuite.Aspect;

import com.rengu.operationsoanagementsuite.Entity.HttpRequestLogEntity;
import com.rengu.operationsoanagementsuite.Repository.HttpRequestLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HttpRequestLogAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HttpRequestLogRepository httpRequestLogRepository;

    @Autowired
    public HttpRequestLogAspect(HttpRequestLogRepository httpRequestLogRepository) {
        this.httpRequestLogRepository = httpRequestLogRepository;
    }

    @Pointcut("execution ( public * com.rengu.operationsoanagementsuite.Controller..*(..))")
    public void HttpRequestLogAspect() {
    }

    @Before("HttpRequestLogAspect()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        // 创建请求日志实体
        HttpRequestLogEntity httpRequestLogEntity = new HttpRequestLogEntity();
        httpRequestLogEntity.setIp(httpServletRequest.getRemoteAddr());
        httpRequestLogEntity.setUrl(httpServletRequest.getRequestURI());
        httpRequestLogEntity.setHttpMethod(httpServletRequest.getMethod());
        httpRequestLogEntity.setClassMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        httpRequestLogEntity.setArgs(httpServletRequest.getParameterMap().toString());
        httpRequestLogRepository.save(httpRequestLogEntity);
    }
}
