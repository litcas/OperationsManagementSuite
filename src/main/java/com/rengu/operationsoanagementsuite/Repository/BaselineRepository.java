package com.rengu.operationsoanagementsuite.Repository;

import com.rengu.operationsoanagementsuite.Entity.BaselineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaselineRepository extends JpaRepository<BaselineEntity, String> {
    List<BaselineEntity> findByProjectEntityId(String projectId);
}
