package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeploymentDesignRepository;
import com.rengu.operationsoanagementsuite.Task.AsyncTask;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DeploymentDesignService {

    @Autowired
    private AsyncTask asyncTask;
    @Autowired
    private DeploymentDesignRepository deploymentDesignRepository;
    @Autowired
    private DeploymentDesignDetailService deploymentDesignDetailService;
    @Autowired
    private DeploymentDesignSnapshotService deploymentDesignSnapshotService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private DeviceService deviceService;

    // 保存部署设计
    @Transactional
    public DeploymentDesignEntity saveDeploymentDesigns(String projectId, DeploymentDesignEntity deploymentDesignArgs) {
        if (StringUtils.isEmpty(deploymentDesignArgs.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NAME_NOT_FOUND);
        }
        if (hasProjectIdAndName(projectId, deploymentDesignArgs.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_EXISTS);
        }
        deploymentDesignArgs.setProjectEntity(projectService.getProjects(projectId));
        return deploymentDesignRepository.save(deploymentDesignArgs);
    }

    // 修改部署设计
    @Transactional
    public DeploymentDesignEntity updateDeploymentDesigns(String deploymentDesignId, DeploymentDesignEntity deploymentDesignArgs) {
        DeploymentDesignEntity deploymentDesignEntity = getDeploymentDesigns(deploymentDesignId);
        if (hasProjectIdAndName(deploymentDesignEntity.getProjectEntity().getId(), deploymentDesignArgs.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_EXISTS);
        }
        BeanUtils.copyProperties(deploymentDesignArgs, deploymentDesignEntity, "id", "createTime", "projectEntity", "deploymentDesignDetailEntities");
        return deploymentDesignRepository.save(deploymentDesignEntity);
    }

    // 删除部署设计
    @Transactional
    public void deleteDeploymentDesigns(String deploymentDesignId) {
        if (!hasDeploymentDesigns(deploymentDesignId)) {
            throw new CustomizeException(NotificationMessage.DEPLOYMENT_DESIGN_NOT_FOUND);
        }
        deploymentDesignDetailService.deleteDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId);
        deploymentDesignRepository.delete(deploymentDesignId);
    }

    @Transactional
    public void deleteDeploymentDesignDetailsByDeviceId(String deviceId) {
        deploymentDesignDetailService.deleteDeploymentDesignDetailsByDeviceId(deviceId);
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
    public DeploymentDesignSnapshotEntity saveDeploymentDesignSnapshots(String deploymentDesignId, DeploymentDesignSnapshotEntity deploymentDesignSnapshotArgs) {
        return deploymentDesignSnapshotService.saveDeploymentDesignSnapshots(deploymentDesignId, deploymentDesignSnapshotArgs);
    }

    @Transactional
    public DeploymentDesignDetailEntity saveDeploymentDesignDetails(String deploymentDesignId, String deviceId, String componentId) {
        return deploymentDesignDetailService.saveDeploymentDesignDetails(deploymentDesignId, deviceId, componentId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> saveDeploymentDesignDetails(String deploymentDesignId, String[] deviceIds, String[] componentIds) {
        return deploymentDesignDetailService.saveDeploymentDesignDetails(deploymentDesignId, deviceIds, componentIds);
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
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(String deploymentDesignId, String deviceId) {
        return deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignEntityIdAndComponentEntityId(String deploymentDesignId, String componentId) {
        return deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndComponentEntityId(deploymentDesignId, componentId);
    }

    @Transactional
    public List<DeploymentDesignDetailEntity> getDeploymentDesignDetailsByDeploymentDesignId(String deploymentDesignId) {
        return deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId);
    }

    @Transactional
    public List<DeviceEntity> getDevicesByDeploymentDesignId(String deploymentDesignId) {
        List<DeviceEntity> deviceEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId)) {
            if (!deviceEntityList.contains(deploymentDesignDetailEntity.getDeviceEntity())) {
                deviceEntityList.add(deviceService.onlineChecker(deviceService.progressChecker(deploymentDesignDetailEntity.getDeviceEntity())));
            }
        }
        return deviceEntityList;
    }

    @Transactional
    public List<ScanResultEntity> scanDevices(String deploymentDesignId, String deviceId, String... extensions) throws IOException, InterruptedException, ExecutionException {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId);
        List<ScanResultEntity> scanResultEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            scanResultEntityList.add(asyncTask.scan(UUID.randomUUID().toString(), deploymentDesignDetailEntity, extensions).get());
        }
        return scanResultEntityList;
    }

    @Transactional
    public List<ScanResultEntity> scanComponents(String deploymentDesignId, String deviceId, String componentId, String... extensions) throws IOException, InterruptedException, ExecutionException {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(deploymentDesignId, deviceId, componentId);
        List<ScanResultEntity> scanResultEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            scanResultEntityList.add(asyncTask.scan(UUID.randomUUID().toString(), deploymentDesignDetailEntity, extensions).get());
        }
        return scanResultEntityList;
    }


    public List<DeployFileEntity> deploy(String deploymentDesignId, String deviceId, String componentId) throws IOException, ExecutionException, InterruptedException {
        List<DeployFileEntity> errorFileList = new ArrayList<>();
        errorFileList.addAll(asyncTask.deployDesign(deviceId, deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(deploymentDesignId, deviceId, componentId)).get());
        return errorFileList;
    }

    public List<DeployFileEntity> deploy(String deploymentDesignId, String deviceId) throws IOException, ExecutionException, InterruptedException {
        List<DeployFileEntity> errorFileList = new ArrayList<>();
        errorFileList.addAll(asyncTask.deployDesign(deviceId, deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId)).get());
        return errorFileList;
    }

    public List<DeployFileEntity> deploy(String deploymentDesignId) throws IOException, ExecutionException, InterruptedException {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = getDeploymentDesignDetailsByDeploymentDesignId(deploymentDesignId);
        Map<DeviceEntity, List<DeploymentDesignDetailEntity>> deviceMap = deploymentDesignDetailEntityList.stream().collect(Collectors.groupingBy(DeploymentDesignDetailEntity::getDeviceEntity));
        List<DeployFileEntity> errorFileList = new ArrayList<>();
        for (Map.Entry<DeviceEntity, List<DeploymentDesignDetailEntity>> entry : deviceMap.entrySet()) {
            errorFileList.addAll(asyncTask.deployDesign(entry.getKey().getId(), entry.getValue()).get());
        }
        return errorFileList;
    }


    public DeploymentDesignEntity copyDeploymentDesign(String deploymentDesignId) {
        DeploymentDesignEntity deploymentDesignArgs = getDeploymentDesigns(deploymentDesignId);
        DeploymentDesignEntity deploymentDesignEntity = new DeploymentDesignEntity();
        BeanUtils.copyProperties(deploymentDesignArgs, deploymentDesignEntity, "id", "createTime", "name");
        // 设置部署图名称-自动累加数字
        int i = 1;
        String name = deploymentDesignArgs.getName() + "-副本( " + i + ")";
        while (hasProjectIdAndName(deploymentDesignArgs.getProjectEntity().getId(), name)) {
            i = i + 1;
            name = deploymentDesignArgs.getName() + "-副本( " + i + ")";
        }
        deploymentDesignEntity.setName(name);
        deploymentDesignDetailService.copyDeploymentDesignDetail(deploymentDesignArgs.getId());
        return deploymentDesignRepository.save(deploymentDesignEntity);
    }

    public boolean hasProjectIdAndName(String projectId, String name) {
        return deploymentDesignRepository.findByProjectEntityIdAndName(projectId, name) != null;
    }

    public boolean hasDeploymentDesigns(String deploymentDesignId) {
        return deploymentDesignRepository.exists(deploymentDesignId);
    }
}