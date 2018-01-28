package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Service.DeployLogService;
import com.rengu.operationsoanagementsuite.Service.DeployPlanService;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Service.ProjectService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private DeployPlanService deployPlanService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeployLogService deployLogService;

    // 保存工程
    @PostMapping
    public ResultEntity saveProjects(@AuthenticationPrincipal UserEntity loginUser, ProjectEntity projectEntity) {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, projectService.saveProjects(projectEntity, loginUser));
    }

    // 删除工程
    @DeleteMapping(value = "/{projectId}")
    public ResultEntity deleteProjects(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        projectService.deleteProjects(projectId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, NotificationMessage.projectDeleteMessage(projectId));
    }

    // 修改工程
    @PatchMapping(value = "/{projectId}")
    public ResultEntity updateProjects(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, ProjectEntity projectArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, projectService.updateProjects(projectId, projectArgs));
    }

    // 查看工程
    @GetMapping(value = "/{projectId}")
    public ResultEntity getProject(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, projectService.getProject(projectId));
    }

    // 搜索工程
    @GetMapping
    public ResultEntity getProjects(@AuthenticationPrincipal UserEntity loginUser, ProjectEntity projectArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, projectService.getProjects(loginUser, projectArgs));
    }

    // 搜索工程（管理员）
    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResultEntity getProjectsAdmin(@AuthenticationPrincipal UserEntity loginUser, ProjectEntity projectArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, projectService.getProjects(projectArgs));
    }

    // 新建设备
    @PostMapping(value = "/{projectId}/device")
    public ResultEntity saveDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, DeviceEntity deviceEntity) {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, deviceService.saveDevice(projectId, deviceEntity));
    }

    // 查询设备
    @GetMapping(value = "/{projectId}/device")
    public ResultEntity getDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, DeviceEntity deviceArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.getDevices(projectId, deviceArgs));
    }

    // 新建部署设计
    @PostMapping(value = "/{projectId}/deployplan")
    public ResultEntity saveDeployPlans(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, DeployPlanEntity deployPlanEntity) {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.saveDeployPlans(projectId, deployPlanEntity));
    }

    // 查看部署设计
    @GetMapping("/{projectId}/deployplan")
    public ResultEntity getDeployPlans(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId, DeployPlanEntity deployPlanArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployPlanService.getDeployPlans(projectId, deployPlanArgs));
    }

    // 查看部署日志
    @GetMapping("/{projectId}/deploylog")
    public ResultEntity getDeploylogs(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "projectId") String projectId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployLogService.getDeployLogs(projectId));
    }
}