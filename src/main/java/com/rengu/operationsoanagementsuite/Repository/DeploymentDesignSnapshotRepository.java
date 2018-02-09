package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentDesignSnapshotRepository extends JpaRepository<DeploymentDesignSnapshotEntity, String>, JpaSpecificationExecutor<DeploymentDesignSnapshotEntity> {
    List<DeploymentDesignSnapshotEntity> findByProjectEntityId(String projectId);
}
