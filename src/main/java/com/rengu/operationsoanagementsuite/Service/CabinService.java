package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.CabinDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.CabinEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.CabinRepository;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Service
public class CabinService {

    private final CabinRepository cabinRepository;
    private final ProjectRepository projectRepository;
    private final DeviceRepository deviceRepository;
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CabinService(CabinRepository cabinRepository, ProjectRepository projectRepository, DeviceRepository deviceRepository) {
        this.cabinRepository = cabinRepository;
        this.projectRepository = projectRepository;
        this.deviceRepository = deviceRepository;
    }

    public CabinEntity saveCabin(String projectId, CabinEntity cabinArgs) {
        if (StringUtils.isEmpty(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (cabinArgs == null) {
            throw new CustomizeException(NotificationMessage.CABIN_NOT_FOUND);
        }
        if (StringUtils.isEmpty(cabinArgs.getName())) {
            throw new CustomizeException(NotificationMessage.CABIN_NAME_NOT_FOUND);
        }
        if (hasCabinByName(cabinArgs.getName())) {
            throw new CustomizeException(NotificationMessage.CABIN_EXISTS);
        }
        CabinEntity cabinEntity = new CabinEntity();
        BeanUtils.copyProperties(cabinArgs, cabinEntity, "id", "createTime", "projectEntity", "cabinDetailEntities");
        cabinEntity.setProjectEntity(projectRepository.findOne(projectId));
        return cabinRepository.save(cabinEntity);
    }

    public void deleteCabin(String cabinId) {
        if (StringUtils.isEmpty(cabinId)) {
            throw new CustomizeException(NotificationMessage.CABIN_ID_NOT_FOUND);
        }
        cabinRepository.delete(cabinId);
    }

    public CabinEntity updateCabin(String cabinId, CabinEntity cabinArgs) {
        if (cabinArgs == null) {
            throw new CustomizeException(NotificationMessage.CABIN_NOT_FOUND);
        }
        if (StringUtils.isEmpty(cabinArgs.getName())) {
            throw new CustomizeException(NotificationMessage.CABIN_NAME_NOT_FOUND);
        }
        if (hasCabinByName(cabinArgs.getName())) {
            throw new CustomizeException(NotificationMessage.CABIN_EXISTS);
        }
        CabinEntity cabinEntity = getCabin(cabinId);
        cabinEntity.setName(cabinArgs.getName());
        return cabinRepository.save(cabinEntity);
    }

    public CabinEntity getCabin(String cabinId) {
        if (StringUtils.isEmpty(cabinId)) {
            throw new CustomizeException(NotificationMessage.CABIN_ID_NOT_FOUND);
        }
        return cabinRepository.findOne(cabinId);
    }

    public List<CabinEntity> getCabin() {
        return cabinRepository.findAll();
    }

    public synchronized CabinEntity putDevice(String cabinId, String deviceId, String name, int position) {
        if (StringUtils.isEmpty(name)) {
            throw new CustomizeException(NotificationMessage.CABIN_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(cabinId)) {
            throw new RuntimeException(NotificationMessage.CABIN_ID_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deviceId)) {
            throw new RuntimeException(NotificationMessage.DEVICE_IP_NOT_FOUND);
        }
        CabinEntity cabinEntity = getCabin(cabinId);
        List<CabinDetailEntity> cabinDetailEntityList = cabinEntity.getCabinDetailEntities() != null ? cabinEntity.getCabinDetailEntities() : new ArrayList<>();
        for (CabinDetailEntity cabinDetailEntity : cabinDetailEntityList) {
            // 已存在的设备
            if (cabinDetailEntity.getDeviceEntity().getId().equals(deviceId)) {
                cabinDetailEntity.setName(name);
                cabinDetailEntity.setPosition(position);
                return cabinRepository.save(cabinEntity);
            }
        }
        CabinDetailEntity cabinDetailEntity = new CabinDetailEntity();
        cabinDetailEntity.setName(name);
        cabinDetailEntity.setPosition(position);
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        if (deviceEntity == null) {
            throw new RuntimeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        cabinDetailEntity.setDeviceEntity(deviceEntity);
        cabinDetailEntityList.add(cabinDetailEntity);
        return cabinRepository.save(cabinEntity);
    }

    public CabinEntity deleteDevice(String cabinId, String deviceId) {
        CabinEntity cabinEntity = getCabin(cabinId);
        List<CabinDetailEntity> cabinDetailEntityList = cabinEntity.getCabinDetailEntities() != null ? cabinEntity.getCabinDetailEntities() : new Vector<>();
        CabinDetailEntity cabinDetailEntity = new CabinDetailEntity();
        cabinDetailEntity.setDeviceEntity(deviceRepository.findOne(deviceId));
        cabinDetailEntityList.remove(cabinDetailEntity);
        cabinEntity.setCabinDetailEntities(cabinDetailEntityList);
        return cabinRepository.save(cabinEntity);
    }

    public boolean hasCabinByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return cabinRepository.findByName(name) != null;
    }
}