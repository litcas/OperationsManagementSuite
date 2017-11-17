package com.rengu.operationsoanagementsuite.Aspect;

import com.rengu.operationsoanagementsuite.Entity.RequestLogEntity;
import com.rengu.operationsoanagementsuite.Repository.RequestLogRepository;
import com.rengu.operationsoanagementsuite.Utils.RequestUtils;
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
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RequestLogRepository requestLogRepository;

    @Autowired
    public HttpRequestLogAspect(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Pointcut("execution ( public * com.rengu.operationsoanagementsuite.Controller..*(..))")
    public void HttpRequestAspect() {
    }

    @Before("HttpRequestAspect()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        // 创建请求日志实体
        RequestLogEntity requestLogEntity = new RequestLogEntity();
        requestLogEntity.setRequestType(RequestUtils.HTTP);
        requestLogEntity.setIp(httpServletRequest.getRemoteAddr());
        requestLogEntity.setUrl(httpServletRequest.getRequestURI());
        requestLogEntity.setRequestMethod(httpServletRequest.getMethod());
        requestLogEntity.setResponseMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        requestLogRepository.save(requestLogEntity);
        // 日志输出请求记录
        logger.info(requestLogEntity.toString());
    }
}
