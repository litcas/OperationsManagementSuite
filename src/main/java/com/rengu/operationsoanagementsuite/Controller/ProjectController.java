package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Service.DeploymentDesignService;
import com.rengu.operationsoanagementsuite.Service.DeploymentDesignSnapshotService;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Service.ProjectService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private DeploymentDesignService deploymentDesignService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeploymentDesignSnapshotService deploymentDesignSnapshotService;

    // 保存工程
    @PostMapping
    public ResultEntity saveProjects(@AuthenticationPrincipal UserEntity loginUser, ProjectEntity projectArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, projectService.saveProjects(projectArgs, loginUser));
    }

    // 删除工程
    @DeleteMapping(value = "/{projectId}")
    public ResultEntity deleteProjects(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        projectService.deleteProjects(projectId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.PROJECT_DELETE);
    }

    // 修改工程
    @PatchMapping(value = "/{projectId}")
    public ResultEntity updateProjects(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, ProjectEntity projectArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, projectService.updateProjects(projectId, projectArgs));
    }

    // 查询工程
    @GetMapping
    public ResultEntity getProjects(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, projectService.getProjects(loginUser));
    }

    // 查询工程
    @GetMapping(value = "/{projectId}")
    public ResultEntity getProjects(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, projectService.getProjects(projectId));
    }

    // 保存部署设计
    @PostMapping(value = "/{projectId}/deploymentdesigns")
    public ResultEntity saveDeploymentDesigns(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, DeploymentDesignEntity deploymentDesignArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, deploymentDesignService.saveDeploymentDesigns(projectId, deploymentDesignArgs));
    }

    // 查询部署设计
    @GetMapping(value = "/{projectId}/deploymentdesigns")
    public ResultEntity getDeploymentDesigns(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignService.getDeploymentDesignsByProjectId(projectId));
    }

    // 查询部署设计快照
    @GetMapping(value = "/{projectId}/deploymentdesignsnapshots")
    public ResultEntity getDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignSnapshotService.getDeploymentDesignSnapshotsByProjectId(projectId));
    }

    // 保存设备
    @PostMapping(value = "/{projectId}/devices")
    public ResultEntity saveDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, DeviceEntity deviceArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, deviceService.saveDevices(projectId, deviceArgs));
    }

    // 查询设备
    @GetMapping(value = "/{projectId}/devices")
    public ResultEntity saveDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deviceService.getDevicesByProjectId(projectId));
    }
}
