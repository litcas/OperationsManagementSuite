package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RequestLogEntity;
import com.rengu.operationsoanagementsuite.Repository.RequestLogRepository;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
public class RequestLogService {

    @Autowired
    private RequestLogRepository requestLogRepository;

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
        requestLogRepository.save(requestLogEntity);
    }
}