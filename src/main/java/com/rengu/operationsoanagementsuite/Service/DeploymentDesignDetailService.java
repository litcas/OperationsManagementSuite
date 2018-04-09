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
@Transactional
public class DeploymentDesignDetailService {

    @Autowired
    private DeploymentDesignDetailRepository deploymentDesignDetailRepository;
    @Autowired
    private DeploymentDesignService deploymentDesignService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ComponentService componentService;


    public DeploymentDesignDetailEntity saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String componentId) {
        return saveDeploymentDesignDetail(deploymentDesignId, deviceId, componentId);
    }


    public List<DeploymentDesignDetailEntity> saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String[] componentIds) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = new ArrayList<>();
        for (String componentId : componentIds) {
            deploymentDesignDetailEntityList.add(saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId));
        }
        return deploymentDesignDetailEntityList;
    }


    public List<DeploymentDesignDetailEntity> saveDeploymentDesignDetails(String deploymentDesignId, String[] deviceIds, String[] componentIds) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = new ArrayList<>();
        for (int i = 0; i < deviceIds.length; i++) {
            deploymentDesignDetailEntityList.add(saveDeploymentDesignDetail(deploymentDesignId, deviceIds[i], componentIds[i]));
        }
        return deploymentDesignDetailEntityList;
    }


    public DeploymentDesignDetailEntity saveDeploymentDesignDetail(String deploymentDesignId, String deviceId, String componentId) {
        if (hasDeploymentDesignDetail(deploymentDesignId, deviceId, componentId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_DETAIL_EXISTS);
        }
        DeploymentDesignDetailEntity deploymentDesignDetailEntity = new DeploymentDesignDetailEntity();
        deploymentDesignDetailEntity.setDeploymentDesignEntity(deploymentDesignService.getDeploymentDesigns(deploymentDesignId));
        deploymentDesignDetailEntity.setDeviceEntity(deviceService.getDevices(deviceId));
        deploymentDesignDetailEntity.setComponentEntity(componentService.getComponents(componentId));
        return deploymentDesignDetailRepository.save(deploymentDesignDetailEntity);
    }


    public void deleteDeploymentDesignDetails(String deploymentdesigndetailId) {
        if (!hasDeploymentDesignDetail(deploymentdesigndetailId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_DETAIL_NOT_FOUND);
        }
        deploymentDesignDetailRepository.delete(deploymentdesigndetailId);
    }


    public void deleteDeploymentDesignDetailsByDeploymentDesignId(String deploymentDesignId) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId);
        deploymentDesignDetailRepository.delete(deploymentDesignDetailEntityList);
    }


    public void deleteDeploymentDesignDetailsByDeviceId(String deviceId) {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = deploymentDesignDetailRepository.findByDeviceEntityId(deviceId);
        deploymentDesignDetailRepository.delete(deploymentDesignDetailEntityList);
    }


    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetails() {
        return deploymentDesignDetailRepository.findAll();
    }


    public DeploymentDesignDetailEntity getDeploymentDesignDetails(String deploymentDesignDetailId) {
        return deploymentDesignDetailRepository.findOne(deploymentDesignDetailId);
    }


    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignId(String deploymentDesignId) {
        if (!deploymentDesignService.hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = deploymentDesignDetailRepository.findByDeploymentDesignEntityId(deploymentDesignId);
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            deploymentDesignDetailEntity.setDeviceEntity(deviceService.onlineChecker(deviceService.deployProgressChecker(deploymentDesignDetailEntity.getDeviceEntity())));
        }
        return deploymentDesignDetailEntityList;
    }

    // 按设备id查询
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

    public boolean hasDeploymentDesignDetail(String deploymentdesigndetailId, String deviceId, String componentId) {
        return getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(deploymentdesigndetailId, deviceId, componentId).size() != 0;
    }
}