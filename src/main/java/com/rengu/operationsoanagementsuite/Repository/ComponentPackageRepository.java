package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.ComponentPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentPackageRepository extends JpaRepository<ComponentPackageEntity, String>, JpaSpecificationExecutor<ComponentPackageEntity> {
    ComponentPackageEntity findByNameAndVersion(String name, String version);
}
