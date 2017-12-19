package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeployPlanRepository extends JpaRepository<DeployPlanEntity, String>, JpaSpecificationExecutor<DeployPlanEntity> {
    DeployPlanEntity findByNameAndProjectEntity(String name, ProjectEntity projectEntity);
}
