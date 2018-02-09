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

@RestController
@RequestMapping(value = "/deploymentdesignsnapshots")
public class DeploymentDesignSnapshotController {

    @Autowired
    private DeploymentDesignSnapshotService deploymentDesignSnapshotService;

    @DeleteMapping(value = "/{deploymentdesignsnapshotId}")
    public ResultEntity deleteDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesignsnapshotId") String deploymentdesignsnapshotId) {
        deploymentDesignSnapshotService.deleteDeploymentDesignSnapshots(deploymentdesignsnapshotId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_DELETED);
    }

    @GetMapping(value = "/{deploymentdesignsnapshotId}")
    public ResultEntity getDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deploymentdesignsnapshotId") String deploymentdesignsnapshotId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignSnapshotService.getDeploymentDesignSnapshots(deploymentdesignsnapshotId));
    }

    @GetMapping()
    public ResultEntity getDeploymentDesignSnapshots(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deploymentDesignSnapshotService.getDeploymentDesignSnapshots());
    }
}
