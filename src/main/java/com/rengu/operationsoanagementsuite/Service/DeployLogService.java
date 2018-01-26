package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Repository.DeployLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DeployLogService {
    @Autowired
    private DeployLogRepository deployLogRepository;

    @Transactional
    public DeployLogEntity saveDeployLog(long fileNum, DeviceEntity deviceEntity) {
        DeployLogEntity deployLogEntity = new DeployLogEntity();
        deployLogEntity.setFileNum(fileNum);
        deployLogEntity.setDeviceEntity(deviceEntity);
        deployLogEntity.setProjectEntity(deviceEntity.getProjectEntity());
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public List<DeployLogEntity> getDeployLog(String projectId) {
        return deployLogRepository.findByProjectEntityId(projectId);
    }

    @Transactional
    public DeployLogEntity updateDeployLog(DeployLogEntity deployLogEntity, long finishNum) {
        deployLogEntity.setFinishedNum(finishNum);
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public DeployLogEntity updateDeployLog(DeployLogEntity deployLogEntity, boolean finished) {
        deployLogEntity.setFinished(finished);
        return deployLogRepository.save(deployLogEntity);
    }
}
