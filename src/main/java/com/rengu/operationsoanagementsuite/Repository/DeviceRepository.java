package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, String>, JpaSpecificationExecutor<DeviceEntity> {
    List<DeviceEntity> findByProjectEntityId(String projectId);

    DeviceEntity findByProjectEntityIdAndIp(String projectId, String ip);
}