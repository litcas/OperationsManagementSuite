package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentDesignRepository extends JpaRepository<DeploymentDesignEntity, String>, JpaSpecificationExecutor<DeploymentDesignEntity> {
    DeploymentDesignEntity findByProjectEntityIdAndName(String projectId, String name);

    List<DeploymentDesignEntity> findByProjectEntityId(String projectId);
}
