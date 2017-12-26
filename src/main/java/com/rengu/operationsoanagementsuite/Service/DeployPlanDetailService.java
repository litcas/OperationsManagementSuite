package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeployPlanDetailService {
    @Autowired
    private DeployPlanDetailRepository deployPlanDetailRepository;

    public List<DeployPlanDetailEntity> createDeployPlanDetails(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity, ComponentEntity componentEntity, String deployPath) {
        DeployPlanDetailEntity deployPlanDetailEntity = new DeployPlanDetailEntity();
        deployPlanDetailEntity.setDeviceEntity(deviceEntity);
        deployPlanDetailEntity.setComponentEntity(componentEntity);
        deployPlanDetailEntity.setDeployPath(deployPath);
        deployPlanDetailEntity.setDeployPlanEntity(deployPlanEntity);
        return AddDeployPlanDetail(deployPlanEntity, deployPlanDetailEntity);
    }

    public List<DeployPlanDetailEntity> getDeployPlanDetails(String deployplanId, String deviceId) {
        return deployPlanDetailRepository.findByDeployPlanEntityIdAndDeviceEntityId(deployplanId, deviceId);
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
