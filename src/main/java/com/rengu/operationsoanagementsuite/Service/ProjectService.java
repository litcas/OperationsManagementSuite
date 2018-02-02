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

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectEntity saveProjects(ProjectEntity projectArgs, UserEntity loginUser) {
        if (StringUtils.isEmpty(projectArgs.getName())) {
            throw new CustomizeException(NotificationMessage.PROJECT_NAME_NOT_FOUND);
        }
        if (hasName(projectArgs.getName(), loginUser)) {
            throw new CustomizeException(NotificationMessage.PROJECT_EXISTS);
        }
        projectArgs.setUserEntity(loginUser);
        return projectRepository.save(projectArgs);
    }

    @Transactional
    public void deleteProjects(String projectId) {
        if (!hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        projectRepository.delete(projectId);
    }

    @Transactional
    public ProjectEntity updateProjects(String projectId, ProjectEntity projectArgs) {
        if (!hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        BeanUtils.copyProperties(projectArgs, projectEntity, "id", "createTime", "name");
        return projectRepository.save(projectEntity);
    }

    @Transactional
    public List<ProjectEntity> getProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    public ProjectEntity getProjects(String projectId) {
        return projectRepository.findOne(projectId);
    }

    @Transactional
    public List<ProjectEntity> getProjects(UserEntity userEntity) {
        return projectRepository.findByUserEntity(userEntity);
    }

    public boolean hasName(String name, UserEntity userEntity) {
        return projectRepository.findByNameAndUserEntity(name, userEntity) != null;
    }

    public boolean hasProject(String projectId) {
        return projectRepository.exists(projectId);
    }
}
