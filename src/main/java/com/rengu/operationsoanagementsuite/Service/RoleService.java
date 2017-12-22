package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public RoleEntity saveRoles(String name) {
        if (StringUtils.isEmpty(name)) {
            logger.info(NotificationMessage.ROLE_NAME_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.ROLE_NAME_NOT_FOUND);
        }
        if (roleRepository.findByName(name) != null) {
            logger.info(NotificationMessage.ROLE_EXISTS);
            throw new CustomizeException(NotificationMessage.ROLE_EXISTS);
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        return roleRepository.save(roleEntity);
    }

    @Transactional
    public RoleEntity getRoleById(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            logger.info(NotificationMessage.ROLE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.ROLE_ID_NOT_FOUND);
        }
        return roleRepository.findOne(roleId);
    }

    @Transactional
    public List<RoleEntity> getRoles(RoleEntity roleArgs) {
        return roleRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(roleArgs.getName())) {
                predicateList.add(cb.like(root.get("name"), roleArgs.getName()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Transactional
    public RoleEntity getRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            logger.info(NotificationMessage.ROLE_NAME_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.ROLE_NAME_NOT_FOUND);
        }
        return roleRepository.findByName(name);
    }

    public List<RoleEntity> addRoles(UserEntity userEntity, RoleEntity... roleEntities) {
        List<RoleEntity> roleEntityList = userEntity.getRoleEntities();
        if (roleEntityList == null) {
            roleEntityList = new ArrayList<>();
        }
        for (RoleEntity roleEntity : roleEntities) {
            if (!roleEntityList.contains(roleEntity)) {
                roleEntityList.add(roleEntity);
            }
        }
        return roleEntityList;
    }
}
