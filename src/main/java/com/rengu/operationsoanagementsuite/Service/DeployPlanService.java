package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanRepository;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
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
            logger.info("请求参数解析异常：project.id不存在，保存失败。");
            throw new CustomizeException("请求参数解析异常：project.id不存在，保存失败。");
        }
        if (!projectRepository.exists(projectId)) {
            logger.info("请求参数不正确：id为：" + projectId + "的工程不存在，保存失败。");
            throw new CustomizeException("请求参数不正确：id为：" + projectId + "的工程不存在，保存失败。");
        }
        if (deployPlanEntity == null) {
            logger.info("请求参数解析异常：deployPlan不存在，保存失败。");
            throw new CustomizeException("请求参数解析异常：deployPlan不存在，保存失败。");
        }
        if (StringUtils.isEmpty(deployPlanEntity.getName())) {
            logger.info("请求参数解析异常：deployPlanEntity.name不存在，保存失败。");
            throw new CustomizeException("请求参数解析异常：deployPlanEntity.name不存在，保存失败。");
        }
        if (hasDeployPlan(deployPlanEntity.getName(), projectRepository.findOne(projectId))) {
            logger.info("名称为：" + deployPlanEntity.getName() + "的部署设计已存在，保存失败。");
            throw new CustomizeException("名称为：" + deployPlanEntity.getName() + "的部署设计已存在，保存失败。");
        }
        deployPlanEntity.setProjectEntity(projectRepository.findOne(projectId));
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    @Transactional
    public void deleteDeployPlans(String deployplanId) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info("请求参数解析异常：deployplan.id不存在，删除失败。");
            throw new CustomizeException("请求参数解析异常：deployplan.id不存在，删除失败。");
        }
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info("请求参数不正确：id为：" + deployplanId + "的部署设计不存在，保存失败。");
            throw new CustomizeException("请求参数不正确：id为：" + deployplanId + "的部署设计不存在，保存失败。");
        }
        deployPlanRepository.delete(deployplanId);
    }

    @Transactional
    public DeployPlanEntity updateDeployPlans(String deployplanId, DeployPlanEntity deployPlanArgs) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info("请求参数解析异常：deployplan.id不存在，更新失败。");
            throw new CustomizeException("请求参数解析异常：deployplan.id不存在，更新失败。");
        }
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info("请求参数不正确：id为：" + deployplanId + "的部署设计不存在，更新失败。");
            throw new CustomizeException("请求参数不正确：id为：" + deployplanId + "的部署设计不存在，更新失败。");
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        BeanUtils.copyProperties(deployPlanArgs, deployPlanEntity, "id", "createTime", "lastModified", "deployPlanDetailEntity", "projectEntity");
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    @Transactional
    public DeployPlanEntity getDeployPlan(String deployplanId) {
        if (StringUtils.isEmpty(deployplanId)) {
            logger.info("请求参数解析异常：deployplan.id不存在，查询失败。");
            throw new CustomizeException("请求参数解析异常：deployplan.id不存在，查询失败。");
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
            logger.info("请求参数解析异常：deployplan.id不存在，更新失败。");
            throw new CustomizeException("请求参数解析异常：deployplan.id不存在，绑定失败。");
        }
        if (!deployPlanRepository.exists(deployplanId)) {
            logger.info("请求参数不正确：id为：" + deployplanId + "的部署设计不存在，更新失败。");
            throw new CustomizeException("请求参数不正确：id为：" + deployplanId + "的部署设计不存在，绑定失败。");
        }
        if (StringUtils.isEmpty(deviceId)) {
            logger.info("请求参数解析异常：device.id不存在，更新失败。");
            throw new CustomizeException("请求参数解析异常：deviceId.id不存在，绑定失败。");
        }
        if (!deviceRepository.exists(deviceId)) {
            logger.info("请求参数不正确：id为：" + deviceId + "的设备不存在，绑定失败。");
            throw new CustomizeException("请求参数不正确：id为：" + deviceId + "的设备不存在，绑定失败。");
        }
        if (StringUtils.isEmpty(componentId)) {
            logger.info("请求参数解析异常：component.id不存在，绑定失败。");
            throw new CustomizeException("请求参数解析异常：component.id不存在，绑定失败。");
        }
        if (!componentRepository.exists(componentId)) {
            logger.info("请求参数不正确：id为：" + componentId + "的组件不存在，更绑定失败。");
            throw new CustomizeException("请求参数不正确：id为：" + componentId + "的组件不存在，绑定失败。");
        }
        if (StringUtils.isEmpty(deployPath)) {
            logger.info("请求参数解析异常：deployPath不存在，绑定失败。");
            throw new CustomizeException("请求参数解析异常：deployPath不存在，绑定失败。");
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        DeployPlanDetailEntity deployPlanDetailEntity = deployPlanDetailService.saveDeployPlanDetails(deviceEntity, componentEntity, deployPath);
        deployPlanEntity.setDeployPlanDetailEntities(AddDeployPlanDetail(deployPlanEntity, deployPlanDetailEntity));
        return deployPlanRepository.save(deployPlanEntity);
    }

    private boolean hasDeployPlan(String name, ProjectEntity projectEntity) {
        return deployPlanRepository.findByNameAndProjectEntity(name, projectEntity) != null;
    }

    private List<DeployPlanDetailEntity> AddDeployPlanDetail(DeployPlanEntity deployPlanEntity, DeployPlanDetailEntity deployPlanDetailEntity) {
        List<DeployPlanDetailEntity> deployPlanDetailEntities = deployPlanEntity.getDeployPlanDetailEntities();
        if (deployPlanDetailEntities == null) {
            deployPlanDetailEntities = new ArrayList<>();
        }
        if (!deployPlanDetailEntities.contains(deployPlanDetailEntity)) {
            deployPlanDetailEntities.add(deployPlanDetailEntity);
        }
        return deployPlanDetailEntities;
    }
}
