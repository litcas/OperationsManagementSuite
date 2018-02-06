package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ComponentBaselineEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ComponentBaselineService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/componentbaselines")
public class ComponentBaselineController {

    @Autowired
    private ComponentBaselineService componentBaselineService;

    // 保存组件基线
    @PostMapping
    public ResultEntity saveComponentBaselines(@AuthenticationPrincipal UserEntity loginUser, ComponentBaselineEntity componentBaselineArgs, @RequestParam(value = "componentIds") String... componentIds) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, componentBaselineService.saveComponentBaselines(componentBaselineArgs, componentIds));
    }

    // 删除组件基线
    @DeleteMapping(value = "/{componentbaselineId}")
    public ResultEntity deleteComponentBaselines(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentbaselineId") String componentBaselineId) {
        componentBaselineService.deleteComponentBaselines(componentBaselineId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.COMPONENT_BASELINE_DELETED);
    }

    // 修改组件基线
    @PatchMapping(value = "/{componentbaselineId}")
    public ResultEntity updateComponentBaselines(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentbaselineId") String componentBaselineId, @RequestParam(value = "componentIds") String... componentIds) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentBaselineService.updateComponentBaselines(componentBaselineId, componentIds));
    }

    // 查询组件基线
    @GetMapping(value = "/{componentbaselineId}")
    public ResultEntity getComponentBaselines(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentbaselineId") String componentBaselineId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentBaselineService.getComponentBaselines(componentBaselineId));
    }

    // 查询组件基线
    @GetMapping()
    public ResultEntity getComponentBaselines(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentBaselineService.getComponentBaselines());
    }
}
