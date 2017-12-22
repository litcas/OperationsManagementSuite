package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ResultLogEntity;
import com.rengu.operationsoanagementsuite.Repository.ResultLogRepository;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class ResultLogService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ResultLogRepository resultLogRepository;

    public void saveResultLog(ResultEntity resultEntity) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        ResultLogEntity resultLogEntity = new ResultLogEntity();
        resultLogEntity.setHostIP(httpServletRequest.getRemoteAddr());
        resultLogEntity.setRequestMethod(httpServletRequest.getMethod());
        resultLogEntity.setCode(resultEntity.getCode());
        resultLogEntity.setMessage(resultEntity.getMessage());
        resultLogEntity.setUsername(resultEntity.getUsername());
        resultLogEntity.setType(resultEntity.getType());
        resultLogEntity.setRequestUrl(httpServletRequest.getRequestURI());
        resultLogRepository.save(resultLogEntity);
        logger.info("返回结果日志：" + resultLogEntity.toString());
    }
}