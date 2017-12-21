package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeployPlanDetailService {
    public List<DeployPlanDetailEntity> createDeployPlanDetails(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity, ComponentEntity componentEntity, String deployPath) {
        DeployPlanDetailEntity deployPlanDetailEntity = new DeployPlanDetailEntity();
        deployPlanDetailEntity.setDeviceEntity(deviceEntity);
        deployPlanDetailEntity.setComponentEntity(componentEntity);
        deployPlanDetailEntity.setDeployPath(deployPath);
        return AddDeployPlanDetail(deployPlanEntity, deployPlanDetailEntity);
    }

    private List<DeployPlanDetailEntity> AddDeployPlanDetail(DeployPlanEntity deployPlanEntity, DeployPlanDetailEntity deployPlanDetailEntity) {
        List<DeployPlanDetailEntity> deployPlanDetailEntities = deployPlanEntity.getDeployPlanDetailEntities();
        if (deployPlanDetailEntities == null) {
            deployPlanDetailEntities = new ArrayList<>();
        }
        if (!deployPlanDetailEntities.contains(deployPlanDetailEntity)) {
            deployPlanDetailEntities.add(deployPlanDetailEntity);
        }
        return deployPlanDetailEntities;
    }
}
