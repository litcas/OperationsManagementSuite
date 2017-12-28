package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanDetailRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeployPlanDetailService {

    @Autowired
    private DeployPlanDetailRepository deployPlanDetailRepository;

    // 创建部署设计信息
    public DeployPlanDetailEntity saveDeployPlanDetails(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity, ComponentEntity componentEntity, String deployPath) {
        DeployPlanDetailEntity deployPlanDetailEntity = new DeployPlanDetailEntity();
        deployPlanDetailEntity.setDeviceEntity(deviceEntity);
        deployPlanDetailEntity.setComponentEntity(componentEntity);
        deployPlanDetailEntity.setDeployPath(deployPath);
        deployPlanDetailEntity.setDeployPlanEntity(deployPlanEntity);
        return deployPlanDetailEntity;
    }

    public List<DeployPlanDetailEntity> getDeployPlanDetails(String deployplanId, String deviceId) {
        return deployPlanDetailRepository.findByDeployPlanEntityIdAndDeviceEntityId(deployplanId, deviceId);
    }

    public DeployPlanDetailEntity getDeployPlanDetails(String deployplanId, String deviceId, String componentId) {
        return deployPlanDetailRepository.findByDeployPlanEntityIdAndDeviceEntityIdAndComponentEntityId(deployplanId, deviceId, componentId);
    }

    public DeployPlanDetailEntity updateDeployPlanDetails(String deployplandetailId, DeployPlanDetailEntity deployPlanDetailArgs) {
        if (!hasDeployplandetail(deployplandetailId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DETAIL_NOT_FOUND);
        }
        DeployPlanDetailEntity deployPlanDetailEntity = deployPlanDetailRepository.findOne(deployplandetailId);
        BeanUtils.copyProperties(deployPlanDetailArgs, deployPlanDetailEntity, "id", "createTime", "deployPlanEntity");
        return deployPlanDetailEntity;
    }

    public void deleteDeployPlanDetails(String deployplandetailId) {
        if (!hasDeployplandetail(deployplandetailId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DETAIL_NOT_FOUND);
        }
        deployPlanDetailRepository.delete(deployplandetailId);
    }

    private boolean hasDeployplandetail(String deployplandetailId) {
        return deployPlanDetailRepository.exists(deployplandetailId);
    }

    public boolean hasDeployplandetail(String deployplanId, String deviceId) {
        return deployPlanDetailRepository.findByDeployPlanEntityIdAndDeviceEntityId(deployplanId, deviceId) != null;
    }
}
