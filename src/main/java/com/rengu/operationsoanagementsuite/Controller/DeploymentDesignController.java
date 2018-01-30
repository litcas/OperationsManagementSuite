package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeploymentDesignService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/deploymentdesigns")
public class DeploymentDesignController {
    @Autowired
    private DeploymentDesignService deploymentDesignService;

    // 修改部署设计
    @PatchMapping(value = "/{deploymentDesignId}")
    public ResultEntity updateDeploymentDesigns(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, DeploymentDesignEntity deploymentDesignArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.updateDeploymentDesigns(deploymentDesignId, deploymentDesignArgs));
    }

    // 删除部署设计
    @DeleteMapping(value = "/{deploymentDesignId}")
    public ResultEntity deleteDeploymentDesigns(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId) {
        deploymentDesignService.deleteDeploymentDesigns(deploymentDesignId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.DEPLOYMENT_DESIGN_DELETED);
    }

    // 查询部署设计
    @GetMapping(value = "/{deploymentDesignId}")
    public ResultEntity getDeploymentDesigns(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesigns(deploymentDesignId));
    }

    // 查询部署设计
    @GetMapping
    public ResultEntity getDeploymentDesigns(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesigns());
    }

    // 保存部署设计详情
    @PostMapping(value = "/{deploymentDesignId}/devices/{deviceId}/components/{componentId}")
    public ResultEntity saveDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId, @PathVariable(value = "componentId") String componentId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, deploymentDesignService.saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId));
    }

    // 保存部署设计详情
    @PostMapping(value = "/{deploymentDesignId}/devices/{deviceId}")
    public ResultEntity saveDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId, @RequestParam(value = "componentIds") String[] componentIds) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, deploymentDesignService.saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentIds));
    }

    // 删除部署设计详情
    @DeleteMapping(value = "/deploymentdesigndetails/{deploymentdesigndetailId}")
    public ResultEntity deleteDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesigndetailId") String deploymentdesigndetailId) {
        deploymentDesignService.deleteDeploymentDesignDetails(deploymentdesigndetailId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.DEPLOYMENT_DESIGN_DETAIL_DELETED);
    }

    // 查询部署设计详情
    @GetMapping(value = "/deploymentdesigndetails")
    public ResultEntity getDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignDetails());
    }

    // 查询部署设计详情
    @GetMapping(value = "/deploymentdesigndetails/{deploymentdesigndetailId}")
    public ResultEntity getDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesigndetailId") String deploymentdesigndetailId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignDetails(deploymentdesigndetailId));
    }

    // 查询部署设计详情
    @GetMapping(value = "/{deploymentDesignId}/deploymentdesigndetails")
    public ResultEntity getDeploymentDesignDetailsByDeploymentDesignId(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId));
    }

    // 查询部署设计详情
    @GetMapping(value = "/{deploymentDesignId}/deploymentdesigndetails/devices/{deviceId}")
    public ResultEntity getDeploymentDesignDetailsByDeploymentDesignId(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignDetails(deploymentDesignId, deviceId));
    }
}