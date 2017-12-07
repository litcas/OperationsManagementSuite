package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ComponentService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(value = "/components")
public class ComponentController {
    @Autowired
    private ComponentService componentService;

    // 保存组件
    @PostMapping
    public ResultEntity saveComponent(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentArgs, @RequestParam(value = "componentfile") MultipartFile[] multipartFiles) throws MissingServletRequestParameterException, IOException, NoSuchAlgorithmException {
        ComponentEntity componentEntity = componentService.saveComponent(loginUser, componentArgs, multipartFiles);
        return ResultUtils.init(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, componentEntity);
    }

    // 查询组件
    @GetMapping
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser) {
        List<ComponentEntity> componentEntityList = componentService.getComponents();
        return ResultUtils.init(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentEntityList);
    }
}