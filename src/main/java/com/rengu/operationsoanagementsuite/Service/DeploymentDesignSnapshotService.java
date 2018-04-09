package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignSnapshotDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignSnapshotEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeploymentDesignSnapshotRepository;
import com.rengu.operationsoanagementsuite.Task.AsyncTask;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeploymentDesignSnapshotService {

    @Autowired
    private AsyncTask asyncTask;
    @Autowired
    private DeploymentDesignSnapshotRepository deploymentDesignSnapshotRepository;
    @Autowired
    private DeploymentDesignSnapshotDetailService deploymentDesignSnapshotDetailService;
    @Autowired
    private DeploymentDesignService deploymentDesignService;
    @Autowired
    private DeploymentDesignDetailService deploymentDesignDetailService;

    public DeploymentDesignSnapshotEntity saveDeploymentDesignSnapshots(String deploymentDesignId, DeploymentDesignSnapshotEntity deploymentDesignSnapshotArgs) {
        DeploymentDesignEntity deploymentDesignEntity = deploymentDesignService.getDeploymentDesigns(deploymentDesignId);
        if (hasProjectIdAndName(deploymentDesignEntity.getProjectEntity().getId(), deploymentDesignSnapshotArgs.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_EXISTS);
        }
        DeploymentDesignSnapshotEntity deploymentDesignSnapshotEntity = new DeploymentDesignSnapshotEntity();
        BeanUtils.copyProperties(deploymentDesignSnapshotArgs, deploymentDesignSnapshotEntity, "id", "createTime", "projectEntity", "deploymentDesignSnapshots");
        deploymentDesignSnapshotEntity.setProjectEntity(deploymentDesignEntity.getProjectEntity());
        deploymentDesignSnapshotEntity.setDeploymentDesignSnapshots(addDeploymentDesignSnapshotDetails(deploymentDesignSnapshotEntity, deploymentDesignSnapshotDetailService.saveDeploymentDesignSnapshotDetails(deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId))));
        return deploymentDesignSnapshotRepository.save(deploymentDesignSnapshotEntity);
    }


    public void deleteDeploymentDesignSnapshot(String deploymentdesignsnapshotId) {
        if (!hasDeploymentDesignSnapshots(deploymentdesignsnapshotId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_NOT_FOUND);
        }
        deploymentDesignSnapshotRepository.delete(deploymentdesignsnapshotId);
    }


    public DeploymentDesignSnapshotEntity getDeploymentDesignSnapshots(String deploymentDesignSnapshotId) {
        if (!hasDeploymentDesignSnapshots(deploymentDesignSnapshotId)) {
            throw new ClassCastException(NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_NOT_FOUND);
        }
        return deploymentDesignSnapshotRepository.findOne(deploymentDesignSnapshotId);
    }


    public List<DeploymentDesignSnapshotEntity> getDeploymentDesignSnapshots() {
        return deploymentDesignSnapshotRepository.findAll();
    }


    public List<DeploymentDesignSnapshotEntity> getDeploymentDesignSnapshotsByProjectId(String projectId) {
        return deploymentDesignSnapshotRepository.findByProjectEntityId(projectId);
    }


    public void deployDeploymentDesignSnapshots(String deploymentdesignsnapshotId) {
        List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntities = getDeploymentDesignSnapshots(deploymentdesignsnapshotId).getDeploymentDesignSnapshots();
        Map<String, List<DeploymentDesignSnapshotDetailEntity>> ipMap = deploymentDesignSnapshotDetailEntities.stream().collect(Collectors.groupingBy(DeploymentDesignSnapshotDetailEntity::getIp));
        for (Map.Entry<String, List<DeploymentDesignSnapshotDetailEntity>> entry : ipMap.entrySet()) {
            if (DeviceService.isOnline(entry.getKey())) {
                asyncTask.deploySnapshot(entry.getKey(), ApplicationConfiguration.deviceTCPPort, entry.getValue());
            }
        }
    }

    public List<DeploymentDesignSnapshotDetailEntity> addDeploymentDesignSnapshotDetails(DeploymentDesignSnapshotEntity deploymentDesignSnapshotEntity, List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntityList) {
        List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntities = deploymentDesignSnapshotEntity.getDeploymentDesignSnapshots();
        if (deploymentDesignSnapshotDetailEntities == null) {
            deploymentDesignSnapshotDetailEntities = new ArrayList<>();
        }
        for (DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity : deploymentDesignSnapshotDetailEntityList) {
            if (!deploymentDesignSnapshotDetailEntities.contains(deploymentDesignSnapshotDetailEntity)) {
                deploymentDesignSnapshotDetailEntities.add(deploymentDesignSnapshotDetailEntity);
            }
        }
        return deploymentDesignSnapshotDetailEntities;
    }

    public boolean hasDeploymentDesignSnapshots(String deploymentDesignSnapshotId) {
        return deploymentDesignSnapshotRepository.exists(deploymentDesignSnapshotId);
    }

    public boolean hasProjectIdAndName(String projectId, String name) {
        return deploymentDesignSnapshotRepository.findByProjectEntityIdAndName(projectId, name).size() != 0;
    }
}
