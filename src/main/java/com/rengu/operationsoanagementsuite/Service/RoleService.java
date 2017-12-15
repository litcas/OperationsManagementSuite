package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleService {
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public List<RoleEntity> getRoles() {
        return roleRepository.findAll();
    }

    // 根据角色id查询角色信息
    @Transactional
    public RoleEntity getRoleById(String roleId) throws MissingServletRequestParameterException {
        if (roleId == null) {
            logger.info("请求参数解析异常：role.id不存在，查询失败。");
            throw new MissingServletRequestParameterException("role.id", "String");
        }
        return roleRepository.findOne(roleId);
    }

    // 根据角色名称查询角色信息
    @Transactional
    public RoleEntity getRoleByRole(String role) throws MissingServletRequestParameterException {
        if (role == null) {
            logger.info("请求参数解析异常：role.name不存在，查询失败。");
            throw new MissingServletRequestParameterException("role.name", "String");
        }
        return roleRepository.findByRole(role);
    }

    //保存角色信息
    @Transactional
    public RoleEntity saveRole(String role) throws MissingServletRequestParameterException {
        if (role == null) {
            logger.info("请求参数解析异常：role.name不存在，查询失败。");
            throw new MissingServletRequestParameterException("role.name", "String");
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(role);
        return roleRepository.save(roleEntity);
    }
}
