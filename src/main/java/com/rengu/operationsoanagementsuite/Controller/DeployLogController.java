package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeployLogService;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/deploylogs")
public class DeployLogController {

    private final DeployLogService deployLogService;

    @Autowired
    public DeployLogController(DeployLogService deployLogService) {
        this.deployLogService = deployLogService;
    }

    @GetMapping
    public ResultEntity getDeployLogs(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "componentName", required = false) String componentName, DeployLogEntity deployLogEntity, @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deployLogService.getDeployLogs(deployLogEntity, componentName, startTime, endTime));
    }
}
