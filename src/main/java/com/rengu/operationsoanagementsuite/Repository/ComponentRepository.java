package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<ComponentEntity, String>, JpaSpecificationExecutor<ComponentEntity> {
    ComponentEntity findByNameAndVersion(String name, String version);

    List<ComponentEntity> findByDeleted(boolean isDeleted);
}
