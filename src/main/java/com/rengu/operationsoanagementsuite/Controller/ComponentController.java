package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ComponentService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/components")
public class ComponentController {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentService componentService;

    @PostMapping
    public ResultEntity saveComponent(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentArgs) throws MissingServletRequestParameterException {
        ComponentEntity componentEntity = componentService.saveComponent(componentArgs, loginUser);
        return ResultUtils.init(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, componentEntity);
    }
}