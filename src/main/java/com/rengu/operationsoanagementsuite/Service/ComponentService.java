package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private ComponentFileService componentFileService;

    // 新建组件
    @Transactional
    public ComponentEntity saveComponent(UserEntity loginUser, ComponentEntity componentArgs, MultipartFile[] multipartFiles) throws MissingServletRequestParameterException, IOException, NoSuchAlgorithmException {
        // 检查组件名称参数是否存在
        if (componentArgs.getName() == null) {
            logger.info("请求参数解析异常：component.name不存在，保存失败。");
            throw new MissingServletRequestParameterException("component.name", "String");
        }
        // 检查组件是否存在
        if (hasComponent(componentArgs.getName())) {
            logger.info("名称为：" + componentArgs.getName() + "的组件已存在，保存失败。");
            throw new DataIntegrityViolationException("名称为：" + componentArgs.getName() + "的组件已存在，保存失败。");
        }
        // 检查上传文件对象是否存在
        if (multipartFiles == null) {
            logger.info("请求参数解析异常：multipartFiles，保存失败。");
            throw new MissingServletRequestParameterException("multipartFiles", "MultipartFile[]");
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
        // 设置组件的拥有者为当前登录用户
        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(loginUser);
        componentEntity.setUserEntities(userEntities);
        // 设置组件文件关联
        componentEntity.setComponentFileEntities(componentFileService.saveComponentFile(multipartFiles, componentEntity));
        return componentRepository.save(componentEntity);
    }

    // 检查是否存在该名称的组件
    private boolean hasComponent(String componentName) {
        List<ComponentEntity> componentEntityList = componentRepository.findByName(componentName);
        return componentEntityList.size() > 0;
    }

    // 查询所有组件
    public List<ComponentEntity> getComponents() {
        return componentRepository.findAll();
    }
}
