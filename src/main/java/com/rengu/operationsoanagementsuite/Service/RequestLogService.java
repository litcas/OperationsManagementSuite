package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RequestLogEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Repository.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
public class RequestLogService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RequestLogRepository requestLogRepository;

    @Autowired
    public RequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    // 新增请求日志
    @Transactional
    public void saveRequestLogs(ResultEntity resultEntity) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        RequestLogEntity requestLogEntity = new RequestLogEntity();
        requestLogEntity.setHostName(httpServletRequest.getRemoteAddr());
        requestLogEntity.setRequestMethod(httpServletRequest.getMethod());
        requestLogEntity.setCode(resultEntity.getCode());
        requestLogEntity.setMessage(resultEntity.getMessage());
        requestLogEntity.setUsername(resultEntity.getUsername());
        requestLogEntity.setRequestUrl(httpServletRequest.getRequestURI());
        requestLogEntity.setUserAgent(httpServletRequest.getHeader("User-Agent"));
        requestLogRepository.save(requestLogEntity);
        logger.info("用户：" + resultEntity.getUsername() + "|--->调用接口：" + httpServletRequest.getMethod() + ":" + httpServletRequest.getRequestURI() + "|--->状态：" + resultEntity.getCode());
    }
}