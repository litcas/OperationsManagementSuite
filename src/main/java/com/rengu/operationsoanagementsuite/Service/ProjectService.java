package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
import com.rengu.operationsoanagementsuite.Task.AsyncTask;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DeploymentDesignService deploymentDesignService;
    private final DeploymentDesignSnapshotService deploymentDesignSnapshotService;

    @Autowired
    public DeviceRepository deviceRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, DeploymentDesignService deploymentDesignService, DeploymentDesignSnapshotService deploymentDesignSnapshotService) {
        this.projectRepository = projectRepository;
        this.deploymentDesignService = deploymentDesignService;
        this.deploymentDesignSnapshotService = deploymentDesignSnapshotService;
    }


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


    public void deleteProjects(String projectId) {
        if (!hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        // 删除设备
        for (DeviceEntity deviceEntity : deviceRepository.findByProjectEntityId(projectId)) {
            deploymentDesignService.deleteDeploymentDesignDetailsByDeviceId(deviceEntity.getId());
            deviceRepository.delete(deviceEntity.getId());
            // 从部署状态中移除设备部署信息
            AsyncTask.deployStatusEntities.removeIf(deployStatusEntity -> deviceEntity.getIp().equals(deployStatusEntity.getIp()));
        }
        // 删除部署设计
        for (DeploymentDesignEntity deploymentDesignEntity : deploymentDesignService.getDeploymentDesignsByProjectId(projectId)) {
            deploymentDesignService.deleteDeploymentDesign(deploymentDesignEntity.getId());
        }
        // 删除部署设计快照
        for (DeploymentDesignSnapshotEntity deploymentDesignSnapshotEntity : deploymentDesignSnapshotService.getDeploymentDesignSnapshotsByProjectId(projectId)) {
            deploymentDesignSnapshotService.deleteDeploymentDesignSnapshot(deploymentDesignSnapshotEntity.getId());
        }
        projectRepository.delete(projectId);
    }


    public ProjectEntity updateProjects(String projectId, ProjectEntity projectArgs) {
        if (!hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        BeanUtils.copyProperties(projectArgs, projectEntity, "id", "createTime", "name");
        return projectRepository.save(projectEntity);
    }


    public List<ProjectEntity> getProjects() {
        return projectRepository.findAll();
    }


    public ProjectEntity getProjects(String projectId) {
        return projectRepository.findOne(projectId);
    }


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
