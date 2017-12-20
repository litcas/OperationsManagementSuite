package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
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
            logger.info(NotificationMessage.USER_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            logger.info(NotificationMessage.USER_NAME_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            logger.info(NotificationMessage.USER_PASSWORD_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_PASSWORD_NOT_FOUND);
        }
        RoleEntity roleEntity = roleService.getRoleByName(ServerConfiguration.USER_ROLE_NAME);
        userEntity.setRoleEntities(addRoles(userEntity, roleEntity));
        return userRepository.save(userEntity);
    }

    // 删除用户
    @Transactional
    public void deleteUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            logger.info(NotificationMessage.USER_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_ID_NOT_FOUND);
        }
        if (!userRepository.exists(userId)) {
            logger.info(NotificationMessage.USER_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        userRepository.delete(userId);
    }

    // 修改用户
    @Transactional
    public UserEntity updateUsers(String userId, UserEntity userArgs) {
        if (StringUtils.isEmpty(userId)) {
            logger.info(NotificationMessage.USER_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_ID_NOT_FOUND);
        }
        if (!userRepository.exists(userId)) {
            logger.info(NotificationMessage.USER_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findOne(userId);
        BeanUtils.copyProperties(userArgs, userEntity, "id", "createTime", "username", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "roleEntities");
        return userRepository.save(userEntity);
    }

    // 查看用户
    @Transactional
    public UserEntity getUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            logger.info(NotificationMessage.USER_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_ID_NOT_FOUND);
        }
        return userRepository.findOne(userId);
    }

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

    @Transactional
    public UserEntity assignRoleToUser(String userId, String roleId) {
        if (StringUtils.isEmpty(userId)) {
            logger.info(NotificationMessage.USER_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_ID_NOT_FOUND);
        }
        if (!userRepository.exists(userId)) {
            logger.info(NotificationMessage.USER_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        if (StringUtils.isEmpty(roleId)) {
            logger.info(NotificationMessage.ROLE_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.ROLE_ID_NOT_FOUND);
        }
        if (!roleRepository.exists(userId)) {
            logger.info(NotificationMessage.ROLE_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.ROLE_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findOne(userId);
        RoleEntity roleEntity = roleRepository.findOne(roleId);
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
}