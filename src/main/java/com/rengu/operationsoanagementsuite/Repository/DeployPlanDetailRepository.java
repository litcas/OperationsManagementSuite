package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployPlanDetailRepository extends JpaRepository<DeployPlanDetailEntity, String> {
    List<DeployPlanDetailEntity> findByDeployPlanEntityIdAndDeviceEntityId(String deployplanId, String deviceId);

    DeployPlanDetailEntity findByDeployPlanEntityIdAndDeviceEntityIdAndComponentEntityId(String deployplanId, String deviceId, String componentId);
}
