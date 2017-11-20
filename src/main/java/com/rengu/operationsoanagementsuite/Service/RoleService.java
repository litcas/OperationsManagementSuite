package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Utils.CustomizeException;
import com.rengu.operationsoanagementsuite.Utils.ResponseCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleService {
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
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return roleEntityList;
    }

    // 更具id查询角色信息
    @Transactional
    public RoleEntity getRole(String roleId) {
        RoleEntity roleEntity = roleRepository.findOne(roleId);
        if (roleEntity == null) {
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return roleEntity;
    }
}
