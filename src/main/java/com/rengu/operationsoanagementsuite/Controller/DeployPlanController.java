package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
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
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlans(deployplanId));
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResultEntity getProjectsAdmin(@AuthenticationPrincipal UserEntity loginUser, DeployPlanEntity deployPlanArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlans(deployPlanArgs));
    }

    // 创建部署信息
    @PutMapping(value = "/{deployplanId}/devices/{deviceId}/components/{componentId}")
    public ResultEntity AddDeployPlanDetail(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, @PathVariable("deviceId") String deviceId, @PathVariable("componentId") String componentId, @RequestParam(value = "deployPath") String deployPath) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.addDeployPlanDetail(deployplanId, deviceId, componentId, deployPath));
    }

    // 删除部署信息
    @DeleteMapping(value = "/deployplandetails/{deployplandetailId}")
    public ResultEntity deleteDeployPlanDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplandetailId") String deployplandetailId) {
        deployPlanService.deleteDeployPlanDetails(deployplandetailId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, NotificationMessage.deployplandetailDeleteMessage(deployplandetailId));
    }

    // 修改部署信息
    @PatchMapping(value = "/deployplandetails/{deployplandetailId}")
    public ResultEntity updateDeployPlanDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplandetailId") String deployplandetailId, DeployPlanDetailEntity deployPlanDetailArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.updateDeployPlanDetails(deployplandetailId, deployPlanDetailArgs));
    }

    // 查询部署信息
    @GetMapping(value = "/{deployplanId}/devices/{deviceId}")
    public ResultEntity getDeployPlanDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, @PathVariable("deviceId") String deviceId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlanDetails(deployplanId, deviceId));
    }


    // 开始部署
    @GetMapping(value = "/deploy/{deployplanId}/devices/{deviceId}")
    public ResultEntity startDeploy(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, @PathVariable("deviceId") String deviceId) throws IOException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.startDeploy(deployplanId, deviceId));
    }

    // 扫描设备
    @GetMapping(value = "/scan/{deployplanId}/devices/{deviceId}")
    public ResultEntity scanDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, @PathVariable("deviceId") String deviceId) throws IOException, InterruptedException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.scanDevices(deployplanId, deviceId));
    }

    // 扫描设备
    @GetMapping(value = "/scan/{deployplanId}/devices/{deviceId}/components/{componentId}")
    public ResultEntity scanDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deployplanId") String deployplanId, @PathVariable("deviceId") String deviceId, @PathVariable("componentId") String componentId) throws IOException, InterruptedException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.scanDevices(deployplanId, deviceId, componentId));
    }
}
