package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentDetailRepository extends JpaRepository<ComponentDetailEntity, String>, JpaSpecificationExecutor<ComponentDetailEntity> {
}
