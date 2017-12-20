package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProjectRepository projectRepository;

    // 保存工程
    @Transactional
    public ProjectEntity saveProjects(ProjectEntity projectEntity, UserEntity loginUser) {
        if (projectEntity == null) {
            logger.info(NotificationMessage.PROJECT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(projectEntity.getName())) {
            logger.info(NotificationMessage.PROJECT_NAME_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_NAME_NOT_FOUND);
        }
        // 检查该名称的工程是否已经存在
        if (hasProject(projectEntity.getName(), loginUser)) {
            logger.info(NotificationMessage.PROJECT_EXISTS);
            throw new CustomizeException(NotificationMessage.PROJECT_EXISTS);
        }
        projectEntity.setUserEntity(loginUser);
        return projectRepository.save(projectEntity);
    }

    // 删除工程
    @Transactional
    public void deleteProjects(String projectId) {
        if (StringUtils.isEmpty(projectId)) {
            logger.info(NotificationMessage.PROJECT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_ID_NOT_FOUND);
        }
        if (!projectRepository.exists(projectId)) {
            logger.info(NotificationMessage.PROJECT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        projectRepository.delete(projectId);
    }

    // 修改工程
    @Transactional
    public ProjectEntity updateProjects(String projectId, ProjectEntity projectArgs) {
        if (StringUtils.isEmpty(projectId)) {
            logger.info(NotificationMessage.PROJECT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_ID_NOT_FOUND);
        }
        if (!projectRepository.exists(projectId)) {
            logger.info(NotificationMessage.PROJECT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        BeanUtils.copyProperties(projectArgs, projectEntity, "id", "createTime", "userEntities", "deployPlanEntities");
        return projectRepository.save(projectEntity);
    }

    // 查询工程
    @Transactional
    public ProjectEntity getProject(String projectId) {
        if (StringUtils.isEmpty(projectId)) {
            logger.info(NotificationMessage.PROJECT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_ID_NOT_FOUND);
        }
        return projectRepository.findOne(projectId);
    }

    @Transactional
    public List<ProjectEntity> getProjects(UserEntity loginUser, ProjectEntity projectArgs) {
        return projectRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (projectArgs.getName() != null) {
                predicateList.add(cb.like(root.get("name"), projectArgs.getName()));
            }
            if (loginUser != null) {
                predicateList.add(cb.equal(root.get("userEntity").get("id"), loginUser.getId()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Transactional
    public List<ProjectEntity> getProjects(ProjectEntity projectArgs) {
        return getProjects(null, projectArgs);
    }

    // 查看工程是否存在
    private boolean hasProject(String name, UserEntity userEntity) {
        return projectRepository.findByNameAndUserEntity(name, userEntity) != null;
    }
}