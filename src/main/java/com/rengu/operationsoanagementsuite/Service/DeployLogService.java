package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Repository.DeployLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeployLogService {
    @Autowired
    private DeployLogRepository deployLogRepository;

    @Transactional
    public DeployLogEntity saveDeployLog(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity, ComponentEntity componentEntity) {
        DeployLogEntity deployLogEntity = deployLogRepository.findByDeployPlanEntityAndDeviceEntity(deployPlanEntity, deviceEntity);
        if (deployLogEntity == null) {
            deployLogEntity = new DeployLogEntity();
            deployLogEntity.setDeployPlanEntity(deployPlanEntity);
            deployLogEntity.setDeviceEntity(deviceEntity);
            deployLogEntity.setProjectEntity(deviceEntity.getProjectEntity());
        }
        deployLogEntity.setComponentEntities(addComponentEntity(deployLogEntity, componentEntity));
        deployLogEntity.setSize(getSize(deployLogEntity));
        deployLogEntity.setFileNums(getFileNum(deployLogEntity));
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public List<DeployLogEntity> getDeployLogs(String projectId) {
        return deployLogRepository.findByProjectEntityId(projectId);
    }

    @Transactional
    public DeployLogEntity getDeployLogs(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity) {
        return deployLogRepository.findByDeployPlanEntityAndDeviceEntity(deployPlanEntity, deviceEntity);
    }

    @Transactional
    public DeployLogEntity updateDeployLogsStarted(DeployLogEntity deployLogEntity, boolean isStarted) {
        deployLogEntity.setStarted(isStarted);
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public DeployLogEntity updateDeployLogsFinished(DeployLogEntity deployLogEntity, boolean isFinished) {
        deployLogEntity.setFinished(isFinished);
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public DeployLogEntity updateDeployLogsSpeedAndFinishedNums(DeployLogEntity deployLogEntity, double speed, int finishedNum, double remainingTime) {
        deployLogEntity.setSpeed(speed);
        deployLogEntity.setFinishedNums(finishedNum);
        deployLogEntity.setRemainingTime(remainingTime);
        return deployLogRepository.save(deployLogEntity);
    }

    public List<ComponentEntity> addComponentEntity(DeployLogEntity deployLogEntity, ComponentEntity componentArgs) {
        List<ComponentEntity> componentEntityList = deployLogEntity.getComponentEntities();
        if (componentEntityList == null) {
            componentEntityList = new ArrayList<>();
        }
        if (!componentEntityList.contains(componentArgs)) {
            componentEntityList.add(componentArgs);
        }
        return componentEntityList;
    }

    public long getSize(DeployLogEntity deployLogEntity) {
        long size = 0;
        for (ComponentEntity componentEntity : deployLogEntity.getComponentEntities()) {
            size = size + componentEntity.getSize();
        }
        return size;
    }

    public int getFileNum(DeployLogEntity deployLogEntity) {
        int fileNum = 0;
        for (ComponentEntity componentEntity : deployLogEntity.getComponentEntities()) {
            fileNum = fileNum + componentEntity.getComponentFileEntities().size();
        }
        return fileNum;
    }

    @Transactional
    public boolean hasDeployLog(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity) {
        return deployLogRepository.findByDeployPlanEntityAndDeviceEntity(deployPlanEntity, deviceEntity) != null;
    }

    public void deleteDeployLog(DeployPlanDetailEntity deployPlanDetailEntity) {
        DeployLogEntity deployLogEntity = deployLogRepository.findByDeployPlanEntityAndDeviceEntity(deployPlanDetailEntity.getDeployPlanEntity(), deployPlanDetailEntity.getDeviceEntity());
        deployLogEntity.getComponentEntities().remove(deployPlanDetailEntity.getComponentEntity());
        deployLogRepository.save(deployLogEntity);
        if (deployLogEntity.getComponentEntities().size() == 0) {
            deployLogRepository.delete(deployLogEntity);
        }
    }
}
