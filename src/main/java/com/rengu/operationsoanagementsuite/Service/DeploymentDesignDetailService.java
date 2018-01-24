package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignDetailEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeploymentDesignDetailRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeploymentDesignDetailService {

    @Autowired
    private DeploymentDesignDetailRepository deploymentDesignDetailRepository;
    @Autowired
    private DeploymentDesignService deploymentDesignService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ComponentService componentService;

    @Transactional
    public DeploymentDesignDetailEntity saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String componentId) {
        String deployPath = deviceService.getDevices(deviceId).getDeployPath() + componentService.getComponents(componentId).getDeployPath();
        return saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId, deployPath);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String[] componentIds) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = new ArrayList<>();
        for (String componentId : componentIds) {
            String deployPath = deviceService.getDevices(deviceId).getDeployPath() + componentService.getComponents(componentId).getDeployPath();
            deploymentDesignDetailEntityList.add(saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId, deployPath));
        }
        return deploymentDesignDetailEntityList;
    }

    @Transactional
    public DeploymentDesignDetailEntity saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String componentId, String deployPath) {
        DeploymentDesignDetailEntity deploymentDesignDetailEntity = new DeploymentDesignDetailEntity();
        deploymentDesignDetailEntity.setDeploymentDesignEntity(deploymentDesignService.getDeploymentDesigns(deploymentDesignId));
        deploymentDesignDetailEntity.setDeviceEntity(deviceService.getDevices(deviceId));
        deploymentDesignDetailEntity.setComponentEntity(componentService.getComponents(componentId));
        deploymentDesignDetailEntity.setDeployPath(deployPath);
        return deploymentDesignDetailRepository.save(deploymentDesignDetailEntity);
    }

    @Transactional
    public void deleteDeploymentDesignDetails(String deploymentdesigndetailId) {
        if (!hasDeploymentDesignDetail(deploymentdesigndetailId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_DETAIL_NOT_FOUND);
        }
        deploymentDesignDetailRepository.delete(deploymentdesigndetailId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetails() {
        return deploymentDesignDetailRepository.findAll();
    }

    @Transactional
    public DeploymentDesignDetailEntity getDeploymentDesignDetails(String deploymentDesignDetailId) {
        return deploymentDesignDetailRepository.findOne(deploymentDesignDetailId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetails(String deploymentDesignId, String deviceId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        if (!deviceService.hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        return deploymentDesignDetailRepository.findByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignId(String deploymentDesignId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        return deploymentDesignDetailRepository.findByDeploymentDesignEntityId(deploymentDesignId);
    }

    public boolean hasDeploymentDesignDetail(String deploymentdesigndetailId) {
        return deploymentDesignDetailRepository.exists(deploymentdesigndetailId);
    }
}