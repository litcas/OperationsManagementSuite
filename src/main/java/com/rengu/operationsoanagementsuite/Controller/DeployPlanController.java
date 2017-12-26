package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeployPlanService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/deployplan")
public class DeployPlanController {
    @Autowired
    private DeployPlanService deployPlanService;

    // 保存部署设计
    @PostMapping
    public ResultEntity saveDeployPlans(@AuthenticationPrincipal UserEntity loginUser, String projectId, DeployPlanEntity deployPlanEntity) {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.saveDeployPlans(projectId, deployPlanEntity));
    }

    // 删除部署设计
    @DeleteMapping(value = "/{deployplanId}")
    public ResultEntity deleteDeployPlans(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId) {
        deployPlanService.deleteDeployPlans(deployplanId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, NotificationMessage.deployplanDeleteMessage(deployplanId));
    }

    // 修改部署设计
    @PatchMapping(value = "/{deployplanId}")
    public ResultEntity updateDeployPlans(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, DeployPlanEntity deployPlanArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.updateDeployPlans(deployplanId, deployPlanArgs));
    }

    // 查看部署设计
    @GetMapping(value = "/{deployplanId}")
    public ResultEntity getDeployPlan(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlan(deployplanId));
    }

    // 搜索部署设计
    @GetMapping
    public ResultEntity getDeployPlans(@AuthenticationPrincipal UserEntity loginUser, String projectId, DeployPlanEntity deployPlanArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlans(projectId, deployPlanArgs));
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResultEntity getProjectsAdmin(@AuthenticationPrincipal UserEntity loginUser, DeployPlanEntity deployPlanArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlans(deployPlanArgs));
    }

    // 创建部署信息
    @PutMapping(value = "/{deployplanId}/devices/{deviceId}/components/{componentId}")
    public ResultEntity AddDeployPlanDetail(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, @PathVariable("deviceId") String deviceId, @PathVariable("componentId") String componentId, @RequestParam(value = "deployPath") String deployPath) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.AddDeployPlanDetail(deployplanId, deviceId, componentId, deployPath));
    }

    // 修改部署信息

    // 删除部署信息

    // 开始部署
    @GetMapping(value = "/start/{deployplanId}")
    public ResultEntity startDeploy(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId) throws IOException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.startDeploy(deployplanId));
    }
}
