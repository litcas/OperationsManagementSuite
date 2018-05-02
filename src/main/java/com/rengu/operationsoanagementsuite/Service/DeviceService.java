package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Task.AsyncTask;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class DeviceService {

    public static ArrayList<HeartbeatEntity> onlineHeartbeats = new ArrayList<>();
    private final DeviceRepository deviceRepository;
    private final ProjectService projectService;
    private final DeploymentDesignService deploymentDesignService;
    private final AsyncTask asyncTask;
    private final EventService eventService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, ProjectService projectService, DeploymentDesignService deploymentDesignService, AsyncTask asyncTask, EventService eventService) {
        this.deviceRepository = deviceRepository;
        this.projectService = projectService;
        this.deploymentDesignService = deploymentDesignService;
        this.asyncTask = asyncTask;
        this.eventService = eventService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DeviceEntity saveDevices(UserEntity loginUser, String projectId, DeviceEntity deviceArgs) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        if (hasIp(projectId, deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        DeviceEntity deviceEntity = new DeviceEntity();
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime", "projectEntity");
        deviceEntity.setDeployPath(getDeployPath(deviceArgs));
        deviceEntity.setProjectEntity(projectService.getProjects(projectId));
        eventService.saveDeviceEvent(loginUser, deviceEntity);
        return deviceRepository.save(deviceEntity);
    }


    public void deleteDevices(UserEntity loginUser, String deviceId) {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        DeviceEntity deviceEntity = getDevices(deviceId);
        // 从部署设计中移除设备
        deploymentDesignService.deleteDeploymentDesignDetailsByDeviceId(deviceId);
        deviceRepository.delete(deviceId);
        // 从部署状态中移除设备部署信息
        AsyncTask.deployStatusEntities.removeIf(deployStatusEntity -> deviceEntity.getIp().equals(deployStatusEntity.getIp()));
        eventService.deleteDeviceEvent(loginUser, deviceEntity);
    }


    public DeviceEntity updateDevices(UserEntity loginUser, String deviceId, DeviceEntity deviceArgs) {
        if (StringUtils.isEmpty(deviceArgs.getIp())) {
            throw new CustomizeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        DeviceEntity deviceEntity = getDevices(deviceId);
        if (!deviceArgs.getIp().equals(deviceEntity.getIp())) {
            if (hasIp(deviceEntity.getProjectEntity().getId(), deviceArgs.getIp())) {
                throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
            }
        }
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime", "projectEntity");
        eventService.updateDeviceEvent(loginUser, deviceEntity);
        return deviceRepository.save(deviceEntity);
    }


    public DeviceEntity getDevices(String deviceId) {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        return onlineChecker(deployProgressChecker(deviceRepository.findOne(deviceId)));
    }


    public List<DeviceEntity> getDevicesByProjectId(String projectId) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        return onlineChecker(deployProgressChecker(deviceRepository.findByProjectEntityId(projectId)));
    }


    public List<DeviceEntity> getDevices() {
        return onlineChecker(deployProgressChecker(deviceRepository.findAll()));
    }

    public DeviceTaskEntity getDeviceTasks(String deviceId) throws IOException, InterruptedException, ExecutionException {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        String id = UUID.randomUUID().toString();
        return asyncTask.getDeviceTasks(id, getDevices(deviceId)).get();
    }

    public DeviceDiskEntity getDeviceDisks(String deviceId) throws IOException, InterruptedException, ExecutionException {
        if (!hasDevices(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        String id = UUID.randomUUID().toString();
        return asyncTask.getDeviceDisks(id, getDevices(deviceId)).get();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DeviceEntity copyDevices(String deviceId) {
        DeviceEntity deviceArgs = getDevices(deviceId);
        DeviceEntity deviceEntity = new DeviceEntity();
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id", "createTime", "name", "ip");
        // 设置复制设备的名字
        deviceEntity.setName(deviceArgs.getName() + "-副本");
        // 设置复制组件的IP
        String ip = deviceArgs.getIp();
        while (hasIp(deviceArgs.getProjectEntity().getId(), ip)) {
            String[] strings = ip.split("\\.");
            int temp = Integer.parseInt(strings[3]) + 1;
            ip = strings[0] + "." + strings[1] + "." + strings[2] + "." + temp;
        }
        deviceEntity.setIp(ip);
        deviceEntity.setProjectEntity(deviceArgs.getProjectEntity());
        return deviceRepository.save(deviceEntity);
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

    public DeviceEntity deployProgressChecker(DeviceEntity deviceEntity) {
        for (DeployStatusEntity deployStatusEntity : AsyncTask.deployStatusEntities) {
            if (deployStatusEntity.getIp().equals(deviceEntity.getIp())) {
                deviceEntity.setProgress(deployStatusEntity.getProgress());
                deviceEntity.setTransferRate(deployStatusEntity.getTransferRate());
                deviceEntity.setErrorFileList(deployStatusEntity.getErrorFileList());
                deviceEntity.setCompletedFileList(deployStatusEntity.getCompletedFileList());
                break;
            }
        }
        return deviceEntity;
    }

    public List<DeviceEntity> deployProgressChecker(List<DeviceEntity> deviceEntityList) {
        for (DeviceEntity deviceEntity : deviceEntityList) {
            deployProgressChecker(deviceEntity);
        }
        return deviceEntityList;
    }

    public static boolean isOnline(String ip) {
        List<HeartbeatEntity> onlineDevices = new ArrayList<>(onlineHeartbeats);
        if (onlineDevices.size() != 0) {
            for (HeartbeatEntity heartbeatEntity : onlineDevices) {
                if (ip.equals(heartbeatEntity.getInetAddress().getHostAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    public DeviceEntity onlineChecker(DeviceEntity deviceEntity) {
        List<HeartbeatEntity> onlineDevices = new ArrayList<>(onlineHeartbeats);
        if (onlineDevices.size() != 0) {
            for (HeartbeatEntity heartbeatEntity : onlineDevices) {
                if (deviceEntity.getIp().equals(heartbeatEntity.getInetAddress().getHostAddress())) {
                    deviceEntity.setOnline(true);
                    deviceEntity.setCPUInfo(heartbeatEntity.getCPUInfo());
                    deviceEntity.setCPUClock(heartbeatEntity.getCPUClock());
                    deviceEntity.setCPUUtilization(heartbeatEntity.getCPUUtilization());
                    deviceEntity.setRAMSize(heartbeatEntity.getRAMSize());
                    deviceEntity.setFreeRAMSize(heartbeatEntity.getFreeRAMSize());
                    break;
                }
            }
        }
        return deviceEntity;
    }

    public List<DeviceEntity> onlineChecker(List<DeviceEntity> deviceEntityList) {
        List<HeartbeatEntity> onlineDevices = new ArrayList<>(onlineHeartbeats);
        Iterator<HeartbeatEntity> heartbeatEntityIterator = onlineDevices.iterator();
        if (onlineDevices.size() != 0) {
            while (heartbeatEntityIterator.hasNext()) {
                HeartbeatEntity heartbeatEntity = heartbeatEntityIterator.next();
                for (DeviceEntity deviceEntity : deviceEntityList) {
                    if (deviceEntity.getIp().equals(heartbeatEntity.getInetAddress().getHostAddress())) {
                        deviceEntity.setOnline(true);
                        deviceEntity.setCPUInfo(heartbeatEntity.getCPUInfo());
                        deviceEntity.setCPUClock(heartbeatEntity.getCPUClock());
                        deviceEntity.setCPUUtilization(heartbeatEntity.getCPUUtilization());
                        deviceEntity.setRAMSize(heartbeatEntity.getRAMSize());
                        deviceEntity.setFreeRAMSize(heartbeatEntity.getFreeRAMSize());
                        heartbeatEntityIterator.remove();
                        break;
                    }
                }
            }
            // 建立虚拟设备
            for (HeartbeatEntity heartbeatEntity : onlineDevices) {
                DeviceEntity deviceEntity = new DeviceEntity();
                deviceEntity.setName(heartbeatEntity.getInetAddress().getHostName());
                deviceEntity.setIp(heartbeatEntity.getInetAddress().getHostAddress());
                deviceEntity.setVirtual(true);
                deviceEntity.setOnline(true);
                deviceEntity.setCPUInfo(heartbeatEntity.getCPUInfo());
                deviceEntity.setCPUClock(heartbeatEntity.getCPUClock());
                deviceEntity.setCPUUtilization(heartbeatEntity.getCPUUtilization());
                deviceEntity.setRAMSize(heartbeatEntity.getRAMSize());
                deviceEntity.setFreeRAMSize(heartbeatEntity.getFreeRAMSize());
                deviceEntityList.add(deviceEntity);
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