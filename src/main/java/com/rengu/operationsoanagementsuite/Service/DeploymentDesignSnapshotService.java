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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeploymentDesignSnapshotService {

    @Autowired
    private AsyncTask asyncTask;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DeploymentDesignSnapshotRepository deploymentDesignSnapshotRepository;
    @Autowired
    private DeploymentDesignSnapshotDetailService deploymentDesignSnapshotDetailService;
    @Autowired
    private DeploymentDesignService deploymentDesignService;
    @Autowired
    private DeploymentDesignDetailService deploymentDesignDetailService;
    @Autowired
    private ProjectService projectService;

    @Transactional
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

    @Transactional
    public void deleteDeploymentDesignSnapshots(String deploymentdesignsnapshotId) {
        if (!hasDeploymentDesignSnapshots(deploymentdesignsnapshotId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_NOT_FOUND);
        }
        deploymentDesignSnapshotRepository.delete(deploymentdesignsnapshotId);
    }

    @Transactional
    public DeploymentDesignSnapshotEntity getDeploymentDesignSnapshots(String deploymentDesignSnapshotId) {
        if (!hasDeploymentDesignSnapshots(deploymentDesignSnapshotId)) {
            throw new ClassCastException(NotificationMessage.DEPLOYMENT_DESIGN_SNAPSHOT_NOT_FOUND);
        }
        return progressChecker(deploymentDesignSnapshotRepository.findOne(deploymentDesignSnapshotId));
    }

    @Transactional
    public List<DeploymentDesignSnapshotEntity> getDeploymentDesignSnapshots() {
        return progressChecker(deploymentDesignSnapshotRepository.findAll());
    }

    @Transactional
    public List<DeploymentDesignSnapshotEntity> getDeploymentDesignSnapshotsByProjectId(String projectId) {
        return progressChecker(deploymentDesignSnapshotRepository.findByProjectEntityId(projectService.getProjects(projectId).getId()));
    }

    @Transactional
    public void deployDeploymentDesignSnapshots(String deploymentdesignsnapshotId) throws IOException {
        List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntities = getDeploymentDesignSnapshots(deploymentdesignsnapshotId).getDeploymentDesignSnapshots();
        Map<String, List<DeploymentDesignSnapshotDetailEntity>> ipMap = deploymentDesignSnapshotDetailEntities.stream().collect(Collectors.groupingBy(DeploymentDesignSnapshotDetailEntity::getIp));
        for (Map.Entry<String, List<DeploymentDesignSnapshotDetailEntity>> entry : ipMap.entrySet()) {
            asyncTask.deploySnapshot(deploymentdesignsnapshotId, entry.getKey(), ApplicationConfiguration.deviceTCPPort, entry.getValue());
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

    public DeploymentDesignSnapshotEntity progressChecker(DeploymentDesignSnapshotEntity deploymentDesignSnapshotEntity) {
        if (stringRedisTemplate.hasKey(deploymentDesignSnapshotEntity.getId())) {
            deploymentDesignSnapshotEntity.setProgress(Double.parseDouble(stringRedisTemplate.opsForValue().get(deploymentDesignSnapshotEntity.getId())));
        }
        return deploymentDesignSnapshotEntity;
    }

    public List<DeploymentDesignSnapshotEntity> progressChecker(List<DeploymentDesignSnapshotEntity> deploymentDesignSnapshotEntityListe) {
        for (DeploymentDesignSnapshotEntity deploymentDesignSnapshotEntity : deploymentDesignSnapshotEntityListe) {
            if (stringRedisTemplate.hasKey(deploymentDesignSnapshotEntity.getId())) {
                deploymentDesignSnapshotEntity.setProgress(Double.parseDouble(stringRedisTemplate.opsForValue().get(deploymentDesignSnapshotEntity.getId())));
            }
        }
        return deploymentDesignSnapshotEntityListe;
    }

    public boolean hasDeploymentDesignSnapshots(String deploymentDesignSnapshotId) {
        return deploymentDesignSnapshotRepository.exists(deploymentDesignSnapshotId);
    }

    public boolean hasProjectIdAndName(String projectId, String name) {
        return deploymentDesignSnapshotRepository.findByProjectEntityIdAndName(projectId, name).size() != 0;
    }
}
