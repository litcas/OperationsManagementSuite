package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Utils.HeartbeatEntity;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ProjectService projectService;
    public static List<HeartbeatEntity> onlineHeartbeats = new ArrayList<>();

    @Transactional
    public DeviceEntity saveDevices(String projectId, DeviceEntity deviceArgs) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        if (hasIp(projectId, deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        deviceArgs.setDeployPath(getDeployPath(deviceArgs));
        return deviceRepository.save(deviceArgs);
    }

    @Transactional
    public void deleteDevices(String deviceId) {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        deviceRepository.delete(deviceId);
    }

    @Transactional
    public DeviceEntity updateDevices(String deviceId, DeviceEntity deviceArgs) {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        if (hasIp(deviceArgs.getProjectEntity().getId(), deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime");
        return deviceRepository.save(deviceEntity);
    }

    @Transactional
    public DeviceEntity getDevices(String deviceId) {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        return onlineChecker(deviceRepository.findOne(deviceId), onlineHeartbeats);
    }

    @Transactional
    public List<DeviceEntity> getDevicesByProjectId(String projectId) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        return onlineChecker(deviceRepository.findByProjectEntityId(projectId), onlineHeartbeats);
    }

    @Transactional
    public List<DeviceEntity> getDevices() {
        return onlineChecker(deviceRepository.findAll(), onlineHeartbeats);
    }

    // 调整路径分隔符
    public String getDeployPath(DeviceEntity deviceEntity) {
        if (StringUtils.isEmpty(deviceEntity.getDeployPath())) {
            throw new CustomizeException(NotificationMessage.DEVICE_DEPLOY_PATH_NOT_FOUND);
        }
        // 替换斜线方向
        String deployPath = deviceEntity.getDeployPath().replace("\\", "/");
        return deployPath.endsWith("/") ? deployPath : deployPath + "/";
    }

    public DeviceEntity onlineChecker(DeviceEntity deviceEntity, List<HeartbeatEntity> onlineDevices) {
        if (onlineDevices != null && onlineDevices.size() != 0) {
            for (HeartbeatEntity heartbeatEntity : onlineDevices) {
                if (deviceEntity.getIp().equals(heartbeatEntity.getInetAddress().getHostAddress())) {
                    deviceEntity.setOnline(true);
                    break;
                }
            }
        }
        return deviceEntity;
    }

    public List<DeviceEntity> onlineChecker(List<DeviceEntity> deviceEntityList, List<HeartbeatEntity> onlineDevices) {
        if (onlineDevices != null && onlineDevices.size() != 0) {
            for (DeviceEntity deviceEntity : deviceEntityList) {
                for (HeartbeatEntity heartbeatEntity : onlineDevices) {
                    if (deviceEntity.getIp().equals(heartbeatEntity.getInetAddress().getHostAddress())) {
                        deviceEntity.setOnline(true);
                        break;
                    }
                }

            }
        }
        return deviceEntityList;
    }

    public boolean hasDevices(String deviceId) {
        return deviceRepository.exists(deviceId);
    }

    public boolean hasIp(String projectId, String ip) {
        return deviceRepository.findByProjectEntityIdAndIp(projectId, ip) != null;
    }
}