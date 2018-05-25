package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.CabinDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.CabinEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.CabinRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CabinService {

    private final CabinRepository cabinRepository;
    private final ProjectService projectService;
    private final DeviceService deviceService;

    @Autowired
    public CabinService(CabinRepository cabinRepository, ProjectService projectService, DeviceService deviceService) {
        this.cabinRepository = cabinRepository;
        this.projectService = projectService;
        this.deviceService = deviceService;
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
        cabinEntity.setProjectEntity(projectService.getProjects(projectId));
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

    public CabinEntity putDevice(String cabinId, String deviceId, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new CustomizeException(NotificationMessage.CABIN_NAME_NOT_FOUND);
        }
        CabinEntity cabinEntity = getCabin(cabinId);
        List<CabinDetailEntity> cabinDetailEntityList = cabinEntity.getCabinDetailEntities() != null ? cabinEntity.getCabinDetailEntities() : new ArrayList<>();
        CabinDetailEntity cabinDetailEntity = new CabinDetailEntity();
        cabinDetailEntity.setName(name);
        cabinDetailEntity.setDeviceEntity(deviceService.getDevices(deviceId));
        if (cabinDetailEntityList.contains(cabinDetailEntity)) {
            throw new CustomizeException("绑定失败,设备已存在");
        }
        cabinDetailEntityList.add(cabinDetailEntity);
        cabinEntity.setCabinDetailEntities(cabinDetailEntityList);
        return cabinRepository.save(cabinEntity);
    }

    public CabinEntity deleteDevice(String cabinId, String deviceId) {
        CabinEntity cabinEntity = getCabin(cabinId);
        List<CabinDetailEntity> cabinDetailEntityList = cabinEntity.getCabinDetailEntities() != null ? cabinEntity.getCabinDetailEntities() : new ArrayList<>();
        CabinDetailEntity cabinDetailEntity = new CabinDetailEntity();
        cabinDetailEntity.setDeviceEntity(deviceService.getDevices(deviceId));
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