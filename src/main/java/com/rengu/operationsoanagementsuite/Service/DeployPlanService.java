package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanRepository;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
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
import java.util.Date;
import java.util.List;

@Service
public class DeployPlanService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeployPlanRepository deployPlanRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private DeployPlanDetailService deployPlanDetailService;

    @Transactional
    public DeployPlanEntity saveDeployPlans(String projectId, DeployPlanEntity deployPlanEntity) {
        if (StringUtils.isEmpty(projectId)) {
            logger.info(NotificationMessage.PROJECT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_ID_NOT_FOUND);
        }
        if (!projectRepository.exists(projectId)) {
            logger.info(NotificationMessage.PROJECT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (deployPlanEntity == null) {
            logger.info(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deployPlanEntity.getName())) {
            logger.info(NotificationMessage.DEPLOY_PLAN_NAME_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NAME_NOT_FOUND);
        }
        if (hasDeployPlan(deployPlanEntity.getName(), projectRepository.findOne(projectId))) {
            logger.info(NotificationMessage.DEPLOY_PLAN_EXISTS);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_EXISTS);
        }
        deployPlanEntity.setProjectEntity(projectRepository.findOne(projectId));
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    @Transactional
    public void deleteDeployPlans(String deployplanId) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
        }
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        deployPlanRepository.delete(deployplanId);
    }

    @Transactional
    public DeployPlanEntity updateDeployPlans(String deployplanId, DeployPlanEntity deployPlanArgs) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
        }
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        BeanUtils.copyProperties(deployPlanArgs, deployPlanEntity, "id", "createTime", "lastModified", "deployPlanDetailEntity", "projectEntity");
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    @Transactional
    public DeployPlanEntity getDeployPlan(String deployplanId) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
        }
        return deployPlanRepository.findOne(deployplanId);
    }

    @Transactional
    public List<DeployPlanEntity> getDeployPlans(String projectId, DeployPlanEntity deployPlanArgs) {
        return deployPlanRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(deployPlanArgs.getName())) {
                predicateList.add(cb.like(root.get("name"), deployPlanArgs.getName()));
            }
            if (!StringUtils.isEmpty(projectId)) {
                predicateList.add(cb.equal(root.get("projectEntity").get("id"), projectId));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Transactional
    public List<DeployPlanEntity> getDeployPlans(DeployPlanEntity deployPlanArgs) {
        return getDeployPlans(null, deployPlanArgs);
    }

    @Transactional
    public DeployPlanEntity AddDeployPlanDetail(String deployplanId, String deviceId, String componentId, String deployPath) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
        }
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_ID_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deviceId)) {
            logger.info(NotificationMessage.DEVICE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_ID_NOT_FOUND);
        }
        if (!deviceRepository.exists(deviceId)) {
            logger.info(NotificationMessage.DEVICE_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        if (StringUtils.isEmpty(componentId)) {
            logger.info(NotificationMessage.COMPONENT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_ID_NOT_FOUND);
        }
        if (!componentRepository.exists(componentId)) {
            logger.info(NotificationMessage.COMPONENT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deployPath)) {
            logger.info(NotificationMessage.DEPLOY_PLAN_DEPLOY_PATH_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DEPLOY_PATH_NOT_FOUND);
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        deployPlanEntity.setDeployPlanDetailEntities(deployPlanDetailService.createDeployPlanDetails(deployPlanEntity, deviceEntity, componentEntity, deployPath));
        return deployPlanRepository.save(deployPlanEntity);
    }

    private boolean hasDeployPlan(String name, ProjectEntity projectEntity) {
        return deployPlanRepository.findByNameAndProjectEntity(name, projectEntity) != null;
    }
}
