package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Repository.DeployLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeployLogService {

    @Autowired
    private DeployLogRepository deployLogRepository;

    @Transactional
    public DeployLogEntity saveDeployLogs(DeploymentDesignEntity deploymentDesignEntity, DeviceEntity deviceEntity, List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList) {
        DeployLogEntity deployLogEntity = new DeployLogEntity();
        deployLogEntity.setIp(deviceEntity.getIp());
        deployLogEntity.setPath(deviceEntity.getDeployPath());
        deployLogEntity.setDeploymentDesignName(deploymentDesignEntity.getName());
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            deployLogEntity.setComponentEntities(addComponents(deployLogEntity, deploymentDesignDetailEntity.getComponentEntity()));
        }
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public List<DeployLogEntity> getDeployLogs() {
        return deployLogRepository.findAll();
    }

    public List<ComponentEntity> addComponents(DeployLogEntity DeployLogEntity, ComponentEntity componentEntity) {
        List<ComponentEntity> componentEntityList = DeployLogEntity.getComponentEntities();
        if (componentEntityList == null) {
            componentEntityList = new ArrayList<>();
        }
        if (!componentEntityList.contains(componentEntity)) {
            componentEntityList.add(componentEntity);
        }
        return componentEntityList;
    }
}
