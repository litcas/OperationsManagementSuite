package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Utils.ComponentUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ComponentService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private ComponentFileService componentFileService;
    @Autowired
    private ComponentUtils componentUtils;

    // 新建组件
    @Transactional
    public ComponentEntity saveComponent(UserEntity loginUser, ComponentEntity componentArgs, String[] addFilePath, MultipartFile[] multipartFiles) throws MissingServletRequestParameterException, IOException, NoSuchAlgorithmException {
        // 检查组件名称参数是否存在
        if (componentArgs.getName() == null) {
            logger.info("请求参数解析异常：component.name不存在，保存失败。");
            throw new MissingServletRequestParameterException("component.name", "String");
        }
        // 检查组件是否存在
        if (componentRepository.findByName(componentArgs.getName()).size() > 0) {
            logger.info("名称为：" + componentArgs.getName() + "的组件已存在，保存失败。");
            throw new DataIntegrityViolationException("名称为：" + componentArgs.getName() + "的组件已存在，保存失败。");
        }
        ComponentEntity componentEntity = new ComponentEntity();
        // 设置组件名称
        componentEntity.setName(componentArgs.getName());
        // 设置为最新版本
        componentEntity.setLatest(true);
        // 设置默认版本号
        componentEntity.setVersion("1.0");
        // 设置组件描述（非必须）
        if (componentArgs.getDescription() != null) {
            componentEntity.setDescription(componentArgs.getDescription());
        }
        // 设置组件文件关联
        componentEntity.setComponentFileEntities(componentFileService.addComponentFile(addFilePath, multipartFiles, componentEntity));
        // 设置组件的拥有者为当前登录用户
        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(loginUser);
        componentEntity.setUserEntities(userEntities);
        return componentRepository.save(componentEntity);
    }

    // 删除组件信息（取消最新版本的指针）
    @Transactional
    public ComponentEntity deleteComponent(String componentId) throws MissingServletRequestParameterException {
        if (componentId == null) {
            logger.info("请求参数不正确：component.id不存在，删除失败。");
            throw new MissingServletRequestParameterException("component.id", "String");
        }
        if (componentRepository.exists(componentId)) {
            logger.info("请求参数不正确：id为：" + componentId + "的组件不存在，删除失败。");
            throw new CustomizeException("请求参数不正确：id为：" + componentId + "的组件不存在，删除失败。");
        }
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        if (!componentEntity.isLatest()) {
            logger.info("请求参数不正确：id为：" + componentId + "的组件是历史版本或已被删除，删除失败。");
            throw new CustomizeException("请求参数不正确：id为：" + componentId + "的组件是历史版本或已被删除，删除失败。");
        }
        componentEntity.setLatest(false);
        return componentRepository.save(componentEntity);
    }

    // 更新组件信息
    @Transactional
    public ComponentEntity updategetComponents(String componentId) throws IOException {
        if (componentRepository.exists(componentId)) {
            logger.info("请求参数不正确：id = " + componentId + "的组件不存在，更新失败。");
            throw new CustomizeException("请求参数不正确：id = " + componentId + "的组件不存在，更新失败。");
        }
        // 查询需要修改的组件
        ComponentEntity modifyComponentEntity = componentRepository.findOne(componentId);
        // 查询需要修改的组件的最新版本号
        ComponentEntity latestComponentEntity = componentRepository.findByNameAndLatest(modifyComponentEntity.getName(), true);
        // 复制需要修改的组件对象
        ComponentEntity componentEntity = new ComponentEntity();
        BeanUtils.copyProperties(modifyComponentEntity, componentEntity);
        // 更新基础信息
        componentEntity.setId(UUID.randomUUID().toString());
        componentEntity.setCreateTime(new Date());
        componentEntity.setLatest(true);
        componentEntity.setVersion(componentUtils.versionUpdate(latestComponentEntity));
        File entityFile = new File(componentUtils.getLibraryPath(componentEntity));
        // 复制组件实体文件到新目录
        FileUtils.copyDirectory(new File(componentUtils.getLibraryPath(modifyComponentEntity)), entityFile);
        // 取消最新版本指针
        latestComponentEntity.setLatest(false);
//        componentRepository.save(latestComponentEntity);
        return componentEntity;
    }

    // 根据id查询组件信息
    public ComponentEntity getComponents(String componentId) throws MissingServletRequestParameterException {
        if (componentId == null) {
            logger.info("请求参数不正确：component.id不存在，查询失败。");
            throw new MissingServletRequestParameterException("component.id", "String");
        }
        return componentRepository.findOne(componentId);
    }

    // 查询所有组件
    public List<ComponentEntity> getComponents(ComponentEntity componentArgs) {
        return componentRepository.findAll(new Specification<ComponentEntity>() {
            @Override
            public Predicate toPredicate(Root<ComponentEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                if (componentArgs.getName() != null) {
                    predicateList.add(cb.like(root.get("name"), componentArgs.getName()));
                }
                predicateList.add(cb.equal(root.get("latest"), componentArgs.isLatest()));
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        });
    }
}
