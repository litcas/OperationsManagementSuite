package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployLogRepository extends JpaRepository<DeployLogEntity, String> {
    List<DeployLogEntity> findByProjectEntityId(String projectId);
}
