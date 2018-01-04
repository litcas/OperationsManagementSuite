package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
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

    @Autowired
    private ProjectRepository projectRepository;

    // 保存工程
    @Transactional
    public ProjectEntity saveProjects(ProjectEntity projectEntity, UserEntity loginUser) {
        if (projectEntity == null) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(projectEntity.getName())) {
            throw new CustomizeException(NotificationMessage.PROJECT_NAME_NOT_FOUND);
        }
        // 检查该名称的工程是否已经存在
        if (hasProject(projectEntity.getName(), loginUser)) {
            throw new CustomizeException(NotificationMessage.PROJECT_EXISTS);
        }
        projectEntity.setUserEntity(loginUser);
        return projectRepository.save(projectEntity);
    }

    // 删除工程
    @Transactional
    public void deleteProjects(String projectId) {
        if (!projectRepository.exists(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        projectRepository.delete(projectId);
    }

    // 修改工程
    @Transactional
    public ProjectEntity updateProjects(String projectId, ProjectEntity projectArgs) {
        if (!projectRepository.exists(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        BeanUtils.copyProperties(projectArgs, projectEntity, "id", "createTime", "userEntities", "deployPlanEntities");
        return projectRepository.save(projectEntity);
    }

    // 查询工程
    @Transactional
    public ProjectEntity getProject(String projectId) {
        return projectRepository.findOne(projectId);
    }

    // 查询全部工程
    @Transactional
    public List<ProjectEntity> getProjects(UserEntity loginUser, ProjectEntity projectArgs) {
        return projectRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (loginUser != null) {
                predicateList.add(cb.equal(root.get("userEntity").get("id"), loginUser.getId()));
            }
            if (projectArgs != null) {
                if (projectArgs.getName() != null) {
                    predicateList.add(cb.like(root.get("name"), projectArgs.getName()));
                }
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    // 查询全部工程（管理员）
    @Transactional
    public List<ProjectEntity> getProjects(ProjectEntity projectArgs) {
        return getProjects(null, projectArgs);
    }

    // 查看工程是否存在
    public boolean hasProject(String projectId) {
        return projectRepository.exists(projectId);
    }

    // 查看工程是否存在
    private boolean hasProject(String name, UserEntity userEntity) {
        return projectRepository.findByNameAndUserEntity(name, userEntity) != null;
    }
}