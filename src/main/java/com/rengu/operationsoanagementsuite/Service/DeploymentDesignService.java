package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeploymentDesignRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DeploymentDesignService {
    @Autowired
    private DeploymentDesignRepository deploymentDesignRepository;
    @Autowired
    private DeploymentDesignDetailService deploymentDesignDetailService;
    @Autowired
    private ProjectService projectService;

    // 保存部署设计
    @Transactional
    public DeploymentDesignEntity saveDeploymentDesigns(String projectId, DeploymentDesignEntity deploymentDesignArgs) {
        if (StringUtils.isEmpty(deploymentDesignArgs.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NAME_NOT_FOUND);
        }
        if (hasName(projectId, deploymentDesignArgs.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_EXISTS);
        }
        deploymentDesignArgs.setProjectEntity(projectService.getProjects(projectId));
        return deploymentDesignRepository.save(deploymentDesignArgs);
    }

    // 修改部署设计
    @Transactional
    public DeploymentDesignEntity updateDeploymentDesigns(String deploymentDesignId, DeploymentDesignEntity deploymentDesignArgs) {
        if (!hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        DeploymentDesignEntity deploymentDesignEntity = deploymentDesignRepository.findOne(deploymentDesignId);
        BeanUtils.copyProperties(deploymentDesignArgs, deploymentDesignEntity, "id", "createTime", "name", "projectEntity", "deploymentDesignDetailEntities");
        return deploymentDesignRepository.save(deploymentDesignEntity);
    }

    // 删除部署设计
    @Transactional
    public void deleteDeploymentDesigns(String deploymentDesignId) {
        if (hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        deploymentDesignRepository.delete(deploymentDesignId);
    }

    @Transactional
    public DeploymentDesignEntity getDeploymentDesigns(String deploymentDesignId) {
        if (!hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        return deploymentDesignRepository.findOne(deploymentDesignId);
    }

    @Transactional
    public List<DeploymentDesignEntity> getDeploymentDesignsByProjectId(String projectId) {
        return deploymentDesignRepository.findByProjectEntityId(projectId);
    }

    @Transactional
    public List<DeploymentDesignEntity> getDeploymentDesigns() {
        return deploymentDesignRepository.findAll();
    }

    @Transactional
    public DeploymentDesignDetailEntity saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String componentId) {
        return deploymentDesignDetailService.saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String[] componentIds) {
        return deploymentDesignDetailService.saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentIds);
    }

    @Transactional
    public void deleteDeploymentDesignDetails(String deploymentdesigndetailId) {
        deploymentDesignDetailService.deleteDeploymentDesignDetails(deploymentdesigndetailId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetails() {
        return deploymentDesignDetailService.getDeploymentDesignDetails();
    }

    @Transactional
    public DeploymentDesignDetailEntity getDeploymentDesignDetails(String deploymentdesigndetailId) {
        return deploymentDesignDetailService.getDeploymentDesignDetails(deploymentdesigndetailId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetails(String deploymentDesignId, String deviceId) {
        return deploymentDesignDetailService.getDeploymentDesignDetails(deploymentDesignId, deviceId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignId(String deploymentDesignId) {
        return deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId);
    }

    public boolean hasName(String projectId, String name) {
        return deploymentDesignRepository.findByProjectEntityIdAndName(projectId, name) != null;
    }

    public boolean hasDeploymentDesigns(String deploymentDesignId) {
        return deploymentDesignRepository.exists(deploymentDesignId);
    }
}