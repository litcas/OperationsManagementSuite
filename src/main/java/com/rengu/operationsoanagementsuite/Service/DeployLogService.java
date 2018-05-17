package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
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
@Transactional
public class DeployLogService {

    public static final String FAIL_STATE = "0";
    public static final String PROCEED_STATE = "1";
    public static final String COMPLETE_STATE = "2";
    @Autowired
    private DeployLogRepository deployLogRepository;


    public DeployLogEntity saveDeployLog(DeviceEntity deviceEntity, ComponentEntity componentEntity) {
        String deployPath = FormatUtils.pathFormat(deviceEntity.getDeployPath() + componentEntity.getDeployPath());
        return saveDeployLog(deviceEntity.getIp(), deployPath, componentEntity);
    }


    public DeployLogEntity saveDeployLog(DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity, ComponentEntity componentEntity) {
        String deployPath = (deploymentDesignSnapshotDetailEntity.getDeployPath() + componentEntity.getDeployPath()).replace("//", "/");
        return saveDeployLog(deploymentDesignSnapshotDetailEntity.getIp(), deployPath, componentEntity);
    }


    public DeployLogEntity saveDeployLog(String ip, String path, ComponentEntity componentEntity) {
        DeployLogEntity deployLogEntity = new DeployLogEntity();
        deployLogEntity.setCreateTime(new Date());
        deployLogEntity.setState(PROCEED_STATE);
        deployLogEntity.setIp(ip);
        deployLogEntity.setPath(path);
        deployLogEntity.setComponentEntity(componentEntity);
        return deployLogRepository.save(deployLogEntity);
    }


    public DeployLogEntity updateDeployLog(DeployLogEntity deployLogEntity, List<DeployLogDetailEntity> errorFileList, List<DeployLogDetailEntity> CompletedFileList, long sendFileSize, String state) {
        deployLogEntity.setFinishTime(new Date());
        deployLogEntity.setErrorFileList(errorFileList);
        deployLogEntity.setCompletedFileList(CompletedFileList);
        deployLogEntity.setState(state);
        // 秒
        deployLogEntity.setTime((deployLogEntity.getFinishTime().getTime() - deployLogEntity.getCreateTime().getTime()) / 1000);
        // kb/s
        deployLogEntity.setTransferRate(deployLogEntity.getTime() == 0 ? 0 : FormatUtils.doubleFormater((double) (sendFileSize / 1024) / deployLogEntity.getTime(), FormatUtils.doubleFormatPattern));
        return deployLogRepository.save(deployLogEntity);
    }


    public List<DeployLogEntity> getDeployLogs(DeployLogEntity deployLogEntity, String componentName, String startTime, String endTime) {
        return deployLogRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(deployLogEntity.getIp())) {
                predicateList.add(cb.like(root.get("ip").as(String.class), "%" + deployLogEntity.getIp() + "%"));
            }
            if (!StringUtils.isEmpty(deployLogEntity.getPath())) {
                predicateList.add(cb.like(root.get("path").as(String.class), "%" + deployLogEntity.getPath() + "%"));
            }
            if (!StringUtils.isEmpty(deployLogEntity.getState())) {
                predicateList.add(cb.equal(root.get("state").as(String.class), deployLogEntity.getState()));
            }
            if (!StringUtils.isEmpty(componentName)) {
                predicateList.add(cb.like(root.get("componentEntity").get("name").as(String.class), "%" + componentName + "%"));
            }
            if (!StringUtils.isEmpty(startTime) || !StringUtils.isEmpty(endTime)) {
                if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                    predicateList.add(cb.between(root.get("createTime").as(Date.class), FormatUtils.dateFormat(Long.parseLong(startTime), "yyyy-MM-dd HH:mm:ss"), FormatUtils.dateFormat(Long.parseLong(endTime), "yyyy-MM-dd HH:mm:ss")));
                } else {
                    if (!StringUtils.isEmpty(startTime)) {
                        predicateList.add(cb.between(root.get("createTime").as(Date.class), FormatUtils.dateFormat(Long.parseLong(startTime), "yyyy-MM-dd HH:mm:ss"), new Date()));
                    }
                    if (!StringUtils.isEmpty(endTime)) {
                        predicateList.add(cb.between(root.get("createTime").as(Date.class), new Date(), FormatUtils.dateFormat(Long.parseLong(endTime), "yyyy-MM-dd HH:mm:ss")));
                    }
                }
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }
}