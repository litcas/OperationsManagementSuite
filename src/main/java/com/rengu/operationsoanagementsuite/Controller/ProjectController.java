package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ProjectService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 保存工程z
    @PostMapping
    public ResultEntity saveProjects(@AuthenticationPrincipal UserEntity loginUser, ProjectEntity projectEntity) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, projectService.saveProjects(projectEntity, loginUser));
    }

    // 删除工程
    @DeleteMapping(value = "/{projectId}")
    public ResultEntity deleteProjects(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("projectId") String projectId) {
        projectService.deleteProjects(projectId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, "Id为" + projectId + "的工程已删除。");
    }
}
