package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentDesignDetailRepository extends JpaRepository<DeploymentDesignDetailEntity, String>, JpaSpecificationExecutor<DeploymentDesignDetailEntity> {
    List<DeploymentDesignDetailEntity> findByDeploymentDesignEntityId(String deploymentDesignId);

    List<DeploymentDesignDetailEntity> findByDeploymentDesignEntityIdAndDeviceEntityId(String deploymentDesignId, String deviceId);
}
