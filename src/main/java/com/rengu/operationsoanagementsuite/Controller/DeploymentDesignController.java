package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignSnapshotEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeploymentDesignService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

    // 复制部署图
    @PostMapping(value = "/{deploymentDesignId}/copy")
    public ResultEntity copyDeploymentDesign(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.copyDeploymentDesign(deploymentDesignId));
    }

    // 生成部署设计快照
    @PostMapping(value = "/{deploymentDesignId}/deploymentdesignsnapshots")
    public ResultEntity saveDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, DeploymentDesignSnapshotEntity deploymentDesignSnapshotArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.saveDeploymentDesignSnapshots(deploymentDesignId, deploymentDesignSnapshotArgs));
    }

    // 保存部署设计详情
    @PostMapping(value = "/{deploymentDesignId}/deploymentdesigndetails")
    public ResultEntity saveDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @RequestParam(value = "deviceIds") String[] deviceIds, @RequestParam(value = "componentIds") String[] componentIds) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, deploymentDesignService.saveDeploymentDesignDetails(deploymentDesignId, deviceIds, componentIds));
    }


    // 保存部署设计详情
    @PostMapping(value = "/{deploymentDesignId}/deploymentdesigndetails/devices/{deviceId}/components/{componentId}")
    public ResultEntity saveDeploymentDesignDetails(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId, @PathVariable(value = "componentId") String componentId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, deploymentDesignService.saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId));
    }

    // 保存部署设计详情
    @PostMapping(value = "/{deploymentDesignId}/deploymentdesigndetails/devices/{deviceId}")
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

    // 查询部署设计设备
    @GetMapping(value = "/{deploymentDesignId}/devices")
    public ResultEntity getDevicesByDeploymentDesignId(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDevicesByDeploymentDesignId(deploymentDesignId));
    }

    // 查询部署设计详情-设备
    @GetMapping(value = "/{deploymentDesignId}/deploymentdesigndetails/devices/{deviceId}")
    public ResultEntity getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId));
    }

    // 查询部署设计详情-组件
    @GetMapping(value = "/{deploymentDesignId}/deploymentdesigndetails/components/{componentId}")
    public ResultEntity getDeploymentDesignDetailsByDeploymentDesignEntityIdAndComponentEntityId(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "componentId") String componentId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndComponentEntityId(deploymentDesignId, componentId));
    }

    // 扫描设备
    @GetMapping(value = "/{deploymentDesignId}/devices/{deviceId}/scan")
    public ResultEntity scanDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId, @RequestParam(value = "extensions", required = false) String... extensions) throws IOException, InterruptedException, ExecutionException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.scanDevices(deploymentDesignId, deviceId, extensions));
    }

    // 扫描设备
    @GetMapping(value = "/{deploymentDesignId}/devices/{deviceId}/components/{componentId}/scan")
    public ResultEntity scanComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId, @PathVariable(value = "componentId") String componentId, @RequestParam(value = "extensions", required = false) String... extensions) throws IOException, InterruptedException, ExecutionException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.scanComponents(deploymentDesignId, deviceId, componentId, extensions));
    }

    // 部署组件
    @PutMapping(value = "/{deploymentDesignId}/devices/{deviceId}/components/{componentId}/deploy")
    public ResultEntity deploy(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId, @PathVariable(value = "componentId") String componentId) throws IOException {
        deploymentDesignService.deploy(deploymentDesignId, deviceId, componentId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, NotificationMessage.DEPLOY_STARTED);
    }

    // 部署组件
    @PutMapping(value = "/{deploymentDesignId}/devices/{deviceId}/deploy")
    public ResultEntity deploy(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId, @PathVariable(value = "deviceId") String deviceId) throws IOException {
        deploymentDesignService.deploy(deploymentDesignId, deviceId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, NotificationMessage.DEPLOY_STARTED);
    }

    // 部署部署图
    @PutMapping(value = "/{deploymentDesignId}/deploy")
    public ResultEntity deploy(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentDesignId") String deploymentDesignId) throws IOException {
        deploymentDesignService.deploy(deploymentDesignId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, NotificationMessage.DEPLOY_STARTED);
    }
}