package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Utils.CustomizeException;
import com.rengu.operationsoanagementsuite.Utils.ResponseCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleService {
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // 查询所有角色信息
    @Transactional
    public List<RoleEntity> getRoles() {
        List<RoleEntity> roleEntityList = roleRepository.findAll();
        if (roleEntityList == null) {
            logger.info("查询角色信息失败");
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return roleEntityList;
    }

    // 根据角色id查询角色信息
    @Transactional
    public RoleEntity getRoleById(String roleId) {
        RoleEntity roleEntity = roleRepository.findOne(roleId);
        if (roleEntity == null) {
            logger.info("角色信息id = '" + roleId + "'查询失败");
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return roleEntity;
    }

    // 根据角色名称查询角色信息
    @Transactional
    public RoleEntity getRoleByRole(String role) {
        RoleEntity roleEntity = roleRepository.findByRole(role);
        if (roleEntity == null) {
            logger.info("角色信息名称 = '" + role + "'查询失败。");
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return roleEntity;
    }

    //保存角色信息
    @Transactional
    public RoleEntity saveRole(String role) {
        if (roleRepository.findByRole(role) != null) {
            logger.info("角色信息名称 = '" + role + "'已存在，保存失败。");
            throw new CustomizeException(ResponseCodeEnum.SAVEFAILED);
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(role);
        return roleRepository.save(roleEntity);
    }
}
