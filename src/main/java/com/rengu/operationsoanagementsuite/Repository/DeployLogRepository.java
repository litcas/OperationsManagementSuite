package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

@Repository
public interface DeployLogRepository extends JpaRepository<DeployLogEntity, String>, JpaSpecificationExecutor<DeployLogEntity> {

}
