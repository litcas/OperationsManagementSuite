package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public RoleEntity saveRoles(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new CustomizeException(NotificationMessage.ROLE_NAME_NOT_FOUND);
        }
        if (roleRepository.findByName(name) != null) {
            throw new CustomizeException(NotificationMessage.ROLE_EXISTS);
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        return roleRepository.save(roleEntity);
    }

    @Transactional
    public RoleEntity getRoles(String roleId) {
        return roleRepository.findOne(roleId);
    }

    @Transactional
    public List<RoleEntity> getRoles(RoleEntity roleArgs) {
        return roleRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (roleArgs != null) {
                if (!StringUtils.isEmpty(roleArgs.getName())) {
                    predicateList.add(cb.like(root.get("name"), roleArgs.getName()));
                }
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Transactional
    public RoleEntity getRolesByName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new CustomizeException(NotificationMessage.ROLE_NAME_NOT_FOUND);
        }
        return roleRepository.findByName(name);
    }

    public boolean hasRoles(String roleId) {
        return roleRepository.exists(roleId);
    }
}
