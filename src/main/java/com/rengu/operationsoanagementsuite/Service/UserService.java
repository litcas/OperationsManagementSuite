package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(s);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Username Not Found");
        }
        return userEntity;
    }

    // 保存用户
    @Transactional
    public UserEntity saveUsers(UserEntity userEntity) {
        if (userEntity == null) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new CustomizeException(NotificationMessage.USER_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            throw new CustomizeException(NotificationMessage.USER_PASSWORD_NOT_FOUND);
        }
        if (hasUsers(userEntity.getUsername())) {
            throw new CustomizeException(NotificationMessage.USER_EXISTS);
        }
        RoleEntity roleEntity = roleService.getRolesByName(ServerConfiguration.USER_ROLE_NAME);
        userEntity.setRoleEntities(addRoles(userEntity, roleEntity));
        return userRepository.save(userEntity);
    }

    // 删除用户
    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.exists(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        userRepository.delete(userId);
    }

    // 修改用户
    @Transactional
    public UserEntity updateUsers(String userId, UserEntity userArgs) {
        if (!userRepository.exists(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findOne(userId);
        BeanUtils.copyProperties(userArgs, userEntity, "id", "createTime", "username", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "roleEntities");
        return userRepository.save(userEntity);
    }

    // 查看用户(Id)
    @Transactional
    public UserEntity getUsers(String userId) {
        return userRepository.findOne(userId);
    }

    // 查看所有
    @Transactional
    public List<UserEntity> getUsers(UserEntity userArgs) {
        return userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(userArgs.getUsername())) {
                predicateList.add(cb.like(root.get("username"), userArgs.getUsername()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    // 用户添加角色
    @Transactional
    public UserEntity assignRoleToUser(String userId, String roleId) {
        if (!userRepository.exists(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        if (!roleService.hasRoles(roleId)) {
            throw new CustomizeException(NotificationMessage.ROLE_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findOne(userId);
        RoleEntity roleEntity = roleService.getRoles(roleId);
        userEntity.setRoleEntities(addRoles(userEntity, roleEntity));
        return userRepository.save(userEntity);
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

    private boolean hasUsers(String username) {
        return userRepository.findByUsername(username) != null;
    }
}