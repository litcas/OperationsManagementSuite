package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignSnapshotDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Repository.DeployLogRepository;
import com.rengu.operationsoanagementsuite.Utils.FormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
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
        String deployPath = (deviceEntity.getDeployPath() + componentEntity.getDeployPath()).replace("//", "/");
        return saveDeployLog(deviceEntity.getIp(), deployPath, componentEntity);
    }

    @Transactional
    public DeployLogEntity saveDeployLog(DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity, ComponentEntity componentEntity) {
        String deployPath = (deploymentDesignSnapshotDetailEntity.getDeployPath() + componentEntity.getDeployPath()).replace("//", "/");
        return saveDeployLog(deploymentDesignSnapshotDetailEntity.getIp(), deployPath, componentEntity);
    }

    @Transactional
    public DeployLogEntity saveDeployLog(String ip, String path, ComponentEntity componentEntity) {
        DeployLogEntity deployLogEntity = new DeployLogEntity();
        deployLogEntity.setCreateTime(new Date());
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
        deployLogEntity.setTime((deployLogEntity.getFinishTime().getTime() - deployLogEntity.getCreateTime().getTime()) / 1000);
        // kb/s
        deployLogEntity.setTransferRate(FormatUtils.doubleFormater((double) (sendSize / 1024) / deployLogEntity.getTime(), FormatUtils.doubleFormatPattern));
        deployLogEntity.setErrorFileNum(errorFileNum);
        deployLogEntity.setCompletedFileNum(completedFileNum);
        deployLogEntity.setState(state);
        return deployLogRepository.save(deployLogEntity);
    }

    @Transactional
    public List<DeployLogEntity> getDeployLogs(DeployLogEntity deployLogEntity) {
        return deployLogRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(deployLogEntity.getIp())) {
                predicateList.add(cb.like(root.get("ip").as(String.class), "%" + deployLogEntity.getIp() + "%"));
            }
            if (!StringUtils.isEmpty(deployLogEntity.getPath())) {
                predicateList.add(cb.like(root.get("path").as(String.class), "%" + deployLogEntity.getPath() + "%"));
            }
            if (!StringUtils.isEmpty(deployLogEntity.getComponentEntity().getName())) {
                predicateList.add(cb.like(root.get("componentEntity").get("name").as(String.class), "%" + deployLogEntity.getComponentEntity().getName() + "%"));
            }
            if (deployLogEntity.getCreateTime() != null || deployLogEntity.getFinishTime() != null) {
                if (deployLogEntity.getCreateTime() != null && deployLogEntity.getFinishTime() != null) {
                    predicateList.add(cb.between(root.get("createTime").as(Date.class), deployLogEntity.getCreateTime(), deployLogEntity.getFinishTime()));
                } else {
                    if (deployLogEntity.getCreateTime() != null) {
                        predicateList.add(cb.between(root.get("createTime").as(Date.class), deployLogEntity.getCreateTime(), new Date()));
                    }
                    if (deployLogEntity.getFinishTime() != null) {
                        predicateList.add(cb.between(root.get("createTime").as(Date.class), new Date(), deployLogEntity.getFinishTime()));
                    }
                }
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }
}