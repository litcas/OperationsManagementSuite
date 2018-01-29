package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceRealInfoEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Task.HearBeatTask;
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
    private ProjectService projectService;

    // 新增设备
    @Transactional
    public DeviceEntity saveDevice(String projectId, DeviceEntity deviceEntity) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        // 检查设备ip参数是否存在
        if (StringUtils.isEmpty(deviceEntity.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        // 检查Ip是否已经存在
        if (hasDeviceByIp(projectId, deviceEntity.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_IP_EXISTS);
        }
        deviceEntity.setProjectEntity(projectService.getProject(projectId));
        deviceEntity.setLastModified(new Date());
        return deviceRepository.save(deviceEntity);
    }

    // 删除设备
    @Transactional
    public void deleteDevice(String deviceId) {
        // 检查设备id是否存在
        if (!hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        deviceRepository.delete(deviceId);
    }

    // 更新设备
    @Transactional
    public DeviceEntity updateDevice(String deviceId, DeviceEntity deviceArgs) {
        // 检查设备id是否存在
        if (!hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        if (!hasDeviceByIp(deviceArgs.getIp(), deviceEntity.getProjectEntity().getId())) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime", "projectEntity");
        deviceEntity.setLastModified(new Date());
        return deviceRepository.save(deviceEntity);
    }

    // 查询设备
    public DeviceEntity getDevice(String deviceId) {
        return deviceRepository.findOne(deviceId);
    }

    // 查询设备
    public List<DeviceEntity> getDevices(String projectId, DeviceEntity deviceArgs) {
        return onlineChecker(deviceRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(projectId)) {
                predicateList.add(cb.equal(root.get("projectEntity").get("id"), projectId));
            }
            if (deviceArgs.getName() != null) {
                predicateList.add(cb.like(root.get("name"), deviceArgs.getName()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }));
    }

    @Transactional
    public DeviceEntity copyDevice(String deviceId) {
        if (!hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        DeviceEntity deviceArgs = deviceRepository.findOne(deviceId);
        DeviceEntity deviceEntity = new DeviceEntity();
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime", "lastModified", "name");
        deviceEntity.setName(deviceArgs.getName() + "-副本");
        if (!StringUtils.isEmpty(deviceEntity.getIp())) {
            String ip = deviceEntity.getIp();
            while (true) {
                if (hasDeviceByIp(ip, deviceEntity.getProjectEntity().getId())) {
                    String[] strings = ip.split("\\.");
                    int temp = Integer.parseInt(strings[3]) + 1;
                    ip = strings[0] + "." + strings[1] + "." + strings[2] + "." + temp;
                } else {
                    deviceEntity.setIp(ip);
                    break;
                }
            }
        }
        return deviceRepository.save(deviceEntity);
    }

    public List<DeviceEntity> onlineChecker(List<DeviceEntity> deviceEntities) {
        for (DeviceEntity deviceEntity : deviceEntities) {
            for (DeviceRealInfoEntity deviceRealInfoEntity : HearBeatTask.onlineDevices) {
                if (deviceEntity.getIp().equals(deviceRealInfoEntity.getInetAddress().getHostAddress())) {
                    deviceEntity.setOnline(true);
                    break;
                }
            }
        }
        return deviceEntities;
    }

    public boolean hasDevice(String deviceId) {
        return deviceRepository.exists(deviceId);
    }

    private boolean hasDeviceByIp(String deviceIp, String projectId) {
        return deviceRepository.findByIpAndProjectEntityId(deviceIp, projectId) != null;
    }
}