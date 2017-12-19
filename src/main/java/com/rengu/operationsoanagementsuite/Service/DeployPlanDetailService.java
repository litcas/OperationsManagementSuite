package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import org.springframework.stereotype.Service;

@Service
public class DeployPlanDetailService {
    public DeployPlanDetailEntity saveDeployPlanDetails(DeviceEntity deviceEntity, ComponentEntity componentEntity, String deployPath) {
        DeployPlanDetailEntity deployPlanDetailEntity = new DeployPlanDetailEntity();
        deployPlanDetailEntity.setDeviceEntity(deviceEntity);
        deployPlanDetailEntity.setComponentEntity(componentEntity);
        deployPlanDetailEntity.setDeployPath(deployPath);
        return deployPlanDetailEntity;
    }
}
