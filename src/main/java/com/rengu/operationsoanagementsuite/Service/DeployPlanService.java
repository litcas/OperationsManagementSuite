package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeployPlanEntity;
import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanRepository;
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

    private boolean hasDeployPlan(String name, ProjectEntity projectEntity) {
        return deployPlanRepository.findByNameAndProjectEntity(name, projectEntity) != null;
    }
}
