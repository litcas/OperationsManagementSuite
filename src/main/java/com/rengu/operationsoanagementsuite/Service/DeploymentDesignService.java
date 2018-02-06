package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeploymentDesignRepository;
import com.rengu.operationsoanagementsuite.Task.AsyncTask;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeploymentDesignService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UDPService udpService;
    @Autowired
    private AsyncTask asyncTask;
    @Autowired
    private DeploymentDesignRepository deploymentDesignRepository;
    @Autowired
    private DeploymentDesignDetailService deploymentDesignDetailService;
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
    public List<ScanResultEntity> scanDevices(String deploymentDesignId, String deviceId, String... extensions) throws IOException, InterruptedException {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId);
        List<ScanResultEntity> scanResultEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            scanResultEntityList.add(scan(UUID.randomUUID().toString(), deploymentDesignDetailEntity, extensions));
        }
        return scanResultEntityList;
    }

    @Transactional
    public List<ScanResultEntity> scanComponents(String deploymentDesignId, String deviceId, String componentId, String... extensions) throws IOException, InterruptedException {
        List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList = deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(deploymentDesignId, deviceId, componentId);
        List<ScanResultEntity> scanResultEntityList = new ArrayList<>();
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            scanResultEntityList.add(scan(UUID.randomUUID().toString(), deploymentDesignDetailEntity, extensions));
        }
        return scanResultEntityList;
    }

    // 扫描设备
    public ScanResultEntity scan(String id, DeploymentDesignDetailEntity deploymentDesignDetailEntity, String... extensions) throws IOException, InterruptedException {
        if (extensions == null) {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deploymentDesignDetailEntity.getDeployPath());
        } else {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deploymentDesignDetailEntity.getDeployPath(), extensions);
        }
        int count = 0;
        while (true) {
            if (stringRedisTemplate.hasKey(id)) {
                ScanResultEntity scanResultEntity = JsonUtils.readJsonString(stringRedisTemplate.opsForValue().get(id), ScanResultEntity.class);
                ComponentEntity componentEntity = deploymentDesignDetailEntity.getComponentEntity();
                List<ComponentDetailEntity> correctComponentFiles = new ArrayList<>();
                List<ComponentDetailEntity> modifyedComponentFiles = new ArrayList<>();
                List<ComponentDetailEntity> unknownFiles = new ArrayList<>();
                for (ComponentDetailEntity scanResult : scanResultEntity.getOriginalScanResultList()) {
                    boolean fileExists = false;
                    for (ComponentDetailEntity componentFile : componentEntity.getComponentDetailEntities()) {
                        // 路径是否一致
                        if (scanResult.getPath().replace(deploymentDesignDetailEntity.getDeployPath(), "").equals(componentFile.getPath())) {
                            fileExists = true;
                            // MD5是否相同
                            if (scanResult.getMD5().equals(componentFile.getMD5())) {
                                correctComponentFiles.add(componentFile);
                                scanResultEntity.setHasCorrectComponentFiles(true);
                            } else {
                                modifyedComponentFiles.add(componentFile);
                                scanResultEntity.setHasModifyedComponentFiles(true);
                            }
                            break;
                        }
                    }
                    // 未知文件
                    if (!fileExists) {
                        scanResultEntity.setHasUnknownFiles(true);
                        ComponentDetailEntity componentFile = new ComponentDetailEntity();
                        componentFile.setMD5(scanResult.getMD5());
                        componentFile.setPath(scanResult.getPath());
                        unknownFiles.add(componentFile);
                    }
                }
                scanResultEntity.setCorrectComponentFiles(correctComponentFiles);
                scanResultEntity.setModifyedComponentFiles(modifyedComponentFiles);
                scanResultEntity.setUnknownFiles(unknownFiles);
                scanResultEntity.setHasMissingFile(scanResultEntity.getOriginalScanResultList().size() - unknownFiles.size() != componentEntity.getComponentDetailEntities().size());
                return scanResultEntity;
            } else {
                Thread.sleep(10000);
                count = count + 1;
                if (count == 10) {
                    throw new CustomizeException("扫描'" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + "'上的'" + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "'组件失败");
                }
            }
        }
    }

    public void deployComponents(String deploymentDesignId, String deviceId, String componentId) throws IOException {
        asyncTask.deploy(deviceService.getDevices(deviceId), deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityIdAndComponentEntityId(deploymentDesignId, deviceId, componentId));
    }

    public void deployDevices(String deploymentDesignId, String deviceId) throws IOException {
        asyncTask.deploy(deviceService.getDevices(deviceId), deploymentDesignDetailService.getDeploymentDesignDetailsByDeploymentDesignEntityIdAndDeviceEntityId(deploymentDesignId, deviceId));
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