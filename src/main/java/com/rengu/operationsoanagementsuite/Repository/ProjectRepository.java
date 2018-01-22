package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, String>, JpaSpecificationExecutor<ProjectEntity> {
    ProjectEntity findByNameAndUserEntity(String name, UserEntity userEntity);

    ProjectEntity findByName(String name);

    List<ProjectEntity> findByUserEntity(UserEntity userEntity);
}
