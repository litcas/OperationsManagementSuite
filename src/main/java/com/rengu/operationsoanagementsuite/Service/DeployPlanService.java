package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.UDPUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
public class DeployPlanService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeployPlanRepository deployPlanRepository;
    @Autowired
    private DeployPlanDetailService deployPlanDetailService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private ProjectService projectService;

    // 保存部署设计
    @Transactional
    public DeployPlanEntity saveDeployPlans(String projectId, DeployPlanEntity deployPlanEntity) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (deployPlanEntity == null) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deployPlanEntity.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NAME_NOT_FOUND);
        }
        if (hasDeployPlans(deployPlanEntity.getName(), projectId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_EXISTS);
        }
        deployPlanEntity.setProjectEntity(projectService.getProject(projectId));
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    // 删除部署设计
    @Transactional
    public void deleteDeployPlans(String deployplanId) {
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        deployPlanRepository.delete(deployplanId);
    }

    // 更新部署设计
    @Transactional
    public DeployPlanEntity updateDeployPlans(String deployplanId, DeployPlanEntity deployPlanArgs) {
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        BeanUtils.copyProperties(deployPlanArgs, deployPlanEntity, "id", "createTime", "lastModified", "deployPlanDetailEntity", "projectEntity");
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    // 查看部署设计
    @Transactional
    public DeployPlanEntity getDeployPlans(String deployplanId) {
        return deployPlanRepository.findOne(deployplanId);
    }

    @Transactional
    public List<DeployPlanEntity> getDeployPlans(String projectId, DeployPlanEntity deployPlanArgs) {
        return deployPlanRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(projectId)) {
                predicateList.add(cb.equal(root.get("projectEntity").get("id"), projectId));
            }
            if (deployPlanArgs != null) {
                if (!StringUtils.isEmpty(deployPlanArgs.getName())) {
                    predicateList.add(cb.like(root.get("name"), deployPlanArgs.getName()));
                }
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Transactional
    public List<DeployPlanEntity> getDeployPlans(DeployPlanEntity deployPlanArgs) {
        return getDeployPlans(null, deployPlanArgs);
    }

    @Transactional
    public DeployPlanDetailEntity addDeployPlanDetail(String deployplanId, String deviceId, String componentId, String deployPath) {
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        if (!componentService.hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deployPath)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DEPLOY_PATH_NOT_FOUND);
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        DeviceEntity deviceEntity = deviceService.getDevice(deviceId);
        ComponentEntity componentEntity = componentService.getComponent(componentId);
        DeployPlanDetailEntity deployPlanDetailEntity = deployPlanDetailService.saveDeployPlanDetails(deployPlanEntity, deviceEntity, componentEntity, deployPath);
        deployPlanEntity.setDeployPlanDetailEntities(addDeployPlanDetails(deployPlanEntity, deployPlanDetailEntity));
        deployPlanRepository.save(deployPlanEntity);
        return deployPlanDetailEntity;
    }

    @Transactional
    public DeployPlanDetailEntity updateDeployPlanDetails(String deployplandetailId, DeployPlanDetailEntity deployPlanDetailArgs) {
        return deployPlanDetailService.updateDeployPlanDetails(deployplandetailId, deployPlanDetailArgs);
    }

    @Transactional
    public void deleteDeployPlanDetails(String deployplandetailId) {
        deployPlanDetailService.deleteDeployPlanDetails(deployplandetailId);
    }

    // 开始部署
    @Transactional
    public Future<Boolean> startDeploy(String deployplanId, String deviceId) throws IOException {
        if (!deployPlanRepository.exists(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        DeviceEntity deviceEntity = deviceService.getDevice(deviceId);
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId);
        return startDeploy(deviceEntity, deployPlanDetailEntityList);
    }

    // 异步发送文件
    @Async
    Future<Boolean> startDeploy(DeviceEntity deviceEntity, List<DeployPlanDetailEntity> deployPlanDetailEntities) throws IOException {
        Socket socket = new Socket(deviceEntity.getIp(), deviceEntity.getPort());
        if (socket.isConnected()) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // 连接成功
            for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanDetailEntities) {
                ComponentEntity componentEntity = deployPlanDetailEntity.getComponentEntity();
                for (ComponentFileEntity componentFileEntity : componentEntity.getComponentFileEntities()) {
                    // 发送文件逻辑
                    // 1、发送文件路径 + 文件名
                    String deployPath = deployPlanDetailEntity.getDeployPath() + componentFileEntity.getPath();
                    dataOutputStream.writeUTF(deployPath);
                    dataOutputStream.flush();
                    // 2、发送文件大小
                    long size = componentFileEntity.getSize();
                    dataOutputStream.writeLong(size);
                    dataOutputStream.flush();
                    // 3、发送文件实体
                    IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentFileEntity.getPath()), dataOutputStream);
                    dataOutputStream.flush();
                    // 发送文件结束标志
                    socket.shutdownOutput();
                }
            }
            dataOutputStream.close();
            socket.close();
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    public void scanDevices(String deployplanId, String deviceId) throws IOException {
        // 检查部署设计id参数是否存在
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        if (!deployPlanDetailService.hasDeployplandetail(deployplanId, deviceId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DETAIL_NOT_FOUND);
        }
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId);
        for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanDetailEntityList) {
            scanDevices(UUID.randomUUID().toString(), deployPlanDetailEntity);
        }
    }

    public void scanDevices(String deployplanId, String deviceId, String componentId) throws IOException {
        // 检查部署设计id参数是否存在
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        if (!componentService.hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        DeployPlanDetailEntity deployPlanDetailEntity = deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId, componentId);
        scanDevices(UUID.randomUUID().toString(), deployPlanDetailEntity);
    }

    @Async
    Future<Boolean> scanDevices(String id, DeployPlanDetailEntity deployPlanDetailEntity) throws IOException {
        DeviceEntity deviceEntity = deployPlanDetailEntity.getDeviceEntity();
        String message = UDPUtils.getScanDeviceMessage(id, deployPlanDetailEntity);
        return new AsyncResult<>(UDPUtils.sandMessage(deviceEntity.getIp(), deviceEntity.getPort(), message));
    }

    // 添加部署设计信息
    private List<DeployPlanDetailEntity> addDeployPlanDetails(DeployPlanEntity deployPlanEntity, DeployPlanDetailEntity... deployPlanDetailEntities) {
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = deployPlanEntity.getDeployPlanDetailEntities();
        if (deployPlanDetailEntityList == null) {
            deployPlanDetailEntityList = new ArrayList<>();
        }
        for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanDetailEntities) {
            if (!deployPlanDetailEntityList.contains(deployPlanDetailEntity)) {
                deployPlanDetailEntityList.add(deployPlanDetailEntity);
            }
        }
        return deployPlanDetailEntityList;
    }

    private boolean hasDeployPlans(String deployplanId) {
        return deployPlanRepository.exists(deployplanId);
    }

    private boolean hasDeployPlans(String name, String projectId) {
        return deployPlanRepository.findByNameAndProjectEntityId(name, projectId) != null;
    }
}
