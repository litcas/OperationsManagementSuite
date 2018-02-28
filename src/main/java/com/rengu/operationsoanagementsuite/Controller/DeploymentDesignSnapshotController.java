package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeploymentDesignSnapshotService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/deploymentdesignsnapshots")
public class DeploymentDesignSnapshotController {

    @Autowired
    private DeploymentDesignSnapshotService deploymentDesignSnapshotService;

    // 删除部署设计快照
    @DeleteMapping(value = "/{deploymentdesignsnapshotId}")
    public ResultEntity deleteDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesignsnapshotId") String deploymentdesignsnapshotId) {
        deploymentDesignSnapshotService.deleteDeploymentDesignSnapshots(deploymentdesignsnapshotId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_DELETED);
    }

    // 查询部署设计快照
    @GetMapping(value = "/{deploymentdesignsnapshotId}")
    public ResultEntity getDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesignsnapshotId") String deploymentdesignsnapshotId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignSnapshotService.getDeploymentDesignSnapshots(deploymentdesignsnapshotId));
    }

    // 查询部署设计快照
    @GetMapping()
    public ResultEntity getDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignSnapshotService.getDeploymentDesignSnapshots());
    }

    // 部署快照
    @PutMapping(value = "/{deploymentdesignsnapshotId}/deploy")
    public ResultEntity deployDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesignsnapshotId") String deploymentdesignsnapshotId) throws IOException, ExecutionException, InterruptedException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignSnapshotService.deployDeploymentDesignSnapshots(deploymentdesignsnapshotId));
    }
}
