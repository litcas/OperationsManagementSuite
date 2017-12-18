package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ProjectEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserService userService;

    // 保存工程
    @Transactional
    public ProjectEntity saveProjects(ProjectEntity projectEntity, UserEntity loginUser) {
        if (StringUtils.isEmpty(projectEntity.getName())) {
            logger.info("请求参数解析异常：project.name不存在，保存失败。");
            throw new CustomizeException("请求参数解析异常：project.name不存在，保存失败。");
        }
        // 检查该名称的工程是否已经存在
        if (hasProject(projectEntity.getName())) {
            logger.info("名称为：" + projectEntity.getName() + "的工程已存在，保存失败。");
            throw new CustomizeException("名称为：" + projectEntity.getName() + "的工程已存在，保存失败。");
        }
        projectEntity.setUserEntities(projectUserInit(projectEntity, loginUser));
        return projectRepository.save(projectEntity);
    }

    // 删除工程
    @Transactional
    public void deleteProjects(String projectId) {
        if (StringUtils.isEmpty(projectId)) {
            logger.info("请求参数解析异常：projectId不存在，删除失败。");
            throw new CustomizeException("请求参数解析异常：projectId不存在，删除失败。");
        }
        if (!projectRepository.exists(projectId)) {
            logger.info("请求参数不正确：id为：" + projectId + "的工程不存在，删除失败。");
            throw new CustomizeException("请求参数不正确：id为：" + projectId + "的工程不存在，删除失败。");
        }
        projectRepository.delete(projectId);
    }

    // 查看工程是否存在
    private boolean hasProject(String name) {
        return projectRepository.findByName(name) != null;
    }

    // 初始化工程用户
    private List<UserEntity> projectUserInit(ProjectEntity projectEntity, UserEntity loginUser) {
        List<UserEntity> userEntityList = projectEntity.getUserEntities();
        if (userEntityList == null) {
            userEntityList = new ArrayList<>();
        }
        // 添加管理员用户
        if (!userEntityList.contains(userService.getAdminUser())) {
            userEntityList.add(userService.getAdminUser());
        }
        // 添加登录用户
        if (!userEntityList.contains(loginUser)) {
            userEntityList.add(loginUser);
        }
        return userEntityList;
    }
}