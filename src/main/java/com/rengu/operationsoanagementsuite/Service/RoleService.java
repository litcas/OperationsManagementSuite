package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class RoleService {

    public static final String USER_ROLE_NAME = "ROLE_USER";
    public static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // 保存角色

    public RoleEntity saveRoles(RoleEntity roleEntity) {
        // 检查角色名称参数是否存在
        if (StringUtils.isEmpty(roleEntity.getName())) {
            throw new CustomizeException(NotificationMessage.ROLE_NAME_NOT_FOUND);
        }
        // 检查角色是否存在
        if (hasRole(roleEntity.getName())) {
            throw new CustomizeException(NotificationMessage.ROLE_EXISTS);
        }
        return roleRepository.save(roleEntity);
    }

    // 查询角色

    public RoleEntity getRoles(String name) {
        // 检查角色名称参数是否存在
        if (StringUtils.isEmpty(name)) {
            throw new CustomizeException(NotificationMessage.ROLE_NAME_NOT_FOUND);
        }
        // 检查角色是否存在
        if (!hasRole(name)) {
            throw new CustomizeException(NotificationMessage.ROLE_NOT_FOUND);
        }
        return roleRepository.findByName(name);
    }

    public boolean hasRole(String name) {
        return roleRepository.findByName(name) != null;
    }
}