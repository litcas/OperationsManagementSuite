package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/componentpackages")
public class ComponentPackageController {

    @Autowired
    private ComponentPackageService componentPackageService;

    // 保存组件基线
    @PostMapping
    public ResultEntity saveComponentPackages(@AuthenticationPrincipal UserEntity loginUser, ComponentPackageEntity componentPackageArgs, @RequestParam(value = "componentIds") String... componentIds) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, componentPackageService.saveComponentPackages(componentPackageArgs, componentIds));
    }

    // 删除组件基线
    @DeleteMapping(value = "/{componentPackageId}")
    public ResultEntity deleteComponentPackages(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentPackageId") String componentPackageId) {
        componentPackageService.deleteComponentPackages(componentPackageId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.COMPONENT_PACKAGE_DELETED);
    }

    // 修改组件基线
    @PatchMapping(value = "/{componentPackageId}")
    public ResultEntity updateComponentPackages(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentPackageId") String componentPackageId, ComponentPackageEntity componentPackageArgs, @RequestParam(value = "componentIds") String... componentIds) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentPackageService.updateComponentPackages(componentPackageId, componentPackageArgs, componentIds));
    }

    // 查询组件基线
    @GetMapping(value = "/{componentPackageId}")
    public ResultEntity getComponentPackages(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentPackageId") String componentPackageId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentPackageService.getComponentPackages(componentPackageId));
    }

    // 查询组件基线
    @GetMapping()
    public ResultEntity getComponentPackages(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentPackageService.getComponentPackages());
    }
}
