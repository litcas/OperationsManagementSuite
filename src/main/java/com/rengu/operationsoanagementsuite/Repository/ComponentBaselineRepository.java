package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.ComponentBaselineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentBaselineRepository extends JpaRepository<ComponentBaselineEntity, String>, JpaSpecificationExecutor<ComponentBaselineEntity> {
    ComponentBaselineEntity findByNameAndVersion(String name, String version);
}
