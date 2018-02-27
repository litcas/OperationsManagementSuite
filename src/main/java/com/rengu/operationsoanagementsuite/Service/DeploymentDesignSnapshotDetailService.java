package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignSnapshotDetailEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeploymentDesignSnapshotDetailService {

    public List<DeploymentDesignSnapshotDetailEntity> saveDeploymentDesignSnapshotDetails(List<DeploymentDesignDetailEntity> deploymentDesignDetailEntities) {
        List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntities) {
            DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity = new DeploymentDesignSnapshotDetailEntity();
            deploymentDesignSnapshotDetailEntity.setIp(deploymentDesignDetailEntity.getDeviceEntity().getIp());
            deploymentDesignSnapshotDetailEntity.setTCPPort(deploymentDesignDetailEntity.getDeviceEntity().getTCPPort());
            deploymentDesignSnapshotDetailEntity.setUDPPort(deploymentDesignDetailEntity.getDeviceEntity().getUDPPort());
            deploymentDesignSnapshotDetailEntity.setComponentEntity(deploymentDesignDetailEntity.getComponentEntity());
            deploymentDesignSnapshotDetailEntity.setDeployPath(deploymentDesignDetailEntity.getDeployPath());
            deploymentDesignSnapshotDetailEntityList.add(deploymentDesignSnapshotDetailEntity);
        }
        return deploymentDesignSnapshotDetailEntityList;
    }
}
