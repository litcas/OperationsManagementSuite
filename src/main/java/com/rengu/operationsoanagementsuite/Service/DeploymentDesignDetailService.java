package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignDetailEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeploymentDesignDetailRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return saveDeploymentDesignDetail(deploymentDesignId, deviceId, componentId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String[] componentIds) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = new ArrayList<>();
        for (String componentId : componentIds) {
            deploymentDesignDetailEntityList.add(saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId));
        }
        return deploymentDesignDetailEntityList;
    }

    @Transactional
    public DeploymentDesignDetailEntity saveDeploymentDesignDetail(String deploymentDesignId, String deviceId, String componentId) {
        DeploymentDesignDetailEntity deploymentDesignDetailEntity = new DeploymentDesignDetailEntity();
        deploymentDesignDetailEntity.setDeploymentDesignEntity(deploymentDesignService.getDeploymentDesigns(deploymentDesignId));
        deploymentDesignDetailEntity.setDeviceEntity(deviceService.getDevices(deviceId));
        deploymentDesignDetailEntity.setComponentEntity(componentService.getComponents(componentId));
        deploymentDesignDetailEntity.setDeployPath((deviceService.getDevices(deviceId).getDeployPath() + componentService.getComponents(componentId).getDeployPath()).replace("//", "/"));
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
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignId(String deploymentDesignId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        return deploymentDesignDetailRepository.findByDeploymentDesignEntityId(deploymentDesignId);
    }

    // 按设备id查询
    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(String deploymentDesignId, String deviceId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        if (!deviceService.hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        return deploymentDesignDetailRepository.findByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId);
    }

    // 按组件id查询
    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignEntityIdAndComponentEntityId(String deploymentDesignId, String componentId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        if (!componentService.hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return deploymentDesignDetailRepository.findByDeploymentDesignEntityIdAndComponentEntityId(deploymentDesignId, componentId);
    }

    // 按设备id和组件id查询
    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(String deploymentDesignId, String deviceId, String componentId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        if (!deviceService.hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        if (!componentService.hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return deploymentDesignDetailRepository.findByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(deploymentDesignId, deviceId, componentId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> copyDeploymentDesignDetail(String deploymentDesignId) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntities = getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId);
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailArgs : deploymentDesignDetailEntities) {
            deploymentDesignDetailEntityList.add(saveDeploymentDesignDetail(deploymentDesignId, deploymentDesignDetailArgs.getDeviceEntity().getId(), deploymentDesignDetailArgs.getComponentEntity().getId()));
        }
        return deploymentDesignDetailEntityList;
    }

    public boolean hasDeploymentDesignDetail(String deploymentdesigndetailId) {
        return deploymentDesignDetailRepository.exists(deploymentdesigndetailId);
    }
}