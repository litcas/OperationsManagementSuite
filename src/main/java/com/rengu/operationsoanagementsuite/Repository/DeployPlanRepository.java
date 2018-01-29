package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployPlanRepository extends JpaRepository<DeployPlanEntity, String>, JpaSpecificationExecutor<DeployPlanEntity> {
    DeployPlanEntity findByNameAndProjectEntityId(String name, String projectId);
}
