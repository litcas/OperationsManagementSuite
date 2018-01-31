package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployLogRepository extends JpaRepository<DeployLogEntity, String> {
    DeployLogEntity findByDeployPlanEntityAndDeviceEntity(DeployPlanEntity deployPlanEntity, DeviceEntity deviceEntity);

    List<DeployLogEntity> findByProjectEntityId(String projectId);

    List<DeployLogEntity> findByDeployPlanEntityId(String deployPlanId);

    List<DeployLogEntity> findByDeviceEntityId(String deviceId);
}
