package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.BaselineService;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/baselines")
public class BaselineController {

    @Autowired
    private BaselineService baselineService;

    // 查询基线
    @DeleteMapping(value = "/{baselineId}")
    public ResultEntity deleteBaselines(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("baselineId") String baselineId) {
        baselineService.deleteBaselines(baselineId);
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, "删除成功");
    }

    // 查询基线
    @GetMapping(value = "/{baselineId}")
    public ResultEntity getBaselines(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("baselineId") String baselineId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, baselineService.getBaselines(baselineId));
    }

    // 部署基线
    @PutMapping(value = "/{baselineId}/deploy")
    public ResultEntity deployBaselines(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("baselineId") String baselineId) throws IOException, InterruptedException {
        baselineService.deployBaselines(baselineId);
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, "部署开始");
    }
}
