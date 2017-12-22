package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanRepository;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DeviceService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DeployPlanRepository deployPlanRepository;
    @Autowired
    private ProjectRepository projectRepository;

    // 新增设备
    @Transactional
    public DeviceEntity saveDevice(String projectId, DeviceEntity deviceEntity) {
        if (StringUtils.isEmpty(projectId)) {
            logger.info(NotificationMessage.PROJECT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_ID_NOT_FOUND);
        }
        if (!projectRepository.exists(projectId)) {
            logger.info(NotificationMessage.PROJECT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        // 检查设备名称参数是否存在
        if (StringUtils.isEmpty(deviceEntity.getName())) {
            logger.info(NotificationMessage.DEVICE_NAME_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_NAME_NOT_FOUND);
        }
        // 检查设备ip参数是否存在
        if (StringUtils.isEmpty(deviceEntity.getIp())) {
            logger.info(NotificationMessage.DEVICE_IP_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        // 检查Ip是否已经存在
        if (deviceRepository.findByIp(deviceEntity.getIp()) != null) {
            logger.info(NotificationMessage.DEVICE_IP_EXISTS);
            throw new CustomizeException(NotificationMessage.DEVICE_IP_EXISTS);
        }
        deviceEntity.setLastModified(new Date());
        deviceEntity.setProjectEntity(projectRepository.findOne(projectId));
        return deviceRepository.save(deviceEntity);
    }

    // 删除设备
    @Transactional
    public void deleteDevice(String deviceId) {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info(NotificationMessage.DEVICE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_ID_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceRepository.exists(deviceId)) {
            logger.info(NotificationMessage.DEVICE_EXISTS);
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        deviceRepository.delete(deviceId);
    }

    // 更新设备
    @Transactional
    public DeviceEntity updateDevice(String deviceId, DeviceEntity deviceArgs) {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info(NotificationMessage.DEVICE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_ID_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceRepository.exists(deviceId)) {
            logger.info(NotificationMessage.DEVICE_EXISTS);
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime", "");
        deviceEntity.setLastModified(new Date());
        return deviceRepository.save(deviceEntity);
    }

    // 查询设备
    public DeviceEntity getDevice(String deviceId) {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info(NotificationMessage.DEVICE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_ID_NOT_FOUND);
        }
        return deviceRepository.findOne(deviceId);
    }

    // 查询设备
    public List<DeviceEntity> getDevices(DeviceEntity deviceArgs) {
        return deviceRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (deviceArgs.getName() != null) {
                predicateList.add(cb.like(root.get("name"), deviceArgs.getName()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    public List<DeployPlanDetailEntity> scanDevices(String deviceId, String deployplanId) {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info(NotificationMessage.DEVICE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_ID_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceRepository.exists(deviceId)) {
            logger.info(NotificationMessage.DEVICE_EXISTS);
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info(NotificationMessage.DEVICE_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = new ArrayList<>();
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanEntity.getDeployPlanDetailEntities()) {
            if (deployPlanDetailEntity.getDeviceEntity().getId().equals(deviceId)) {
                deployPlanDetailEntityList.add(deployPlanDetailEntity);
                logger.info(deployPlanDetailEntity.getComponentEntity().getId());
            }
        }
        return deployPlanDetailEntityList;
    }
}