package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Repository.DeployLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DeployLogService {

    public static final int FAIL_STATE = 0;
    public static final int PROCEED_STATE = 1;
    public static final int COMPLETE_STATE = 2;
    @Autowired
    private DeployLogRepository deployLogRepository;

    @Transactional
    public DeployLogEntity saveDeployLog(DeviceEntity deviceEntity, ComponentEntity componentEntity) {
        return saveDeployLog(deviceEntity.getIp(), deviceEntity.getDeployPath(), componentEntity);
    }

    @Transactional
    public DeployLogEntity saveDeployLog(String ip, String path, ComponentEntity componentEntity) {
        DeployLogEntity deployLogEntity = new DeployLogEntity();
        deployLogEntity.setState(PROCEED_STATE);
        deployLogEntity.setIp(ip);
        deployLogEntity.setPath(path);
        deployLogEntity.setComponentEntity(componentEntity);
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public DeployLogEntity updateDeployLog(DeployLogEntity deployLogEntity, long sendSize, int errorFileNum, int completedFileNum, int state) {
        deployLogEntity.setFinishTime(new Date());
        // 秒
        deployLogEntity.setTime((deployLogEntity.getCreateTime().getTime() - deployLogEntity.getFinishTime().getTime()) / 1000);
        // mb/s
        deployLogEntity.setTransferRate((sendSize / 1024) / deployLogEntity.getTime());
        deployLogEntity.setErrorFileNum(errorFileNum);
        deployLogEntity.setCompletedFileNum(completedFileNum);
        deployLogEntity.setState(state);
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public List<DeployLogEntity> getDeployLogs() {
        return deployLogRepository.findAll();
    }
}