package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, String>, JpaSpecificationExecutor<ProjectEntity> {
    ProjectEntity findByName(String name);
}